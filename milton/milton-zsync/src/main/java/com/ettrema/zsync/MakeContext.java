/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package com.ettrema.zsync;

/**
 * Holds working variables used when applying deltas
 *
 * 
 */
public class MakeContext {
	final ChainingHash hashtable; 
	final long[] fileMap; 
	long fileOffset; 
	boolean rangeQueue;

	public MakeContext(ChainingHash hashtable, long[] fileMap) {
		this.hashtable = hashtable;
		this.fileMap = fileMap;
	}
	
	
	public void put(int blockIndex, long offset){
		
		fileMap[blockIndex] = offset;
	}
	
	public void delete(ChecksumPair key){
		
		hashtable.delete(key);
	}
	
	public boolean matched(int blockIndex) {
		
		return fileMap[blockIndex] > -1;
	}
	
	public void removematch(int blockIndex) {

		fileMap[blockIndex] = -1;
	}
	
	public int blockcount() {
		
		return fileMap.length;
	}
}
