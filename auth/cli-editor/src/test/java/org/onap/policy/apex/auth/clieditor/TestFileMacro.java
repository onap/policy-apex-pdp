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

package org.onap.policy.apex.auth.clieditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.ResourceUtils;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * Test FileMacro in the CLI.
 */
public class TestFileMacro {
	private String[] fileMacroArgs;

	private File tempModelFile;
	private File tempLogFile;

	@Before
	public void createTempFiles() throws IOException {
		tempModelFile = File.createTempFile("TestPolicyModel", ".json");
		tempLogFile   = File.createTempFile("TestPolicyModel", ".log");

		fileMacroArgs = new String[] {
				"-c",
				"src/test/resources/scripts/FileMacro.apex",
				"-l",
				tempLogFile.getCanonicalPath(),
				"-o",
				tempModelFile.getCanonicalPath(),
				"-if",
				"true"
		};
	}

	@After
	public void removeGeneratedModels() {
		tempModelFile.delete();
	}

	/**
	 * Test logic block macro in CLI scripts.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ApexModelException if there is an Apex error
	 */
	@Test
	public void testLogicBlock() throws IOException, ApexModelException {
		final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(fileMacroArgs);
		// We expect eight errors
		assertEquals(8, cliEditor.getErrorCount());

		// Read the file from disk
		final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
		modelReader.setValidateFlag(false);

		final URL writtenModelURL = ResourceUtils.getLocalFile(tempModelFile.getCanonicalPath());
		final AxPolicyModel writtenModel = modelReader.read(writtenModelURL.openStream());

		final URL compareModelURL = ResourceUtils.getLocalFile("src/test/resources/compare/FileMacroModel_Compare.json");
		final AxPolicyModel compareModel = modelReader.read(compareModelURL.openStream());

		// Ignore key info UUIDs
		writtenModel.getKeyInformation().getKeyInfoMap().clear();
		compareModel.getKeyInformation().getKeyInfoMap().clear();

		assertTrue(writtenModel.equals(compareModel));

		// The output event is in this file
		final File outputLogFile = new File(tempLogFile.getCanonicalPath());

		final String outputLogString = TextFileUtils
				.getTextFileAsString(outputLogFile.getCanonicalPath())
				.replace(Paths.get("").toAbsolutePath().toString() + File.separator, "")
				.replaceAll("\\s+", "");

		// We compare the log to what we expect to get
		final String outputLogCompareString = TextFileUtils
				.getTextFileAsString("src/test/resources/compare/FileMacro_Compare.log")
				.replaceAll("\\s+", "");

		// Check what we got is what we expected to get
		assertEquals(outputLogCompareString, outputLogString);
	}
}
