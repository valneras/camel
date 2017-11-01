/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.yql;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.yql.configuration.YqlConfiguration;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

@UriEndpoint(firstVersion = "2.21.0", scheme = "yql", title = "Yahoo Query Language", syntax = "yql:query", producerOnly = true, label = "api,http")
public class YqlEndpoint extends DefaultEndpoint {

    @UriParam
    private final YqlConfiguration configuration;
    private final HttpClientConnectionManager connectionManager;
    private CloseableHttpClient httpClient;

    YqlEndpoint(final String uri, final YqlComponent component, final YqlConfiguration configuration, final HttpClientConnectionManager connectionManager) {
        super(uri, component);
        this.configuration = configuration;
        this.connectionManager = connectionManager;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new YqlProducer(this);
    }

    @Override
    public Consumer createConsumer(final Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer does not supported for YQL component: " + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    protected void doStop() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        }
        return httpClient;
    }

    YqlConfiguration getConfiguration() {
        return configuration;
    }
}