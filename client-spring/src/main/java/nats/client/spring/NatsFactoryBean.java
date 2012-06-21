/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package nats.client.spring;

import nats.NatsLogger;
import nats.client.ExceptionHandler;
import nats.client.Nats;
import org.jboss.netty.channel.ChannelFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NatsFactoryBean implements FactoryBean<Nats>, DisposableBean {

	private Nats nats;

	private Collection<String> hostUris;
	private boolean autoReconnect = true;
	private ChannelFactory channelFactory;
	private ExceptionHandler exceptionHandler;
	private NatsLogger logger;
	private int maxReconnectAttempts = -1;
	private long reconnectWaitTime = -1;

	@Override
	public Nats getObject() throws Exception {
		if (nats != null) {
			return nats;
		}
		final Nats.Builder builder = new Nats.Builder();
		if (hostUris == null) {
			throw new IllegalStateException("At least one host URI must be provided.");
		}
		for (String uri : hostUris) {
			builder.addHost(uri);
		}
		builder.automaticReconnect(autoReconnect);
		if (channelFactory != null) {
			builder.channelFactory(channelFactory);
		}
		if (exceptionHandler != null) {
			builder.exceptionHandler(exceptionHandler);
		}
		if (logger != null) {
			builder.logger(logger);
		}
		if (maxReconnectAttempts > 0) {
			builder.maxReconnectAttempts(maxReconnectAttempts);
		}
		if (reconnectWaitTime >= 0) {
			builder.reconnectWaitTime(reconnectWaitTime, TimeUnit.MILLISECONDS);
		}
		nats = builder.connect();
		return nats;
	}

	@Override
	public Class<?> getObjectType() {
		return nats == null ? Nats.class : nats.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		if (nats != null) {
			nats.close();
		}
	}

	public void setHostUris(Collection<String> hostUris) {
		this.hostUris = new ArrayList<String>(hostUris);
	}

	public void setAutoReconnect(boolean autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	public void setChannelFactory(ChannelFactory channelFactory) {
		this.channelFactory = channelFactory;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public void setLogger(NatsLogger logger) {
		this.logger = logger;
	}

	public void setMaxReconnectAttempts(int maxReconnectAttempts) {
		this.maxReconnectAttempts = maxReconnectAttempts;
	}

	public void setReconnectWaitTime(long reconnectWaitTime) {
		this.reconnectWaitTime = reconnectWaitTime;
	}

}