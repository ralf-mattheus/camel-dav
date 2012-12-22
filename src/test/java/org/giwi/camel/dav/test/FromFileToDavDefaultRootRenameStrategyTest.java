/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.giwi.camel.dav.test;

import java.io.File;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @version
 */
public class FromFileToDavDefaultRootRenameStrategyTest extends CamelTestSupport {
	protected static final String DAV_ROOT_DIR = "/var/www/web1/web";

	/*
	 * This is our poll we want to test (no folder specified). Uses the rename strategy
	 */
	private String getFtpPollingUrl() {
		return "dav://localhost:80/webdav?delete=true&delay=1000&initialDelay=1500&readLock=rename";
	}

	@Test
	public void testFromFileToDav() throws Exception {
		File expectedOnDavServer = new File(DAV_ROOT_DIR + "/logo.jpg");
		// the poller won't start for 1.5 seconds, so we check to make sure the file
		// is there first check 1 - is the file there (default root location)
		assertTrue(expectedOnDavServer.exists());

		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(1);

		assertMockEndpointsSatisfied();

		// give our mock a chance to delete the file
		Thread.sleep(250);

		// assert the file is NOT there now
		assertTrue(!expectedOnDavServer.exists());
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(getFtpPollingUrl()).to("mock:result");
			}
		};
	}

}