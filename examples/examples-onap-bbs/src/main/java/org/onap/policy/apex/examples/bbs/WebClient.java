/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
 *  Modifications Copyright (C) 2019-2021,2024 Nordix Foundation.
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The Class WebClient act as rest client for BBS usecase.
 */
public class WebClient {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(WebClient.class);

    // Duplicated string constants
    private static final String BBS_POLICY = "BBS Policy";

    //Features to prevent XXE injection
    private static final String XML_DISALLOW_DOCTYPE_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String XML_EXTERNAL_ENTITY_FEATURE = "http://xml.org/sax/features/external-general-entities";

    /**
     * Send simple https rest request.
     *
     * @param requestUrl    url
     * @param requestMethod method eg POST/GET/PUT
     * @param outputStr     Data
     * @param username      Simple Username
     * @param pass          Simple password
     * @param contentType   http content type
     * @return String response message
     */
    public String httpRequest(URL requestUrl, String requestMethod, String outputStr, String username, String pass,
        String contentType) {
        var result = "";
        var builder = new StringBuilder();
        try {
            LOGGER.info("httpsRequest starts {} method {}", requestUrl, requestMethod);
            disableCertificateValidation();

            var httpUrlConn = (HttpURLConnection) requestUrl.openConnection();

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);

            if ((username != null) && (pass != null)) {
                httpUrlConn.setRequestProperty("Authorization", getAuth(username, pass));
            } else {
                LOGGER.warn("Authorization information missing");
            }

            httpUrlConn.setRequestProperty("Content-Type", contentType);
            httpUrlConn.setRequestProperty("Accept", contentType);
            httpUrlConn.setRequestProperty("X-FromAppId", BBS_POLICY);
            httpUrlConn.setRequestProperty("X-TransactionId", BBS_POLICY);
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            if (null != outputStr) {
                var outputStream = httpUrlConn.getOutputStream();
                outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            }

            try (var bufferedReader = new BufferedReader(
                new InputStreamReader(httpUrlConn.getInputStream(), StandardCharsets.UTF_8))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    builder.append(str);
                }
                httpUrlConn.disconnect();
                result = builder.toString();
            }
            LOGGER.info("httpsRequest success");
        } catch (Exception ce) {
            LOGGER.error("httpsRequest Exception", ce);
        }
        return result;
    }

    /**
     * Pretty print xml string.
     *
     * @param xml    Input string
     * @param indent Indent number
     * @return Indented xml string
     */
    public String toPrettyString(String xml, int indent) {
        try {
            try (var br = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {

                var df = DocumentBuilderFactory.newInstance();
                df.setFeature(XML_DISALLOW_DOCTYPE_FEATURE, true);
                df.setFeature(XML_EXTERNAL_ENTITY_FEATURE, false);
                var document = df.newDocumentBuilder().parse(new InputSource(br));

                document.normalize();
                var path = XPathFactory.newInstance().newXPath();
                var nodeList = (NodeList) path
                    .evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET);

                for (var i = 0; i < nodeList.getLength(); ++i) {
                    var node = nodeList.item(i);
                    node.getParentNode().removeChild(node);
                }

                var transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", indent);
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

                var transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                var stringWriter = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
                return stringWriter.toString();
            }
        } catch (Exception e) {
            throw new ApexRuntimeException("Convert to Pretty string failed", e);
        }
    }

    /**
     * Disable ssl verification.
     */
    private static void disableCertificateValidation() {
        try {
            TrustManager[] trustAllCerts = NetworkUtil.getAlwaysTrustingManager();

            var sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((String hostname, SSLSession session) -> {
                if (!hostname.equalsIgnoreCase(session.getPeerHost())) {
                    LOGGER.warn("Warning: URL host \"{}\" is different to SSLSession host \"{}\".", hostname,
                        session.getPeerHost());
                    return false;
                }
                return true;
            });
        } catch (Exception e) {
            LOGGER.error("certificate validation Exception", e);
        }
    }

    /**
     * Return Basic Authentication String.
     *
     * @param userName UserName
     * @param password PassWord
     * @return Basic Authentication
     */
    private String getAuth(String userName, String password) {
        String userCredentials = userName + ":" + password;
        return ("Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes(StandardCharsets.UTF_8)));
    }
}
