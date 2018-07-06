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
 * Create a Context Table
 */
function createEngineContextTable(wrapper) {
	var tableId = config.engineContext.tableId;
	var headers = config.engineContext.headers.map(function(a) {
		return a.title;
	});
	return createEngineTable($(wrapper), tableId, headers);
}

/*
 * Update each Context Table with the latest data
 */
function setEngineContextData(engineContextData) {
	var tableId = config.engineContext.tableId;
	for(var ecd in engineContextData) {
		var id = tableId + "_" + engineContextData[ecd].id;
		var existingTable = undefined;
		for(var ect in this.engineContextTables) {
			if(id === this.engineContextTables[ect].getAttribute("id")) {
				existingTable = this.engineContextTables[ect];
			}
		}
		var engineInfo = JSON.parse(engineContextData[ecd].engine_info);
		var contextAlbums = engineInfo.ContextAlbums;
		var data = [];
		
		for(var ca in contextAlbums) {
			var cAlbumn = contextAlbums[ca];
			data.push([cAlbumn.AlbumKey.name, cAlbumn.AlbumKey.version, JSON.stringify(cAlbumn.AlbumContent, undefined, 50)]);
		}
		
		var table = existingTable;
		// If no table already exists for the context, add one
		if(!table) {
			var wrapper = document.createElement("div");
			wrapper.setAttribute("class", "engineContextWrapper");
			var title = document.createElement("div");
			title.setAttribute("class", "engineContextTitle");
			title.innerHTML = engineContextData[ecd].id;
			$(wrapper).append(title);
			$("." + config.engineContext.parent).append(wrapper);
			table = createEngineContextTable(wrapper);
			table.setAttribute("id", id);
			table.style["margin-bottom"] = "10px";
			this.engineContextTables.push(table);
		}
		
		var old_data = $(table).data(tableId);
		if(!deepCompare(old_data, data)) {
			$(table).find("tbody").remove();
			var tbody = document.createElement("tbody");
			
			for(var d in data) {
				var rowData = data[d];
				var row = document.createElement("tr");
				var rowContent = "<tr>";
				for(var rd in rowData) {
					var tdClass = (rd == 2 ? "dataTd_wordwrap" : "dataTd");
					rowContent += "<td class=" + tdClass + ">" + rowData[rd] + "</td>";
				}
				rowContent += "</tr>";
				row.innerHTML = rowContent;
				$(tbody).append(row);
			}
			$(table).data(tableId, data);
			$(table).append(tbody);			
		}
		
	}
}