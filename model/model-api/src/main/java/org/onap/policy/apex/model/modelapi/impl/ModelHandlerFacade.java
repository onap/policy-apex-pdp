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

package org.onap.policy.apex.model.modelapi.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.dao.ApexDao;
import org.onap.policy.apex.model.basicmodel.dao.ApexDaoFactory;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelFileWriter;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelWriter;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyAnalyser;
import org.onap.policy.apex.model.policymodel.handling.PolicyAnalysisResult;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelComparer;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelMerger;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelSplitter;
import org.onap.policy.apex.model.utilities.Assertions;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class acts as a facade for model handling for the Apex Model API.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ModelHandlerFacade {
    private static final String FOUND_IN_DATABASE = " found in database";
    private static final String FILE_NAME_MAY_NOT_BE_NULL = "fileName may not be null";
    private static final String MODEL = "model ";
    private static final String ALREADY_LOADED = " already loaded";

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ModelHandlerFacade.class);

    // Apex model we're working towards
    private final ApexModel apexModel;

    // JSON output on list/delete if set
    private final boolean jsonMode;

    /**
     * This Constructor creates a model handling facade for the given {@link ApexModel}.
     *
     * @param apexModel the apex model to manipulate
     * @param apexProperties properties for the model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     */
    public ModelHandlerFacade(final ApexModel apexModel, final Properties apexProperties, final boolean jsonMode) {
        Assertions.argumentNotNull(apexModel, "apexModel may not be null");
        Assertions.argumentNotNull(apexProperties, "apexProperties may not be null");

        this.apexModel = apexModel;
        this.jsonMode = jsonMode;
    }

    /**
     * Load an Apex model from a string.
     *
     * @param modelString the string with the model
     * @return the result of the operation
     */
    public ApexAPIResult loadFromString(final String modelString) {
        Assertions.argumentNotNull(modelString, "modelString may not be null");

        if (!apexModel.getPolicyModel().getKey().equals(AxArtifactKey.getNullKey())) {
            return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                    MODEL + apexModel.getPolicyModel().getKey().getId() + ALREADY_LOADED);
        }

        ApexAPIResult result = new ApexAPIResult();
        AxPolicyModel newPolicyModel = loadModelFromString(modelString, result);
        apexModel.setPolicyModel(newPolicyModel != null ? newPolicyModel : new AxPolicyModel());

        return result;
    }

    /**
     * Load an Apex model from a file.
     *
     * @param fileName the file name of the file with the model
     * @return the result of the operation
     */
    public ApexAPIResult loadFromFile(final String fileName) {
        Assertions.argumentNotNull(fileName, FILE_NAME_MAY_NOT_BE_NULL);

        if (!apexModel.getPolicyModel().getKey().equals(AxArtifactKey.getNullKey())) {
            return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                    MODEL + apexModel.getPolicyModel().getKey().getId() + ALREADY_LOADED);
        }

        ApexAPIResult result = new ApexAPIResult();
        AxPolicyModel newPolicyModel = loadModelFromFile(fileName, result);
        apexModel.setPolicyModel(newPolicyModel != null ? newPolicyModel : new AxPolicyModel());

        return result;
    }

    /**
     * Save an Apex model to a file.
     *
     * @param fileName the file name
     * @param xmlFlag if true, save the file in XML format, otherwise save the file in the default
     *        JSON format
     * @return the result of the operation
     */
    public ApexAPIResult saveToFile(final String fileName, final boolean xmlFlag) {
        Assertions.argumentNotNull(fileName, FILE_NAME_MAY_NOT_BE_NULL);

        ApexModelFileWriter<AxPolicyModel> apexModelFileWriter = new ApexModelFileWriter<>(false);

        try {
            if (xmlFlag) {
                apexModelFileWriter.apexModelWriteXmlFile(apexModel.getPolicyModel(), AxPolicyModel.class, fileName);
            } else {
                apexModelFileWriter.apexModelWriteJsonFile(apexModel.getPolicyModel(), AxPolicyModel.class, fileName);
            }
            return new ApexAPIResult();
        } catch (ApexException e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Load an Apex model from a database.
     *
     * @param modelName the name of the model to load
     * @param modelVersion the version of the model to load, loads the policy model from the
     *        database with this name, if more than one exist, an exception is thrown
     * @param DaoParameters the parameters to use to access the database over JDBC
     * @return the result of the operation
     */
    public ApexAPIResult loadFromDatabase(final String modelName, final String modelVersion,
            final DaoParameters DaoParameters) {
        Assertions.argumentNotNull(modelName, "modelName may not be null");
        Assertions.argumentNotNull(DaoParameters, "DaoParameters may not be null");

        if (!apexModel.getPolicyModel().getKey().equals(AxArtifactKey.getNullKey())) {
            return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                    MODEL + apexModel.getPolicyModel().getKey().getId() + ALREADY_LOADED);
        }

        ApexDao apexDao = null;
        try {
            apexDao = new ApexDaoFactory().createApexDao(DaoParameters);
            apexDao.init(DaoParameters);

            // Single specific model requested
            if (modelVersion != null) {
                AxPolicyModel daoPolicyModel =
                        apexDao.get(AxPolicyModel.class, new AxArtifactKey(modelName, modelVersion));

                if (daoPolicyModel != null) {
                    apexModel.setPolicyModel(daoPolicyModel);
                    return new ApexAPIResult();
                } else {
                    apexModel.setPolicyModel(new AxPolicyModel());
                    return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, "no policy model with name " + modelName
                            + " and version " + modelVersion + FOUND_IN_DATABASE);
                }
            }
            // Fishing expedition
            else {
                return searchInDatabase(modelName, apexDao, apexModel);
            }
        } catch (ApexException | ApexRuntimeException e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        } finally {
            if (apexDao != null) {
                apexDao.close();
            }
        }
    }

    /**
     * Search for an Apex model in the database.
     *
     * @param modelName the name of the model to load
     * @param apexDao the DAO to use to find the model
     * @param apexModel the APEX model we are loading the found model into
     * @return the result of the operation
     */
    private ApexAPIResult searchInDatabase(String modelName, ApexDao apexDao, ApexModel apexModel) {
        AxPolicyModel foundPolicyModel = null;

        List<AxPolicyModel> policyModelList = apexDao.getAll(AxPolicyModel.class);
        for (AxPolicyModel dbPolicyModel : policyModelList) {
            if (dbPolicyModel.getKey().getName().equals(modelName)) {
                if (foundPolicyModel == null) {
                    foundPolicyModel = dbPolicyModel;
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.FAILED,
                            "more than one policy model with name " + modelName + FOUND_IN_DATABASE);
                }
            }
        }

        if (foundPolicyModel != null) {
            apexModel.setPolicyModel(foundPolicyModel);
            return new ApexAPIResult();
        } else {
            apexModel.setPolicyModel(new AxPolicyModel());
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED,
                    "no policy model with name " + modelName + FOUND_IN_DATABASE);
        }
    }

    /**
     * Save an Apex model to a database.
     *
     * @param DaoParameters the parameters to use to access the database over JDBC
     * @return the result of the operation
     */
    public ApexAPIResult saveToDatabase(final DaoParameters DaoParameters) {
        ApexDao apexDao = null;

        try {
            apexDao = new ApexDaoFactory().createApexDao(DaoParameters);
            apexDao.init(DaoParameters);

            apexDao.create(apexModel.getPolicyModel());
            return new ApexAPIResult();
        } catch (ApexException e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        } finally {
            if (apexDao != null) {
                apexDao.close();
            }
        }
    }

    /**
     * Read an APEX model from a location identified by a URL.
     *
     * @param urlString the url string
     * @return the result of the operation
     */
    public ApexAPIResult readFromURL(final String urlString) {
        Assertions.argumentNotNull(urlString, "urlString may not be null");

        if (!apexModel.getPolicyModel().getKey().equals(AxArtifactKey.getNullKey())) {
            return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                    MODEL + apexModel.getPolicyModel().getKey().getId() + ALREADY_LOADED);
        }

        URL apexModelURL;
        try {
            apexModelURL = new URL(urlString);
        } catch (MalformedURLException e) {
            ApexAPIResult result = new ApexAPIResult(ApexAPIResult.RESULT.FAILED);
            result.addMessage("URL string " + urlString + " is not a valid URL");
            result.addThrowable(e);
            return result;
        }

        try {
            ApexModelReader<AxPolicyModel> apexModelReader = new ApexModelReader<>(AxPolicyModel.class);
            apexModelReader.setValidateFlag(false);
            AxPolicyModel newPolicyModel = apexModelReader.read(apexModelURL.openStream());
            apexModel.setPolicyModel(newPolicyModel != null ? newPolicyModel : new AxPolicyModel());
            return new ApexAPIResult();
        } catch (ApexModelException | IOException e) {
            apexModel.setPolicyModel(new AxPolicyModel());
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Write an APEX model to a location identified by a URL.
     *
     * @param urlString the URL to read the model from
     * @param xmlFlag if true, save the file in XML format, otherwise save the file in the default
     *        JSON format
     * @return the result of the operation
     */
    public ApexAPIResult writeToURL(final String urlString, final boolean xmlFlag) {
        Assertions.argumentNotNull(urlString, "urlString may not be null");

        URL apexModelURL;
        try {
            apexModelURL = new URL(urlString);
        } catch (MalformedURLException e) {
            ApexAPIResult result = new ApexAPIResult(ApexAPIResult.RESULT.FAILED);
            result.addMessage("URL string " + urlString + " is not a valid URL");
            result.addThrowable(e);
            return result;
        }

        try {
            ApexModelWriter<AxPolicyModel> apexModelWriter = new ApexModelWriter<>(AxPolicyModel.class);
            apexModelWriter.setValidateFlag(false);
            apexModelWriter.setJsonOutput(!xmlFlag);

            // Open the URL for output and write the model
            URLConnection urlConnection = apexModelURL.openConnection();
            urlConnection.setDoOutput(true);

            apexModelWriter.write(apexModel.getPolicyModel(), urlConnection.getOutputStream());
            return new ApexAPIResult();
        } catch (ApexModelException | IOException e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Analyse an Apex model that shows the concept usage references of a policy model.
     *
     * @return the result of the operation
     */
    public ApexAPIResult analyse() {
        PolicyAnalysisResult analysisResult = new PolicyAnalyser().analyse(apexModel.getPolicyModel());
        return new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS, analysisResult.toString());
    }

    /**
     * Validate an Apex model, checking all concepts and references in the model.
     *
     * @return the result of the operation
     */
    public ApexAPIResult validate() {
        ApexAPIResult result = new ApexAPIResult();
        try {
            AxValidationResult validationResult = apexModel.getPolicyModel().validate(new AxValidationResult());

            if (!validationResult.isValid()) {
                result.setResult(ApexAPIResult.RESULT.FAILED);
            }
            result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false)
                    .writeString(apexModel.getPolicyModel().getKey(), AxArtifactKey.class, jsonMode));
            result.addMessage(validationResult.toString());
            return result;
        } catch (Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Compare to Apex models, returning the differences between the models.
     *
     * @param otherModelFileName the file name of the other model
     * @param diffsOnly only returns differences between the model when set
     * @param keysOnly only returns the keys that are different when set, when not set values are
     *        also returned
     * @return the result of the operation
     */
    public ApexAPIResult compare(final String otherModelFileName, final boolean diffsOnly, final boolean keysOnly) {
        ApexAPIResult result = new ApexAPIResult();
        try {
            AxPolicyModel otherPolicyModel = loadModelFromFile(otherModelFileName, result);
            if (!result.getResult().equals(ApexAPIResult.RESULT.SUCCESS)) {
                return result;
            }

            PolicyModelComparer policyModelComparer =
                    new PolicyModelComparer(apexModel.getPolicyModel(), otherPolicyModel);
            result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false)
                    .writeString(apexModel.getPolicyModel().getKey(), AxArtifactKey.class, jsonMode));
            result.addMessage(policyModelComparer.toString());

            return result;
        } catch (Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Compare two Apex models, returning the differences between the models.
     *
     * @param otherModelString the other model as a string
     * @param diffsOnly only returns differences between the model when set
     * @param keysOnly only returns the keys that are different when set, when not set values are
     *        also returned
     * @return the result of the operation
     */
    public ApexAPIResult compareWithString(final String otherModelString, final boolean diffsOnly,
            final boolean keysOnly) {
        ApexAPIResult result = new ApexAPIResult();
        try {
            AxPolicyModel otherPolicyModel = loadModelFromString(otherModelString, result);
            if (!result.getResult().equals(ApexAPIResult.RESULT.SUCCESS)) {
                return result;
            }

            PolicyModelComparer policyModelComparer =
                    new PolicyModelComparer(apexModel.getPolicyModel(), otherPolicyModel);
            result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false)
                    .writeString(apexModel.getPolicyModel().getKey(), AxArtifactKey.class, jsonMode));
            result.addMessage(policyModelComparer.toString());

            return result;
        } catch (Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Split out a sub model from an Apex model that contains a given subset of the policies in the
     * original model.
     *
     * @param targetModelName the file name of the target model in which to store the model split
     *        out from the original model
     * @param splitOutPolicies the policies form the original model to include in the split out
     *        model, specified as a comma delimited list of policy names
     * @return the result of the operation
     */
    public ApexAPIResult split(final String targetModelName, final String splitOutPolicies) {
        Set<AxArtifactKey> requiredPolicySet = new LinkedHashSet<>();

        // Split the policy names on comma
        String[] policyNames = splitOutPolicies.split(",");

        // Iterate over the policy names
        for (String policyName : policyNames) {
            // Split out this specific policy
            AxPolicy requiredPolicy = apexModel.getPolicyModel().getPolicies().get(policyName);

            if (requiredPolicy != null) {
                requiredPolicySet.add(requiredPolicy.getKey());
            } else {
                return new ApexAPIResult(ApexAPIResult.RESULT.FAILED,
                        "policy for policy name " + policyName + " not found in model");
            }
        }

        try {
            AxPolicyModel splitPolicyModel =
                    PolicyModelSplitter.getSubPolicyModel(apexModel.getPolicyModel(), requiredPolicySet, false);

            ApexModelFileWriter<AxPolicyModel> apexModelFileWriter = new ApexModelFileWriter<>(false);
            apexModelFileWriter.apexModelWriteJsonFile(splitPolicyModel, AxPolicyModel.class, targetModelName);
            return new ApexAPIResult();
        } catch (ApexException e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Split out a sub model from an Apex model that contains a given subset of the policies in the
     * original model, return the split model in the result as a string.
     *
     * @param splitOutPolicies the policies form the original model to include in the split out
     *        model, specified as a comma delimited list of policy names
     * @return the result of the operation
     */
    public ApexAPIResult split(final String splitOutPolicies) {
        ApexAPIResult splitResult = new ApexAPIResult();
        File tempSplitPolicyFile = null;
        try {
            tempSplitPolicyFile = File.createTempFile("ApexTempPolicy", null);

            // Split the policy into a temporary file
            splitResult = split(tempSplitPolicyFile.getCanonicalPath(), splitOutPolicies);
            if (splitResult.isNOK()) {
                return splitResult;
            }

            // Get the policy model into a string
            String splitPolicyModelString = TextFileUtils.getTextFileAsString(tempSplitPolicyFile.getCanonicalPath());

            // Return the policy model
            splitResult.addMessage(splitPolicyModelString);
            return splitResult;
        } catch (Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED,
                    "split of policy model " + apexModel.getPolicyModel().getId() + " failed", e);
        } finally {
            if (tempSplitPolicyFile != null) {
                try {
                    Files.delete(tempSplitPolicyFile.toPath());
                } catch (IOException e) {
                    LOGGER.debug("delete of temporary file failed", e);
                }
            }
        }
    }

    /**
     * Merge two Apex models together.
     *
     * @param mergeInModelName the file name of the model to merge into the current model
     * @param keepOriginal if this flag is set to true, if a concept exists in both models, the
     *        original model copy of that concept is kept, if the flag is set to false, then the
     *        copy of the concept from the mergeInModel overwrites the concept in the original model
     * @return the result of the operation
     */
    public ApexAPIResult merge(final String mergeInModelName, final boolean keepOriginal) {
        ApexAPIResult result = new ApexAPIResult();
        AxPolicyModel mergeInPolicyModel = loadModelFromFile(mergeInModelName, result);
        if (!result.getResult().equals(ApexAPIResult.RESULT.SUCCESS)) {
            return result;
        }

        try {
            AxPolicyModel mergedPolicyModel = PolicyModelMerger.getMergedPolicyModel(apexModel.getPolicyModel(),
                    mergeInPolicyModel, keepOriginal, false);
            apexModel.setPolicyModel(mergedPolicyModel != null ? mergedPolicyModel : new AxPolicyModel());
            return new ApexAPIResult();
        } catch (ApexModelException e) {
            apexModel.setPolicyModel(new AxPolicyModel());
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Merge two Apex models together.
     *
     * @param otherModelString the model to merge as a string
     * @param keepOriginal if this flag is set to true, if a concept exists in both models, the
     *        original model copy of that concept is kept, if the flag is set to false, then the
     *        copy of the concept from the mergeInModel overwrites the concept in the original model
     * @return the result of the operation
     */
    public ApexAPIResult mergeWithString(final String otherModelString, final boolean keepOriginal) {
        ApexAPIResult result = new ApexAPIResult();
        AxPolicyModel mergeInPolicyModel = loadModelFromString(otherModelString, result);
        if (!result.getResult().equals(ApexAPIResult.RESULT.SUCCESS)) {
            return result;
        }

        try {
            AxPolicyModel mergedPolicyModel = PolicyModelMerger.getMergedPolicyModel(apexModel.getPolicyModel(),
                    mergeInPolicyModel, keepOriginal, false);
            apexModel.setPolicyModel(mergedPolicyModel != null ? mergedPolicyModel : new AxPolicyModel());
            return new ApexAPIResult();
        } catch (ApexModelException e) {
            apexModel.setPolicyModel(new AxPolicyModel());
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Load a policy model from a file.
     *
     * @param fileName the name of the file containing the model
     * @param result the result of the operation
     * @return the model
     */
    private AxPolicyModel loadModelFromFile(final String fileName, final ApexAPIResult result) {
        Assertions.argumentNotNull(fileName, FILE_NAME_MAY_NOT_BE_NULL);

        AxPolicyModel readModel = null;

        final URL apexModelURL = ResourceUtils.getLocalFile(fileName);
        if (apexModelURL == null) {
            result.setResult(ApexAPIResult.RESULT.FAILED);
            result.addMessage("file " + fileName + " not found");
            return null;
        }

        try {
            ApexModelReader<AxPolicyModel> apexModelReader = new ApexModelReader<>(AxPolicyModel.class);
            apexModelReader.setValidateFlag(false);
            readModel = apexModelReader.read(apexModelURL.openStream());
            result.setResult(ApexAPIResult.RESULT.SUCCESS);
            return readModel;
        } catch (Exception e) {
            result.setResult(ApexAPIResult.RESULT.FAILED);
            result.addThrowable(e);
            return null;
        }
    }

    /**
     * Load a policy model from a string.
     *
     * @param modelString the string containing the model
     * @param result the result of the operation
     * @return the model
     */
    private AxPolicyModel loadModelFromString(final String modelString, final ApexAPIResult result) {
        Assertions.argumentNotNull(modelString, "modelString may not be null");

        AxPolicyModel readModel = null;

        InputStream modelStringStream = new ByteArrayInputStream(modelString.getBytes());

        try {
            ApexModelReader<AxPolicyModel> apexModelReader = new ApexModelReader<>(AxPolicyModel.class);
            apexModelReader.setValidateFlag(false);
            readModel = apexModelReader.read(modelStringStream);
            result.setResult(ApexAPIResult.RESULT.SUCCESS);
            return readModel;
        } catch (Exception e) {
            result.setResult(ApexAPIResult.RESULT.FAILED);
            result.addThrowable(e);
            return null;
        }
    }
}
