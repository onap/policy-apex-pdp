/*
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

/*
 * Create a table with given headers
 */
function createEngineTable(parent, id, tableHeaders) {
	var table = createTable(id);

	var tableHead = document.createElement("thead");
	table.appendChild(tableHead);
	tableHead.setAttribute("id", "engineTableHeader");

	var tableHeaderRow = document.createElement("tr");
	tableHead.appendChild(tableHeaderRow);
	tableHeaderRow.setAttribute("id", "engineTableHeaderRow");

	for (var t in tableHeaders) {
		var tableHeader = document.createElement("th");
		tableHeaderRow.appendChild(tableHeader);
		tableHeader.setAttribute("id", "engineTableHeader");
		tableHeader.appendChild(document.createTextNode(tableHeaders[t]));
	}

	var tableBody = document.createElement("tbody");
	tableBody.setAttribute("id", "engineTableBody");
	table.appendChild(tableBody);

	parent.append(table);

	return table;
}

/*
 * Create a table and apply UISDK styles to it
 */
function createTable(id) {
	var table = document.createElement("table");
	table.setAttribute("id", id);
	table.setAttribute("class", "apexTable ebTable elTablelib-Table-table ebTable_striped");
	return table;
}