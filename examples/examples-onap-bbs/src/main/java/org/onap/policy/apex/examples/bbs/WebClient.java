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



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
     * Disable ssl verification.
     */
    private static void disableCertificateValidation() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            LOGGER.error("httpsRequest Exception " + e);
        }
    }

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
     * @param disableSSl If disabling ssl checking
     * @return  String response message
     */
    public String httpsRequest(String requestUrl, String requestMethod,
                               String outputStr, String username, String pass,
                               String contentType, boolean fillApp, boolean disableSSl) {
        String result = "";
        StringBuffer buffer = new StringBuffer();
        try {
            LOGGER.info("httpsRequest starts" + requestUrl + " method " + requestMethod);
            if (disableSSl) {
                disableCertificateValidation();
            }
            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
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
            if (fillApp) {
                httpUrlConn.setRequestProperty("X-FromAppId", "BBS Policy");
                httpUrlConn.setRequestProperty("X-TransactionId", "BBS Policy");
            }
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            try (InputStream inputStream = httpUrlConn.getInputStream()) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8")) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            buffer.append(str);
                        }
                    }
                }
                httpUrlConn.disconnect();
                result = buffer.toString();
            }
            LOGGER.info("httpsRequest success ");
        } catch (Exception ce) {
            LOGGER.error("httpsRequest Exception " + ce);
        }
        return result;
    }

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
     * @param disableSSl If disabling ssl checking
     * @return  String response message
     */
    public String httpRequest(String requestUrl, String requestMethod,
                              String outputStr, String username, String pass,
                              String contentType, boolean fillApp, boolean disableSSl) {
        String result = "";
        StringBuffer buffer = new StringBuffer();
        try {
            LOGGER.info("httpRequest starts" + requestUrl + " method " + requestMethod);
            if (disableSSl) {
                disableCertificateValidation();
            }
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
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
            if (fillApp) {
                httpUrlConn.setRequestProperty("X-FromAppId", "BBS Policy");
                httpUrlConn.setRequestProperty("X-TransactionId", "BBS Policy");
            }
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            try (InputStream inputStream = httpUrlConn.getInputStream()) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8")) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            buffer.append(str);
                        }
                    }
                }
                httpUrlConn.disconnect();
                result = buffer.toString();
            }
            LOGGER.info("httpsRequest success ");
        } catch (Exception ce) {
            LOGGER.error("httpsRequest Exception " + ce);
        }
        return result;
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
        return ("Basic " + java.util.Base64.getEncoder().encodeToString(userCredentials.getBytes()));
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
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
