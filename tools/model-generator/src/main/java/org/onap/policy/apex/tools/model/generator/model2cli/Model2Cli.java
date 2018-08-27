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

package org.onap.policy.apex.tools.model.generator.model2cli;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.onap.policy.apex.auth.clicodegen.CGCliEditor;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.modelapi.ApexModelFactory;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.apex.tools.common.OutputFile;
import org.onap.policy.apex.tools.model.generator.KeyInfoGetter;
import org.stringtemplate.v4.ST;

/**
 * Takes a model and generates the JSON event schemas.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class Model2Cli {

    /** Application name, used as prompt. */
    private final String appName;

    /** The file name of the policy model. */
    private final String modelFile;

    /** The output file, if any. */
    private final OutputFile outFile;

    /** Pre-validate the model. */
    private final boolean validate;

    /** utility for getting key information and parsing keys etc.. */
    private KeyInfoGetter kig = null;

    /**
     * Creates a new model to CLI commands generator.
     *
     * @param modelFile the model file to be used
     * @param outFile the out file
     * @param validate true for model validation, false otherwise
     * @param appName application name for printouts
     */
    public Model2Cli(final String modelFile, final OutputFile outFile, final boolean validate, final String appName) {
        Validate.notNull(modelFile, "Model2Cli: given model file name was blank");
        Validate.notNull(appName, "Model2Cli: given application name was blank");
        this.modelFile = modelFile;
        this.outFile = outFile;
        this.appName = appName;
        this.validate = validate;
    }

    /**
     * Runs the application.
     *
     * @return status of the application execution, 0 for success, positive integer for exit condition (such as help or
     *         version), negative integer for errors
     * @throws ApexException if any problem occurred in the model
     */
    public int runApp() throws ApexException {
        final CGCliEditor codeGen = new CGCliEditor();

        final ApexModelFactory factory = new ApexModelFactory();
        final ApexModel model = factory.createApexModel(new Properties(), true);

        final ApexAPIResult result = model.loadFromFile(modelFile);
        if (result.isNOK()) {
            System.err.println(appName + ": " + result.getMessage());
            return -1;
        }

        final AxPolicyModel policyModel = model.getPolicyModel();
        policyModel.register();

        if (validate) {
            final AxValidationResult val = new AxValidationResult();
            policyModel.validate(val);
            if (!val.isOK()) {
                System.err.println("Cannot translate the model. The model is not valid: \n" + val.toString());
                return -1;
            }
        }

        kig = new KeyInfoGetter(policyModel);

        // Order is important. 0: model, 1: context schemas, 2: tasks, 3: events, 4: ContextAlbums, 5: Policies
        // 0: model
        final AxArtifactKey pmkey = policyModel.getKey();
        codeGen.addModelParams(kig.getName(pmkey), kig.getVersion(pmkey), kig.getUUID(pmkey), kig.getDesc(pmkey));

        // 1: Context Schemas
        for (final AxContextSchema s : policyModel.getSchemas().getSchemasMap().values()) {
            final AxArtifactKey key = s.getKey();

            codeGen.addSchemaDeclaration(kig.getName(key), kig.getVersion(key), kig.getUUID(key), kig.getDesc(key),
                    s.getSchemaFlavour(), s.getSchema());
        }

        // 2: tasks
        for (final AxTask t : policyModel.getTasks().getTaskMap().values()) {
            final AxArtifactKey key = t.getKey();
            final List<ST> infields = getInfieldsForTask(codeGen, t);
            final List<ST> outfields = getOutfieldsForTask(codeGen, t);
            final ST logic = getLogicForTask(codeGen, t);
            final List<ST> parameters = getParametersForTask(codeGen, t);
            final List<ST> contextRefs = getCtxtRefsForTask(codeGen, t);

            codeGen.addTaskDeclaration(kig.getName(key), kig.getVersion(key), kig.getUUID(key), kig.getDesc(key),
                    infields, outfields, logic, parameters, contextRefs);
        }

        // 3: events
        for (final AxEvent e : policyModel.getEvents().getEventMap().values()) {
            final AxArtifactKey key = e.getKey();
            final List<ST> fields = getParametersForEvent(codeGen, e);

            codeGen.addEventDeclaration(kig.getName(key), kig.getVersion(key), kig.getUUID(key), kig.getDesc(key),
                    e.getNameSpace(), e.getSource(), e.getTarget(), fields);
        }

        // 4: context albums
        for (final AxContextAlbum a : policyModel.getAlbums().getAlbumsMap().values()) {
            final AxArtifactKey key = a.getKey();

            codeGen.addContextAlbumDeclaration(kig.getName(key), kig.getVersion(key), kig.getUUID(key),
                    kig.getDesc(key), a.getScope(), a.isWritable(), kig.getName(a.getItemSchema()),
                    kig.getVersion(a.getItemSchema()));
        }

        // 5: policies
        for (final AxPolicy p : policyModel.getPolicies().getPolicyMap().values()) {
            final AxArtifactKey key = p.getKey();
            final List<ST> states = getStatesForPolicy(codeGen, p);
            codeGen.addPolicyDefinition(kig.getName(key), kig.getVersion(key), kig.getUUID(key), kig.getDesc(key),
                    p.getTemplate(), p.getFirstState(), states);
        }

        final String out = codeGen.getModel().render();
        if (outFile != null) {
            try {
                final Writer w = outFile.toWriter();
                if (w == null) {
                    System.err.println("Error writing output to file " + outFile);
                    return -1;
                }
                w.write(out);
                w.close();
            } catch (final IOException e) {
                System.err.println("Error writing output to file " + outFile + ": " + e.getMessage());
                return -1;
            }
        } else {
            System.err.println(out);
        }
        return 0;
    }

    /**
     * Gets the parameters for event.
     *
     * @param cg the code generator
     * @param event the event
     * @return the parameters for event
     */
    private List<ST> getParametersForEvent(final CGCliEditor cg, final AxEvent event) {
        final Collection<AxField> fields = event.getFields();
        final List<ST> ret = new ArrayList<>(fields.size());
        for (final AxField f : fields) {
            final AxReferenceKey fkey = f.getKey();

            final ST val = cg.createEventFieldDefinition(kig.getPName(fkey), kig.getPVersion(fkey), kig.getLName(fkey),
                    kig.getName(f.getSchema()), kig.getVersion(f.getSchema()), f.getOptional());

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the context references for task.
     *
     * @param cg the code generator
     * @param task the task
     * @return the context references for task
     */
    private List<ST> getCtxtRefsForTask(final CGCliEditor cg, final AxTask task) {
        final Collection<AxArtifactKey> ctxs = task.getContextAlbumReferences();
        final List<ST> ret = new ArrayList<>(ctxs.size());
        final AxArtifactKey tkey = task.getKey();
        for (final AxArtifactKey ckey : ctxs) {

            final ST val = cg.createTaskDefinitionContextRef(kig.getName(tkey), kig.getVersion(tkey), kig.getName(ckey),
                    kig.getVersion(ckey));

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the parameters for task.
     *
     * @param cg the code generator
     * @param task the task
     * @return the parameters for task
     */
    private List<ST> getParametersForTask(final CGCliEditor cg, final AxTask task) {
        final Collection<AxTaskParameter> pars = task.getTaskParameters().values();
        final List<ST> ret = new ArrayList<>(pars.size());
        for (final AxTaskParameter p : pars) {
            final AxReferenceKey pkey = p.getKey();

            final ST val = cg.createTaskDefinitionParameters(kig.getPName(pkey), kig.getPVersion(pkey),
                    kig.getLName(pkey), p.getTaskParameterValue());

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the logic for task.
     *
     * @param cg the code generator
     * @param task the task
     * @return the logic for task
     */
    private ST getLogicForTask(final CGCliEditor cg, final AxTask task) {
        final AxArtifactKey tkey = task.getKey();
        final AxTaskLogic tl = task.getTaskLogic();

        final ST val =
                cg.createTaskDefLogic(kig.getName(tkey), kig.getVersion(tkey), tl.getLogicFlavour(), tl.getLogic());

        return val;
    }

    /**
     * Gets the output fields for task.
     *
     * @param cg the code generator
     * @param task the task
     * @return the output fields for task
     */
    private List<ST> getOutfieldsForTask(final CGCliEditor cg, final AxTask task) {
        final Collection<? extends AxField> fields = task.getOutputFields().values();
        final List<ST> ret = new ArrayList<>(fields.size());
        for (final AxField f : fields) {
            final AxReferenceKey fkey = f.getKey();

            final ST val = cg.createTaskDefinitionOutfields(kig.getPName(fkey), kig.getPVersion(fkey),
                    kig.getLName(fkey), kig.getName(f.getSchema()), kig.getVersion(f.getSchema()));

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the input fields for task.
     *
     * @param cg the code generator
     * @param task the task
     * @return the input fields for task
     */
    private List<ST> getInfieldsForTask(final CGCliEditor cg, final AxTask task) {
        final Collection<? extends AxField> fields = task.getInputFields().values();
        final List<ST> ret = new ArrayList<>(fields.size());
        for (final AxField f : fields) {
            final AxReferenceKey fkey = f.getKey();

            final ST val = cg.createTaskDefinitionInfields(kig.getPName(fkey), kig.getPVersion(fkey),
                    kig.getLName(fkey), kig.getName(f.getSchema()), kig.getVersion(f.getSchema()));

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the states for policy.
     *
     * @param cg the code generator
     * @param pol the policy
     * @return the states for policy
     */
    private List<ST> getStatesForPolicy(final CGCliEditor cg, final AxPolicy pol) {
        final Collection<AxState> states = pol.getStateMap().values();
        final List<ST> ret = new ArrayList<>(states.size());
        for (final AxState st : states) {
            final AxReferenceKey skey = st.getKey();
            final List<ST> outputs = getStateOutputsForState(cg, st);
            final List<ST> finalizerLogics = getFinalizersForState(cg, st);
            final List<ST> tasks = getTaskRefsForState(cg, st);
            final List<ST> tsLogic = getTSLForState(cg, st);
            final List<ST> ctxRefs = getCtxtRefsForState(cg, st);

            final ST val = cg.createPolicyStateDef(kig.getPName(skey), kig.getPVersion(skey), kig.getLName(skey),
                    kig.getName(st.getTrigger()), kig.getVersion(st.getTrigger()), kig.getName(st.getDefaultTask()),
                    kig.getVersion(st.getDefaultTask()), outputs, tasks, tsLogic, finalizerLogics, ctxRefs);

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the finalizers for state.
     *
     * @param cg the code generator
     * @param st the state
     * @return the finalizers for state
     */
    private List<ST> getFinalizersForState(final CGCliEditor cg, final AxState st) {
        final Collection<AxStateFinalizerLogic> fins = st.getStateFinalizerLogicMap().values();
        final List<ST> ret = new ArrayList<>(fins.size());
        final AxReferenceKey skey = st.getKey();
        for (final AxStateFinalizerLogic fin : fins) {
            final AxReferenceKey finkey = fin.getKey();

            final ST val = cg.createPolicyStateDefFinalizerLogic(kig.getPName(skey), kig.getPVersion(skey),
                    kig.getLName(skey), kig.getLName(finkey), fin.getLogicFlavour(), fin.getLogic());

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the context references for state.
     *
     * @param cg the code generator
     * @param st the state
     * @return the context references for state
     */
    private List<ST> getCtxtRefsForState(final CGCliEditor cg, final AxState st) {
        final Collection<AxArtifactKey> ctxs = st.getContextAlbumReferences();
        final List<ST> ret = new ArrayList<>(ctxs.size());
        final AxReferenceKey skey = st.getKey();
        for (final AxArtifactKey ctx : ctxs) {

            final ST val = cg.createPolicyStateDefContextRef(kig.getPName(skey), kig.getPVersion(skey),
                    kig.getLName(skey), kig.getName(ctx), kig.getVersion(ctx));

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the Task Selection Logic for state.
     *
     * @param cg the code generator
     * @param st the state
     * @return the TSL for state (if any) in a list
     */
    private List<ST> getTSLForState(final CGCliEditor cg, final AxState st) {
        final AxReferenceKey skey = st.getKey();
        if (st.checkSetTaskSelectionLogic()) {
            final AxTaskSelectionLogic tsl = st.getTaskSelectionLogic();
            final ST val = cg.createPolicyStateDefTaskSelLogic(kig.getPName(skey), kig.getPVersion(skey),
                    kig.getLName(skey), tsl.getLogicFlavour(), tsl.getLogic());
            return Collections.singletonList(val);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets the task references for state.
     *
     * @param cg the code generator
     * @param st the state
     * @return the task references for state
     */
    private List<ST> getTaskRefsForState(final CGCliEditor cg, final AxState st) {
        final Map<AxArtifactKey, AxStateTaskReference> taskrefs = st.getTaskReferences();
        final List<ST> ret = new ArrayList<>(taskrefs.size());
        final AxReferenceKey skey = st.getKey();
        for (final Entry<AxArtifactKey, AxStateTaskReference> e : taskrefs.entrySet()) {
            final AxArtifactKey tkey = e.getKey();
            final AxStateTaskReference tr = e.getValue();
            final AxReferenceKey trkey = tr.getKey();

            final ST val = cg.createPolicyStateTask(kig.getPName(skey), kig.getPVersion(skey), kig.getLName(skey),
                    kig.getLName(trkey), kig.getName(tkey), kig.getVersion(tkey), tr.getStateTaskOutputType().name(),
                    kig.getLName(tr.getOutput()));

            ret.add(val);
        }
        return ret;
    }

    /**
     * Gets the state outputs for state.
     *
     * @param cg the code generator
     * @param st the state
     * @return the state outputs for state
     */
    private List<ST> getStateOutputsForState(final CGCliEditor cg, final AxState st) {
        final Collection<AxStateOutput> outs = st.getStateOutputs().values();
        final List<ST> ret = new ArrayList<>(outs.size());
        final AxReferenceKey skey = st.getKey();
        for (final AxStateOutput out : outs) {
            final AxReferenceKey outkey = out.getKey();

            final ST val = cg.createPolicyStateOutput(kig.getPName(skey), kig.getPVersion(skey), kig.getLName(skey),
                    kig.getLName(outkey), kig.getName(out.getOutgingEvent()), kig.getVersion(out.getOutgingEvent()),
                    kig.getLName(out.getNextState()));

            ret.add(val);
        }
        return ret;
    }

}
