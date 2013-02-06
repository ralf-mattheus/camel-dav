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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to verify FTP filter option.
 */
public class FromDavRemoteFileFilterDirectoryTest extends AbstractDavTest {

	private String getDavUrl() {
		return DAV_URL + "/filefilter?recursive=true&filter=#myFilter";
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		JndiRegistry jndi = super.createRegistry();
		jndi.bind("myFilter", new MyFileFilter<Object>());
		return jndi;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		prepareDavServer();
	}

	@Test
	public void testDavFilter() throws Exception {
		if (isPlatform("aix")) {
			// skip testing on AIX as it have an issue with this test with the file filter
			return;
		}

		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived("Hello World");

		mock.assertIsSatisfied();
	}

	private void prepareDavServer() throws Exception {
		// prepares the FTP Server by creating files on the server that we want to unit
		// test that we can pool
		sendFile(getDavUrl(), "This is a file to be filtered", "skipDir/skipme.txt");
		sendFile(getDavUrl(), "This is a file to be filtered", "skipDir2/skipme.txt");
		sendFile(getDavUrl(), "Hello World", "okDir/hello.txt");
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(getDavUrl()).convertBodyTo(String.class).to("mock:result");
			}
		};
	}

	// START SNIPPET: e1
	public class MyFileFilter<T> implements GenericFileFilter<T> {

		@Override
		public boolean accept(GenericFile<T> file) {
			// we dont accept any files within directory starting with skip in the name
			if (file.isDirectory() && file.getFileName().startsWith("skip")) {
				return false;
			}

			return true;
		}
	}
	// END SNIPPET: e1
}