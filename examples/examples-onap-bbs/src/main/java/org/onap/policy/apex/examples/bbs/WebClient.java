/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.examples.bbs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.EntityTag;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The Class WebClient act as rest client for BBS usecase.
 */
public class WebClient {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(WebClient.class);

    /**
     * Send simple https rest request.
     *
     * @param requestUrl  url
     * @param requestMethod  method eg POST/GET/PUT
     * @param outputStr   Data
     * @param username  Simple Username
     * @param pass   Simple password
     * @param contentType http content type
     * @param fillApp If required to fill app details
     * @return  String response message
     */
    public String httpRequest(String requestUrl, String requestMethod,
                               String outputStr, String username, String pass,
                               String contentType, boolean fillApp) {
        try {
            /* Create a trust manager that does not validate certificate chains*/
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            /* Install the all-trusting trust manager */
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            /* Create all-trusting host name verifier. */
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            /* Install the all-trusting host verifier */
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            LOGGER.error("httpsRequest Exception " + e);
        }

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.setConnectTimeout(5000);
        client.addFilter(new LoggingFilter());

        LOGGER.info("Sending request:");
        LOGGER.info(outputStr);
        LOGGER.info("URL: " + requestUrl + " method " + requestMethod);

        WebClientHttpResponse resp = new WebClientHttpResponse();
        resp.code = 200;
        resp.body = "";

        String tt1 = contentType + ";charset=UTF-8";

        WebResource webResource = addAuthType(client, "BASIC", username, pass).resource(requestUrl);
        WebResource.Builder webResourceBuilder = webResource.accept(contentType).type(tt1);
        if (fillApp) {
            webResourceBuilder.header("X-FromAppId", "BBS Policy");
            webResourceBuilder.header("X-TransactionId", "BBS Policy");
        }

        ClientResponse response;
        try {
            response = webResourceBuilder.method(requestMethod, ClientResponse.class, outputStr);
            resp.code = response.getStatus();
            resp.headers = response.getHeaders();
            EntityTag etag = response.getEntityTag();
            if (etag != null) {
                resp.message = etag.getValue();
            }
            if (response.hasEntity() && resp.code != 204) {
                resp.body = response.getEntity(String.class);
            }
        } catch (UniformInterfaceException | ClientHandlerException e) {
            LOGGER.info("Exception when sending http request", e);
        }

        LOGGER.info("HTTP response code: {}", resp.code);
        LOGGER.info("HTTP header: {}", resp.headers);
        LOGGER.info("HTTP response message: {}", resp.message);
        LOGGER.info("HTTP response body: {}", resp.body);

        return resp.body;
    }


    protected Client addAuthType(Client client, String authtype, String userName, String passWord) {
        if (authtype.equalsIgnoreCase("BASIC")) {
            if (userName != null && passWord != null) {
                client.addFilter(new HTTPBasicAuthFilter(userName, passWord));
            } else {
                LOGGER.info("Missing Authentication parameters");
            }
        } else {
            LOGGER.info("Non supported Authtication type");
        }
        return client;
    }

    /**
     * Pretty print xml string.
     *
     * @param xml Input string
     * @param indent Indent number
     * @return Indented xml string
     */
    public String toPrettyString(String xml, int indent) {
        try {
            try (ByteArrayInputStream br = new ByteArrayInputStream(xml.getBytes("UTF-8"))) {
                Document document = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(br));

                document.normalize();
                XPath path = XPathFactory.newInstance().newXPath();
                NodeList nodeList = (NodeList) path.evaluate("//text()[normalize-space()='']",
                        document,
                        XPathConstants.NODESET);

                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Node node = nodeList.item(i);
                    node.getParentNode().removeChild(node);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", indent);
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                StringWriter stringWriter = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
                return stringWriter.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
