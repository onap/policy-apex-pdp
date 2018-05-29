/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.test.distribution;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextItem000;
import org.onap.policy.apex.context.test.concepts.TestContextItem001;
import org.onap.policy.apex.context.test.concepts.TestContextItem002;
import org.onap.policy.apex.context.test.concepts.TestContextItem003;
import org.onap.policy.apex.context.test.concepts.TestContextItem004;
import org.onap.policy.apex.context.test.concepts.TestContextItem005;
import org.onap.policy.apex.context.test.concepts.TestContextItem006;
import org.onap.policy.apex.context.test.concepts.TestContextItem007;
import org.onap.policy.apex.context.test.concepts.TestContextItem008;
import org.onap.policy.apex.context.test.concepts.TestContextItem009;
import org.onap.policy.apex.context.test.concepts.TestContextItem00A;
import org.onap.policy.apex.context.test.concepts.TestContextItem00B;
import org.onap.policy.apex.context.test.concepts.TestContextItem00C;
import org.onap.policy.apex.context.test.concepts.TestExternalContextItem;
import org.onap.policy.apex.context.test.concepts.TestGlobalContextItem;
import org.onap.policy.apex.context.test.concepts.TestPolicyContextItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestContextInstantiation is used to test Apex context insitiation is correct.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class ContextInstantiation {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextInstantiation.class);

    /**
     * Test context instantiation.
     *
     * @throws ContextException the context exception
     */
    // CHECKSTYLE:OFF: checkstyle:MaximumMethodLength
    public void testContextInstantiation() throws ContextException {
        // CHECKSTYLE:ON: checkstyle:MaximumMethodLength
        LOGGER.debug("Running TestContextInstantiation test . . .");

        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributorInit", "0.0.1");
        Distributor contextDistributor = null;
        try {
            contextDistributor = new DistributorFactory().getDistributor(distributorKey);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }

        // @formatter:off
        final AxArtifactKey[] usedArtifactStackArray = {
                new AxArtifactKey("testC-top", "0.0.1"), new AxArtifactKey("testC-next", "0.0.1"),
                new AxArtifactKey("testC-bot", "0.0.1")
        };
        // @formatter:on

        try {
            // CHECKSTYLE:OFF: checkstyle:magicNumber
            final AxContextModel policyContextModel = TestContextAlbumFactory.createPolicyContextModel();
            final AxValidationResult result = new AxValidationResult();
            policyContextModel.validate(result);
            LOGGER.debug(result.toString());
            assert (result.isValid());

            contextDistributor.registerModel(policyContextModel);

            final ContextAlbum policyContextAlbum =
                    contextDistributor.createContextAlbum(new AxArtifactKey("PolicyContextAlbum", "0.0.1"));
            assert (policyContextAlbum != null);
            policyContextAlbum.setUserArtifactStack(usedArtifactStackArray);

            final Float testFloat = new Float(3.14159265359);
            final Date testDate = new Date();

            final TestContextItem009 tci9 = new TestContextItem009();
            tci9.setDateValue(new TestContextItem008(testDate));
            tci9.setTZValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
            tci9.setDST(true);

            final TestContextItem00A tciA = new TestContextItem00A();
            tciA.setDateValue(new TestContextItem008(testDate));
            tciA.setTZValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
            tciA.setDST(true);
            tciA.setUTCOffset(-600);
            tciA.setLocale(Locale.ENGLISH);

            final TreeSet<String> testTreeSet = new TreeSet<>();
            testTreeSet.add("one hundred");
            testTreeSet.add("one hundred and one");
            testTreeSet.add("one hundred and two");
            testTreeSet.add("one hundred and three");
            testTreeSet.add("one hundred and four");

            final Map<String, String> testHashMap = new HashMap<>();
            testHashMap.put("0", "zero");
            testHashMap.put("1", "one");
            testHashMap.put("2", "two");
            testHashMap.put("3", "three");
            testHashMap.put("4", "four");

            final TestContextItem006 testPolicyContextItem000 =
                    new TestContextItem006("This is a policy context string");
            final TestContextItem003 testPolicyContextItem001 = new TestContextItem003(0xFFFFFFFFFFFFFFFFL);
            final TestContextItem005 testPolicyContextItem002 = new TestContextItem005(Math.PI);
            final TestContextItem000 testPolicyContextItem003 = new TestContextItem000(true);
            final TestContextItem003 testPolicyContextItem004 = new TestContextItem003(testDate.getTime());
            final TestContextItem00C testPolicyContextItem005 = new TestContextItem00C(testHashMap);

            final Map<String, Object> valueMapA = new LinkedHashMap<>();
            valueMapA.put("TestPolicyContextItem001", testPolicyContextItem001);
            valueMapA.put("TestPolicyContextItem002", testPolicyContextItem002);
            valueMapA.put("TestPolicyContextItem003", testPolicyContextItem003);
            valueMapA.put("TestPolicyContextItem004", testPolicyContextItem004);
            valueMapA.put("TestPolicyContextItem005", testPolicyContextItem005);
            valueMapA.put("TestPolicyContextItem000", testPolicyContextItem000);

            try {
                policyContextAlbum.put("TestPolicyContextItem000", testPolicyContextItem000);
                assert ("Test should throw an exception".equals(""));
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals(
                        "Failed to set context value for key \"TestPolicyContextItem000\" in album \"PolicyContextAlbum:0.0.1\": "
                                + "PolicyContextAlbum:0.0.1: object \"TestContextItem006 [stringValue=This is a policy context string]\" "
                                + "of class \"org.onap.policy.apex.context.test.concepts.TestContextItem006\""
                                + " not compatible with class \"org.onap.policy.apex.context.test.concepts.TestPolicyContextItem\""));
            }

            try {
                policyContextAlbum.putAll(valueMapA);
                assert ("Test should throw an exception".equals(""));
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals(
                        "Failed to set context value for key \"TestPolicyContextItem001\" in album \"PolicyContextAlbum:0.0.1\": "
                                + "PolicyContextAlbum:0.0.1: object \"TestContextItem003 [longValue=-1]\" "
                                + "of class \"org.onap.policy.apex.context.test.concepts.TestContextItem003\""
                                + " not compatible with class \"org.onap.policy.apex.context.test.concepts.TestPolicyContextItem\""));
            }

            final TestPolicyContextItem policyContext = new TestPolicyContextItem();

            LOGGER.debug(policyContextAlbum.toString());

            policyContext.setTestPolicyContextItem000(testPolicyContextItem000);
            policyContext.setTestPolicyContextItem001(testPolicyContextItem001);
            policyContext.setTestPolicyContextItem002(testPolicyContextItem002);
            policyContext.setTestPolicyContextItem003(testPolicyContextItem003);
            policyContext.setTestPolicyContextItem004(testPolicyContextItem004);
            policyContext.setTestPolicyContextItem005(testPolicyContextItem005);

            final Map<String, Object> valueMap0 = new HashMap<>();
            valueMap0.put("TestPolicyContextItem", policyContext);

            policyContextAlbum.putAll(valueMap0);

            assert (((TestPolicyContextItem) policyContextAlbum.get("TestPolicyContextItem"))
                    .getTestPolicyContextItem000().getStringValue().equals("This is a policy context string"));
            assert (((TestPolicyContextItem) policyContextAlbum.get("TestPolicyContextItem"))
                    .getTestPolicyContextItem001().getLongValue() == 0xFFFFFFFFFFFFFFFFL);
            assert (((TestPolicyContextItem) policyContextAlbum.get("TestPolicyContextItem"))
                    .getTestPolicyContextItem002().getDoubleValue() == Math.PI);
            assert (((TestPolicyContextItem) policyContextAlbum.get("TestPolicyContextItem"))
                    .getTestPolicyContextItem003().getFlag());
            assert (((TestPolicyContextItem) policyContextAlbum.get("TestPolicyContextItem"))
                    .getTestPolicyContextItem004().getLongValue() == testDate.getTime());
            assert (((TestPolicyContextItem) policyContextAlbum.get("TestPolicyContextItem"))
                    .getTestPolicyContextItem005().getMapValue().equals(testHashMap));

            final AxContextModel globalContextModel = TestContextAlbumFactory.createGlobalContextModel();

            final TestContextItem000 testGlobalContextItem000 = new TestContextItem000(false);
            final TestContextItem001 testGlobalContextItem001 = new TestContextItem001((byte) 0xFF);
            final TestContextItem002 testGlobalContextItem002 = new TestContextItem002(0xFFFFFFFF);
            final TestContextItem003 testGlobalContextItem003 = new TestContextItem003(0xFFFFFFFFFFFFFFFFL);
            final TestContextItem004 testGlobalContextItem004 = new TestContextItem004(testFloat);
            final TestContextItem005 testGlobalContextItem005 = new TestContextItem005(Math.PI);
            final TestContextItem006 testGlobalContextItem006 =
                    new TestContextItem006("This is a global context string");
            final TestContextItem007 testGlobalContextItem007 = new TestContextItem007(testDate.getTime());
            final TestContextItem008 testGlobalContextItem008 = new TestContextItem008(testDate);
            final TestContextItem00B testGlobalContextItem00B = new TestContextItem00B(testTreeSet);
            final TestContextItem00C testGlobalContextItem00C = new TestContextItem00C(testHashMap);

            final TestGlobalContextItem globalContext = new TestGlobalContextItem();

            globalContext.setTestGlobalContextItem000(testGlobalContextItem000);
            globalContext.setTestGlobalContextItem001(testGlobalContextItem001);
            globalContext.setTestGlobalContextItem002(testGlobalContextItem002);
            globalContext.setTestGlobalContextItem003(testGlobalContextItem003);
            globalContext.setTestGlobalContextItem004(testGlobalContextItem004);
            globalContext.setTestGlobalContextItem005(testGlobalContextItem005);
            globalContext.setTestGlobalContextItem006(testGlobalContextItem006);
            globalContext.setTestGlobalContextItem007(testGlobalContextItem007);
            globalContext.setTestGlobalContextItem008(testGlobalContextItem008);
            globalContext.setTestGlobalContextItem009(tci9);
            globalContext.setTestGlobalContextItem00A(tciA);
            globalContext.setTestGlobalContextItem00B(testGlobalContextItem00B);
            globalContext.setTestGlobalContextItem00C(testGlobalContextItem00C);
            final Map<String, Object> valueMap1 = new HashMap<>();
            valueMap1.put("globalContext", globalContext);

            try {
                contextDistributor.registerModel(globalContextModel);
            } catch (final ContextException e) {
                assert (e.getMessage().equals(
                        "active context albums found in distributor, clear the distributor before registering models"));
            }

            contextDistributor.registerModel(globalContextModel);

            final ContextAlbum globalContextAlbum =
                    contextDistributor.createContextAlbum(new AxArtifactKey("GlobalContextAlbum", "0.0.1"));
            assert (globalContextAlbum != null);
            globalContextAlbum.setUserArtifactStack(usedArtifactStackArray);

            globalContextAlbum.putAll(valueMap1);

            assert (!((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem000()
                    .getFlag());
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem001()
                    .getByteValue() == (byte) 0xFF);
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem002()
                    .getIntValue() == 0xFFFFFFFF);
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem003()
                    .getLongValue() == 0xFFFFFFFFFFFFFFFFL);
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem004()
                    .getFloatValue() == testFloat);
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem005()
                    .getDoubleValue() == Math.PI);
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem006()
                    .getStringValue().equals("This is a global context string"));
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem007()
                    .getLongValue() == testDate.getTime());
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem008()
                    .getDateValue().equals(testDate));
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem009()
                    .getDateValue().getTime() == tci9.getDateValue().getTime());
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem00A()
                    .getDateValue().getTime() == tciA.getDateValue().getTime());
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem00B()
                    .getSetValue().equals(testTreeSet));
            assert (((TestGlobalContextItem) globalContextAlbum.get("globalContext")).getTestGlobalContextItem00C()
                    .getMapValue().equals(testHashMap));

            final AxContextModel externalContextModel = TestContextAlbumFactory.createExternalContextModel();

            final TestExternalContextItem externalContext = new TestExternalContextItem();

            final TestContextItem000 testExternalContextItem000 = new TestContextItem000(false);
            final TestContextItem001 testExternalContextItem001 = new TestContextItem001((byte) 0xFF);
            final TestContextItem002 testExternalContextItem002 = new TestContextItem002(0xFFFFFFFF);
            final TestContextItem003 testExternalContextItem003 = new TestContextItem003(0xFFFFFFFFFFFFFFFFL);
            final TestContextItem004 testExternalContextItem004 = new TestContextItem004(testFloat);
            final TestContextItem005 testExternalContextItem005 = new TestContextItem005(Math.PI);
            final TestContextItem006 testExternalContextItem006 =
                    new TestContextItem006("This is an external context string");
            final TestContextItem007 testExternalContextItem007 = new TestContextItem007(testDate.getTime());
            final TestContextItem008 testExternalContextItem008 = new TestContextItem008(testDate);
            final TestContextItem00B testExternalContextItem00B = new TestContextItem00B(testTreeSet);
            final TestContextItem00C testExternalContextItem00C = new TestContextItem00C(testHashMap);

            final TestContextItem009 tci9A = new TestContextItem009(tci9);
            final TestContextItem00A tciAA = new TestContextItem00A(tciA);

            externalContext.setTestExternalContextItem000(testExternalContextItem000);
            externalContext.setTestExternalContextItem001(testExternalContextItem001);
            externalContext.setTestExternalContextItem002(testExternalContextItem002);
            externalContext.setTestExternalContextItem003(testExternalContextItem003);
            externalContext.setTestExternalContextItem004(testExternalContextItem004);
            externalContext.setTestExternalContextItem005(testExternalContextItem005);
            externalContext.setTestExternalContextItem006(testExternalContextItem006);
            externalContext.setTestExternalContextItem007(testExternalContextItem007);
            externalContext.setTestExternalContextItem008(testExternalContextItem008);
            externalContext.setTestExternalContextItem009(tci9A);
            externalContext.setTestExternalContextItem00A(tciAA);
            externalContext.setTestExternalContextItem00B(testExternalContextItem00B);
            externalContext.setTestExternalContextItem00C(testExternalContextItem00C);

            final Map<String, Object> valueMap2 = new HashMap<>();
            valueMap2.put("externalContext", externalContext);

            contextDistributor.clear();
            contextDistributor.init(new AxArtifactKey("ClearedandInittedDistributor", "0.0.1"));
            contextDistributor.registerModel(externalContextModel);

            final ContextAlbum externalContextAlbum =
                    contextDistributor.createContextAlbum(new AxArtifactKey("ExternalContextAlbum", "0.0.1"));
            assert (externalContextAlbum != null);
            externalContextAlbum.setUserArtifactStack(usedArtifactStackArray);

            externalContextAlbum.putAll(valueMap2);
            externalContextAlbum.getAlbumDefinition().setWritable(false);

            assert (!((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem000().getFlag());
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem001().getByteValue() == (byte) 0xFF);
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem002().getIntValue() == 0xFFFFFFFF);
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem003().getLongValue() == 0xFFFFFFFFFFFFFFFFL);
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem004().getFloatValue() == testFloat);
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem005().getDoubleValue() == Math.PI);
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem006().getStringValue().equals("This is an external context string"));
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem007().getLongValue() == testDate.getTime());
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem008().getDateValue().equals(testDate));
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem009().getDateValue().getTime() == tci9A.getDateValue().getTime());
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem00A().getDateValue().getTime() == tciAA.getDateValue().getTime());
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem00B().getSetValue().equals(testTreeSet));
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem00C().getMapValue().equals(testHashMap));

            final Collection<Object> mapValues = externalContextAlbum.values();
            assert (externalContextAlbum.values().containsAll(mapValues));

            // Check that clearing does not work
            try {
                externalContextAlbum.clear();
                assert ("Test should throw an exception".equals(""));
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage()
                        .equals("album \"ExternalContextAlbum:0.0.1\" clear() not allowed on read only albums"));
            }

            assert (externalContextAlbum.size() == 1);

            try {
                externalContextAlbum.containsKey(null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals("null values are illegal on method parameter \"key\""));
            }

            assert (externalContextAlbum.containsKey("externalContext"));
            assert (!externalContextAlbum.containsKey("globalContext"));

            try {
                externalContextAlbum.containsValue(null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals("null values are illegal on method parameter \"value\""));
            }

            assert (externalContextAlbum.containsValue(externalContext));
            assert (!externalContextAlbum.containsValue(new String("Hello")));

            final Set<Entry<String, Object>> entrySet = externalContextAlbum.entrySet();
            assert (entrySet.size() == 1);

            try {
                externalContextAlbum.get(null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage()
                        .equals("album \"ExternalContextAlbum:0.0.1\" null keys are illegal on keys for get()"));
            }

            final Object aObject = externalContextAlbum.get("externalContext");
            assert (aObject.equals(externalContext));

            // put null keys should fail, throws a runtime exception
            try {
                externalContextAlbum.put(null, null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage()
                        .equals("album \"ExternalContextAlbum:0.0.1\" null keys are illegal on keys for put()"));
            }

            try {
                externalContextAlbum.put("TestExternalContextItem00A", null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals(
                        "album \"ExternalContextAlbum:0.0.1\" null values are illegal on key \"TestExternalContextItem00A\" for put()"));
            }
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem00A().equals(tciAA));

            // Should return the hash set
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem00B().getSetValue().equals(testTreeSet));

            assert (externalContextAlbum.values().containsAll(mapValues));

            // Set the write flag back as it should be
            externalContextAlbum.getAlbumDefinition().setWritable(true);

            // Put should return the previous contextItem
            final TestExternalContextItem externalContextOther = new TestExternalContextItem();
            externalContextOther.setTestExternalContextItem002(new TestContextItem002());
            externalContextOther.getTestExternalContextItem002().setIntValue(2000);

            assert (externalContextAlbum.put("externalContext", externalContextOther).equals(externalContext));
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem002().getIntValue() == 2000);
            assert (externalContextAlbum.put("externalContext", externalContext).equals(externalContextOther));
            assert (((TestExternalContextItem) externalContextAlbum.get("externalContext"))
                    .getTestExternalContextItem002().getIntValue() == -1);

            try {
                externalContextAlbum.put("TestExternalContextItem00A", null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals(
                        "album \"ExternalContextAlbum:0.0.1\" null values are illegal on key \"TestExternalContextItem00A\" for put()"));
            }
            assert (externalContextAlbum.get("externalContext").equals(externalContext));

            try {
                externalContextAlbum.put("TestExternalContextItemFFF", null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals(
                        "album \"ExternalContextAlbum:0.0.1\" null values are illegal on key \"TestExternalContextItemFFF\" for put()"));
            }
            assert (externalContextAlbum.size() == 1);

            try {
                externalContextAlbum.put("TestExternalContextItemFFF", null);
            } catch (final ContextRuntimeException e) {
                assert (e.getMessage().equals(
                        "album \"ExternalContextAlbum:0.0.1\" null values are illegal on key \"TestExternalContextItemFFF\" for put()"));
            }
            assert (externalContextAlbum.size() == 1);

            // Should ignore remove
            externalContextAlbum.remove("TestExternalContextItem017");
            assert (externalContextAlbum.size() == 1);

            assert (externalContextAlbum.values().size() == 1);
            assert (externalContextAlbum.values().containsAll(mapValues));
            // CHECKSTYLE:ON: checkstyle:magicNumber
        } catch (final Exception e) {
            e.printStackTrace();
            contextDistributor.clear();
            assert ("Test has failed".equals(""));
        }

        try {
            contextDistributor.clear();
        } catch (final Exception e) {
            e.printStackTrace();
            assert ("Test has failed".equals(""));
        }
    }
}
