package com.ettrema.zsync;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.DigestResource;
import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PostableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.ReplaceableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import com.bradmcevoy.io.BufferingOutputStream;
import com.bradmcevoy.io.StreamUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resource factory allows resouces to be retrieved and updated using
 * the zsync protocol.
 * 
 * Client side process for updating a local file from a server file
 * a) assume the remote file is at path /somefile
 * b) retrieve zsync metadata (ie headers and checksums)
 *		GET /somefile/.zsync
 * c) implement rolling checksums and retrieve ranges of real file as needed with partial GETs
 *		GET /somefile
 *		Ranges: x-y, n-m, etc
 * d) merge the partial ranges
 * 
 * 
 * Client side process for updating a server file with a local file
 * a) assume the remote file is at path /somefile
 * b) Find the data ranges to update by POSTing local metadata (headers+checksums)
 *		PUT /somefile/.zsync
 *		Version: zsync-1.0.0
 *		Blocksize: 256
 * 
 *      (eg response)
 *		1222-1756
 *		20000-20512
 * c) Upload the metadata again and the checksums in a PUT
 *		
 *
 * @author brad
 */
public class ZSyncResourceFactory implements ResourceFactory {

	private static final Logger log = LoggerFactory.getLogger(ZSyncResourceFactory.class);
	private String suffix = ".zsync";
	private final ResourceFactory wrapped;
	private MetaStore metaStore;
	private MetaFileMaker metaFileMaker;
	private FileMaker fileMaker;
	private int defaultBlockSize = 512;
	private int maxMemorySize = 100000;

	public ZSyncResourceFactory(ResourceFactory wrapped) {
		this.wrapped = wrapped;
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		this.metaStore = new FileMetaStore(tempDir);
		metaFileMaker = new MetaFileMaker();
		fileMaker = new FileMaker();
	}

	@Override
	public Resource getResource(String host, String path) {
		if (path.endsWith("/" + suffix)) {
			Path p = Path.path(path);
			String realPath = p.getParent().toString();
			Resource r = wrapped.getResource(host, realPath);
			if (r == null) {
				return null;
			} else {
				if (r instanceof GetableResource) {
					return new ZSyncAdapterResource((GetableResource) r, realPath, host);
				} else {
					return null;
				}
			}
		} else {
			return wrapped.getResource(host, path);
		}
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public ResourceFactory getWrapped() {
		return wrapped;
	}

	public class ZSyncAdapterResource implements PostableResource, GetableResource, ReplaceableResource, DigestResource {

		private final GetableResource r;
		private final String realPath;
		private final String host;
		/**
		 * populated on POST, then used in sendContent
		 */
		private List<Range> ranges;

		public ZSyncAdapterResource(GetableResource r, String realPath, String host) {
			this.r = r;
			this.realPath = realPath;
			this.host = host;
		}

		@Override
		public String processForm(Map<String, String> parameters, Map<String, FileItem> files) throws BadRequestException, NotAuthorizedException, ConflictException {
			System.out.println("processForm: parameters: " + parameters + " files: " + files);

			if (files.isEmpty()) {
				log.warn("No meta file provided");
				throw new BadRequestException(r);
			} else {
				try {
					FileItem item = files.values().iterator().next();

					File metaFile = metaStore.storeMetaData(r, item.getInputStream());

					// copy content to a file
					File tempData = File.createTempFile("milton-zsync", null);
					FileOutputStream fDataOut = new FileOutputStream(tempData);
					r.sendContent(fDataOut, null, null, null);
					fDataOut.close();

					// build the list of required ranges
					ranges = fileMaker.findMissingRanges(tempData, metaFile);

				} catch (IOException ex) {
					throw new RuntimeException(ex);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
			return null;
		}

		@Override
		public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException {
			if (ranges != null) {
				log.info("sendContent: sending range data");
				sendRangeData(out);
			} else {
				log.info("sendContent: sending meta data");
				sendMetaData(params, contentType, out);
			}

		}

		private void sendMetaData(Map<String, String> params, String contentType, OutputStream out) throws RuntimeException {
			Long fileLength = r.getContentLength();
			int blocksize = defaultBlockSize;
			if (fileLength != null) {
				blocksize = metaFileMaker.computeBlockSize(fileLength);
			}

			MetaFileMaker.MetaData metaData;
			if (r instanceof ZSyncResource) {
				ZSyncResource zr = (ZSyncResource) r;
				metaData = zr.getZSyncMetaData();
			} else {
				BufferingOutputStream bufOut = new BufferingOutputStream(maxMemorySize);
				try {
					r.sendContent(bufOut, null, params, contentType);
					bufOut.flush();
				} catch (Exception ex) {
					bufOut.deleteTempFileIfExists();
					throw new RuntimeException(ex);
				} finally {
					StreamUtils.close(bufOut);
				}
				InputStream in = bufOut.getInputStream();
				try {
					metaData = metaFileMaker.make(realPath, blocksize, fileLength, r.getModifiedDate(), in);
				} finally {
					StreamUtils.close(in);
				}
			}
			metaFileMaker.write(metaData, out);
		}

		@Override
		public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
			log.trace("replaceContent: bytes: " + length);
			try {
				File metaFile = metaStore.getMetaData(r);
				if (metaFile == null) {
					throw new BadRequestException(r, "No previous metadata was found for this version of this file");
				}

				// save new data to a temp file
				File tempNewData = File.createTempFile("milton-zsync", "newdata");
				FileOutputStream fout = new FileOutputStream(tempNewData);
				StreamUtils.readTo(in, fout);
				StreamUtils.close(fout);

				log.trace("saved data from client to: " + tempNewData.getAbsolutePath());

				// save current version to a temp file
				File tempOldData = File.createTempFile("milton-zsync", "olddata");
				fout = new FileOutputStream(tempOldData);
				r.sendContent(fout, null, null, null);
				StreamUtils.close(fout);
				log.trace("saved current server file to: " + tempOldData.getAbsolutePath());

				// merge old data, new data and the metadata
				File mergedFile;
				FileInputStream fin = null;
				try {
					fin = new FileInputStream(tempNewData);
					RangeLoader rangeLoader = new PreChunkedRangeLoader(fin);
					mergedFile = fileMaker.make(tempOldData, metaFile, rangeLoader);
					log.trace("merged old and new data to: " + mergedFile.getAbsolutePath());
				} finally {
					StreamUtils.close(fin);
				}

				// Now do the actual replace
				updateResourceContentActual(mergedFile);

			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

		}

		@Override
		public Long getMaxAgeSeconds(Auth auth) {
			return null;
		}

		@Override
		public String getContentType(String accepts) {
			return "application/zsyncM";
		}

		@Override
		public Long getContentLength() {
			return null;
		}

		@Override
		public String getUniqueId() {
			return null;
		}

		@Override
		public String getName() {
			return suffix;
		}

		@Override
		public Object authenticate(String user, String password) {
			return r.authenticate(user, password);
		}

		@Override
		public boolean authorise(Request request, Method method, Auth auth) {
			return r.authorise(request, method, auth);
		}

		@Override
		public String getRealm() {
			return r.getRealm();
		}

		@Override
		public Date getModifiedDate() {
			return r.getModifiedDate();
		}

		@Override
		public String checkRedirect(Request request) {
			return null;
		}

		@Override
		public Object authenticate(DigestResponse digestRequest) {
			return ((DigestResource) r).authenticate(digestRequest);
		}

		@Override
		public boolean isDigestAllowed() {
			return (r instanceof DigestResource) && ((DigestResource) r).isDigestAllowed();
		}

		private void sendRangeData(OutputStream out) {
			PrintWriter pw = new PrintWriter(out);
			for (Range range : ranges) {
				pw.println(range.getRange());
			}
			pw.flush();
		}

		private void updateResourceContentActual(File mergedFile) throws FileNotFoundException, BadRequestException, ConflictException, NotAuthorizedException, IOException {
			if (r instanceof ReplaceableResource) {
				log.trace("updateResourceContentActual: " + mergedFile.getAbsolutePath() + ", resource is replaceable");
				FileInputStream fin = null;
				try {
					fin = new FileInputStream(mergedFile);
					ReplaceableResource rr = (ReplaceableResource) r;
					rr.replaceContent(fin, mergedFile.length());
				} finally {
					StreamUtils.close(fin);
				}
			} else {
				log.trace("updateResourceContentActual: " + mergedFile.getAbsolutePath() + ", resource is NOT replaceable, try to replace through parent");
				String parentPath = Path.path(realPath).getParent().toString();
				Resource rParent = wrapped.getResource(host, parentPath);
				if (rParent == null) {
					throw new RuntimeException("Failed to locate parent resource to update contents. parent: " + parentPath + " host: " + host);
				}
				if (rParent instanceof PutableResource) {
					log.trace("found parent resource, implements PutableResource");
					FileInputStream fin = null;
					try {
						fin = new FileInputStream(mergedFile);
						PutableResource putable = (PutableResource) rParent;
						putable.createNew(r.getName(), fin, mergedFile.length(), r.getContentType(null));
					} finally {
						StreamUtils.close(fin);
					}
				} else {
					throw new RuntimeException("Tried to update non-replaceable resource by doing createNew on parent, but the parent doesnt implement PutableResource. parent path: " + parentPath + " host: " + host + " parent type: " + rParent.getClass());
				}
			}


		}
	}
}
