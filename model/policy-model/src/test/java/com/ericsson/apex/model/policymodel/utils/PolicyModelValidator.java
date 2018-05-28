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

package com.ericsson.apex.model.policymodel.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

public class PolicyModelValidator {
    public static void main(final String[] args) throws ApexModelException, FileNotFoundException {
        final ApexModelReader<AxPolicyModel> policyModelReader =
                new ApexModelReader<AxPolicyModel>(AxPolicyModel.class);


        final AxPolicyModel policyModel = policyModelReader.read(new FileInputStream(args[0]));
        final AxValidationResult result = policyModel.validate(new AxValidationResult());
        System.out.println(result);
    }
}
