/*******************************************************************************
 *   BSD License
 *    
 *   Copyright (c) 2017, AT&T Intellectual Property.  All other rights reserved.
 *    
 *   Redistribution and use in source and binary forms, with or without modification, are permitted
 *   provided that the following conditions are met:
 *    
 *   1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. All advertising materials mentioning features or use of this software must display the
 *      following acknowledgement:  This product includes software developed by the AT&T.
 *   4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *      promote products derived from this software without specific prior written permission.
 *    
 *   THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 *   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *   SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 *******************************************************************************/
package com.att.ajsc.common.restlet.interceptors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.camel.component.restlet.RestletEndpoint;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.att.ajsc.common.Interceptor;
import com.att.ajsc.common.PreInterceptor6;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PreInterceptorExceptionTest.configuration.class })
public class PreInterceptorExceptionTest {

	@Autowired
	private ApplicationContext context;
	private com.att.ajsc.common.Exchange exchange;
	private MockEnvironment mockEnvironment;
	private Message inMessage;

	@Configuration
	static class configuration {

		@Bean
		public PreInterceptor6 getPreInterceptor() {
			PreInterceptor6 interceptor6 = spy(new PreInterceptor6());
			return interceptor6;
		}

	}

	@Before
	public void init() throws Exception {

		mockEnvironment = new MockEnvironment();
		inMessage = new DefaultMessage();
		Message outMessage = new DefaultMessage();
		exchange = new com.att.ajsc.common.Exchange();
		inMessage.setHeader(Exchange.HTTP_URI, "/path");
		exchange.setOut(outMessage);
		exchange.setFromEndpoint(new RestletEndpoint(null, null));
	}

	@Test
	public void RestletEndPointErrorTest() throws Exception {

		exchange.setIn(inMessage);

		org.restlet.Request request = new org.restlet.Request();
		request.setHostRef("");
		org.restlet.Response response = new org.restlet.Response(request);
		exchange.getIn().setHeader(RestletConstants.RESTLET_RESPONSE, response);
		exchange.getIn().setHeader(RestletConstants.RESTLET_REQUEST, request);
		exchange.setFromEndpoint(new RestletEndpoint(null, null));
		Interceptor interceptor = new Interceptor();
		interceptor.setContext(context);
		interceptor.setEnvironment(mockEnvironment);
		interceptor.invokePreInterceptorChain(exchange);

		assertEquals(((org.restlet.Response) exchange.getOut().getBody()).getStatus().getCode(), 500);

	}

}
