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

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.giwi.camel.dav.RemoteFile;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test CAMEL-1247.
 * 
 * @version
 */
public class DavConsumerWithNoFileOptionTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "?consumer.delay=5000";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();
	prepareDavServer();
    }

    /**
     * Test with no file in option.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testWithNoFileInOption() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceived("Hello World");

	assertMockEndpointsSatisfied();

	Exchange exchange = mock.getExchanges().get(0);
	RemoteFile<?> file = (RemoteFile<?>) exchange
		.getProperty(FileComponent.FILE_EXCHANGE_FILE);
	assertNotNull(file);
	assertEquals("webdav/hello.txt", file.getAbsoluteFilePath());
	assertEquals("hello.txt", file.getRelativeFilePath());
	assertEquals("hello.txt", file.getFileName());
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	sendFile(getDavUrl(), "Hello World", "hello.txt");
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
		from(getDavUrl()).to("mock:result");
	    }
	};
    }
}