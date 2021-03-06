/**
 *  Copyright 2013 Giwi Softwares (http://giwi.free.fr)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.giwi.camel.dav.test;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DavChangedReadLockTest.
 */
public class DavChangedReadLockTest extends AbstractDavTest {

    /** The Constant LOG. */
    private static final transient Logger LOG = LoggerFactory
	    .getLogger(DavChangedReadLockTest.class);

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL
		+ "/changed?readLock=changed&readLockCheckInterval=1000&delete=true";
    }

    /**
     * Test changed read lock.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testChangedReadLock() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);
	mock.expectedFileExists("tmpOut/changed/out/slowfile.dat");

	writeSlowFile();

	assertMockEndpointsSatisfied();

	String content = context.getTypeConverter().convertTo(String.class,
		new File("tmpOut/changed/out/slowfile.dat"));
	String[] lines = content.split(LS);
	assertEquals("There should be 20 lines in the file", 20, lines.length);
	for (int i = 0; i < 20; i++) {
	    assertEquals("Line " + i, lines[i]);
	}
    }

    /**
     * Write slow file.
     * 
     * @throws Exception
     *             the exception
     */
    private void writeSlowFile() throws Exception {
	LOG.debug("Writing slow file...");

	createDirectory(DAV_ROOT_DIR + "/changed");
	FileOutputStream fos = new FileOutputStream(DAV_ROOT_DIR
		+ "/changed/slowfile.dat", true);
	for (int i = 0; i < 20; i++) {
	    fos.write(("Line " + i + LS).getBytes());
	    LOG.debug("Writing line " + i);
	    Thread.sleep(200);
	}

	fos.flush();
	fos.close();
	LOG.debug("Writing slow file DONE...");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).to("file:tmpOut/changed/out", "mock:result");
	    }
	};
    }

}
