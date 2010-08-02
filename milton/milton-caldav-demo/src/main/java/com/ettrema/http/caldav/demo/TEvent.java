package com.ettrema.http.caldav.demo;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.http.ICalResource;
import com.ettrema.http.acl.Principal;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class TEvent extends TResource implements ICalResource {

    private static final Logger log = LoggerFactory.getLogger( TEvent.class );
    private String iCalData;

    public TEvent( TCalendarResource parent, String name ) {
        super( parent, name );
    }

    @Override
    protected Object clone( TFolderResource newParent ) {
        TEvent e = new TEvent( (TCalendarResource) newParent, name );
        e.setiCalData( iCalData );
        return e;
    }

    public void sendContent( OutputStream out, Range range, Map<String, String> params, String contentType ) throws IOException, NotAuthorizedException, BadRequestException {
        out.write( iCalData.getBytes() );
    }

    public String getContentType( String accepts ) {
        return "text/calendar";
    }

    public String getICalData() {
        return iCalData;
    }

    public void setiCalData( String iCalData ) {
        this.iCalData = iCalData;
    }

    public Map<Principal, List<Priviledge>> getAccessControlList() {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Priviledge> getPriviledges(Auth auth) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPriviledges(Principal principal, boolean isGrantOrDeny, List<Priviledge> privs) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
}
