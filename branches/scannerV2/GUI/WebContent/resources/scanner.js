/*
 * Copyright 2012 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * SCANNER UI
 * 
 * @author Serban Voinea
 * 
 */

var MAX_RETRIES = 1;
var AJAX_TIMEOUT = 300000;
var HOME;

var oQueryTable = null;
var oAnalyzeTable = null;

var oStatusQueryTable = null;
var oStatusAnalyzeTable = null;

var sitesMap = null;

var selStudies = null;
var selDatasets = null;
var selLibraries = null;
var selMethods = null;
var selSites = null;
var studiesMultiSelect = null;
var datasetsMultiSelect = null;
var librariesMultiSelect = null;
var methodsMultiSelect = null;
var sitesMultiSelect = null;
var queryResult = {};
var analyzeResult = {};

var availableStudies = {};
var availableDatasets = {};
var availableLibraries = {};
var availableMethods = {};
var availableSites = {};
var availableParameters = {};

var descriptionId = 0;
var tipBox;
var alertErrorDialog;

var investigatorsMultiSelect = null;
var selInvestigators = null;


var emptyValue = ['&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'];

var analyzeLayout = [[
                      {'name': 'Date', 'field': 'date', 'width': '15%'},
                      {'name': 'Study', 'field': 'study', 'width': '15%'},
                      {'name': 'Dataset', 'field': 'dataset', 'width': '15%'},
                      {'name': 'Library', 'field': 'library','width': '10%'},
                      {'name': 'Method', 'field': 'method', 'width': '15%'},
                      {'name': 'Status', 'field': 'status', 'width': '15%'},
                      {'name': 'Options', 'field': 'options', 'width': '15%'}
                      ]];

var analyzeDataStore = {
		identifier: "id",
		items: []
};

var slicesDataStore = {
		identifier: "id",
		items: []
};

var projectsDataStore = {
		identifier: "id",
		items: []
};

var intervalVariable;

var analyzeGrid = null;
var slicesGrid = null;
var projectsGrid = null;
var queriesCounter = 0;
var studiesCounter = [];

var parametersDescription = null;
var parametersBody = null;

var postResult = null;
var scrollbarWidth;

/**
 * Method "contains" for the Array object
 * returns true if an element is in the array, and false otherwise
 */
Array.prototype.contains = function (elem) {
	for (i in this) {
		if (this[i] == elem) return true;
	}
	return false;
};

/**
 * Function called after the SCANNER page was loaded
 */
function initScanner() {
	var val = '' + window.location;
	var len = val.length - 1;
	if (val[len] == '/') {
		val = val.substr(0, len);
	}
	HOME = val;
	require(["dojo/ready", "dijit/form/MultiSelect", "dijit/form/Button", "dojo/dom", "dojo/_base/window", "dojo/parser", "dijit/layout/TabContainer", "dijit/layout/ContentPane"], function(ready) {
		ready(function(){
			$('#ui').css('display', 'none');
			$('#welcome').css('display', '');
			$('#loginForm').css('display', '');
			$('#footer').css('display', '');
		});
	});
	$('#acceptConditions').removeAttr('checked');
	$('#continueButton').attr('disabled', 'disabled');
	tipBox = $('#TipBox');
	$('#Alert_Error_Dialog').css('display', '');
	alertErrorDialog = $('#Alert_Error_Dialog');
	alertErrorDialog.dialog({
		autoOpen: false,
		title: 'Error',
		buttons: {
			'OK': function() {
					$(this).dialog('close');
				}
		},
		draggable: true,
		modal: false,
		resizable: true,
		width: 500
	});
	$('#projectsListDiv').tabs({
		activate: function(event, ui) {
			var divId = ui.newPanel.attr('id');
			if (divId == 'newProjectDiv') {
				$('#studyTitleInput').val('');
				$('#createStudyButton').attr('disabled', 'disabled');
				$('#studyTitleInput').unbind('keyup');
				$('#studyTitleInput').keyup(function(event) {checkCreateStudyButton();});
				
				$('#createStudyIRBInput').val('');
				$('#createStudyPrincipalInvestigator').val('');
				$('#createStudyPrincipalInvestigator').unbind('change');
				var select = $('#createStudyPrincipalInvestigator');
				select.html('');
				var option = $('<option>');
				option.text('Enter Letters of Name...');
				option.attr('value', '');
				select.append(option);
				$.each(scannerUsersList, function(i, user) {
					option = $('<option>');
					option.text(user['firstName'] + ' ' + user['lastName']);
					option.attr('value', user['userId']);
					select.append(option);
				});
				select.change(function(event) {checkCreateStudyButton();});
			} else if (divId == 'manageStudiesDiv') {
				manageStudy(true);
			} else if (divId == 'myStudiesDiv') {
				collapseMyStudies();
			}
		}
	});
	scrollbarWidth = getScrollbarWidth();
	submitLogin();
}

/**
 * @return the value of the selected study
 */
function getSelectedStudyName() {
	var ret = studiesMultiSelect.get('value')[0];
	if (activeStudy != null) {
		ret = activeStudy['studyName'];
	}
	return ret;
}

/**
 * @return the value of the selected dataset
 */
function getSelectedDatasetName() {
	return datasetsMultiSelect.get('value')[0];
}

/**
 * @return the value of the selected library
 */
function getSelectedLibraryName() {
	return librariesMultiSelect.get('value')[0];
}

/**
 * @return the value of the selected method
 */
function getSelectedMethodName() {
	return methodsMultiSelect.get('value')[0];
}

/**
 * @return the value of the selected sites
 */
function getSelectedSitesNames() {
	return sitesMultiSelect.get('value');
}

function getSelectedInvestigatorsNames() {
	return investigatorsMultiSelect.get('value');
}

/**
 * @return the string representing the JSONObject of the selected parameters
 * Example: 
   {
    "dependentVariableName":"Outcome",
    "independentVariableNames":["Age","Race_Cat","Creatinine","CAD","LOS","Diabetes"]
   }
 */
function getSelectedParameters() {
	var query = {};
	var ret = true;
	$.each(parametersBody, function(i, param) {
		ret = buildParameterBody(param, query);
		if (!ret) {
			return false;
		}
	});
	var result = null;
	if (ret) {
		result = valueToString(query);
	}
	return result;
}

function buildParameterBody(param, query) {
	var ret = true;
	$.each(param, function(key, value) {
	var metadata = value['metadata'];
		if (metadata['parameterType'] == 'group') {
			if (metadata['linkedParameter'] != null) {
				// dependentVariableName
				query[metadata['cname']] = $('#select_'+makeId(metadata['cname'])).val();
			} else if (metadata['arrayParameter'] != null) {
				// independentVariableName
				var values = [];
				$.each($('input:checked', $('#table_'+makeId(metadata['cname']))), function(i, elem) {
					values.push($(elem).attr('parValue'));
				});
				query[metadata['cname']] = values;
			} else {
				var obj = {};
				query[metadata['cname']] = obj;
				$.each(value['data'], function(i, elem) {
					ret = buildParameterBody(elem, obj);
					if (!ret) {
						return false;
					}
				});
			}
		} else if (metadata['parameterType'] == 'enum') {
			var values = [];
			$.each($('input:checked', $('#table_'+makeId(metadata['cname']))), function(i, elem) {
				values.push($(elem).attr('parValue'));
			});
			if (values.length > 0) {
				query[metadata['cname']] = values[0];
			}
		} else if (metadata['parameterType'] == 'auto') {
			query[metadata['cname']] = $('#param_'+makeId(metadata['cname'])).val();
		} else if (metadata['parameterType'] == 'integer') {
			var val = $('#param_'+makeId(metadata['cname'])).val().replace(/^\s*/, "").replace(/\s*$/, "");
			if (!isNaN(parseInt(val))) {
				query[metadata['cname']] = parseInt(val);
			} else if (val.length > 0) {
				alert('Invalid integer value for "' + metadata['cname'] + '": "' + val + '".');
				ret = false;
			} else if (val.length == 0 && metadata['minOccurs'] == 1) {
				alert('Please specify a value for "' + metadata['cname'] + '".');
				ret = false;
			}
		} else if (metadata['parameterType'] == 'text') {
			query[metadata['cname']] = $('#param_'+makeId(metadata['cname'])).val();
		}
	});
	return ret;
}

/**
 * Send the request to get the sitest status
 */
function renderSitesStatus() {
	// send the request
	var obj = {};
	var url = HOME + '/query';
	obj['action'] = 'displaySitesStatus';
	scanner.POST(url, obj, true, postRenderSitesStatus, null, null, 0);
}

/**
 * Render the sites status
 * 
 * @param data
 * 	the data returned from the server (a JSONObject)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 */
function postRenderSitesStatus(data, textStatus, jqXHR, param) {
	data = $.parseJSON(data);
	var tbody = $('#sitesStatusBody');
	tbody.html('');
	sitesMap = data['sitesMap'];
	var sitesStatus = data['echo']['ServiceResponses']['ServiceResponse'];
	if (!$.isArray(sitesStatus)) {
		sitesStatus = [];
		sitesStatus.push(data['echo']['ServiceResponses']['ServiceResponse']);
	}
	sitesStatus.sort(compareServiceResponseMetadata);
	var refreshTimestamp = $('#refreshTimestamp');
	refreshTimestamp.html(data['timestamp']);
	var successConnections = [];
	var errorConnections = [];
	$.each(sitesStatus, function(i, elem) {
		if (elem['ServiceResponseMetadata']['RequestState'] == 'Complete') {
			successConnections.push(elem['ServiceResponseMetadata']);
		} else {
			errorConnections.push(elem['ServiceResponseMetadata']);
		}
	});
	$.each(successConnections, function(i, siteInfo) {
		var siteName = siteInfo['RequestSiteInfo']['SiteName'];
		var url = siteInfo['RequestURL'];
		siteName = getSiteName(siteName, url);
		var tr = $('<tr>');
		tbody.append(tr);
		var td = $('<td>');
		tr.append(td);
		var img = $('<img>');
		img.attr({'alt': 'Up',
			'title': 'Status Up',
			'src': 'resources/images/green_circle.png',
			'height': '10'
			});
		td.append(img);
		var label = $('<label>');
		td.append(label);
		label.html(siteName);
		var description = 'Status: Up<br/>Description: ' + siteInfo['RequestSiteInfo']['SiteDescription']; 
		label.hover(
				function(event) {DisplayTipBox(event, description);}, 
				function(){HideTipBox();});
	});
	$.each(errorConnections, function(i, siteInfo) {
		var siteName = siteInfo['RequestSiteInfo']['SiteName'];
		var url = siteInfo['RequestURL'];
		siteName = getSiteName(siteName, url);
		var tr = $('<tr>');
		tbody.append(tr);
		var td = $('<td>');
		tr.append(td);
		var img = $('<img>');
		img.attr({'alt': 'Down',
			'title': 'Status Down',
			'src': 'resources/images/red_circle.png',
			'height': '10'
			});
		td.append(img);
		var label = $('<label>');
		td.append(label);
		label.html(siteName);
		var errorsStatistics = data['errorsStatistics'];
		var total = errorsStatistics['total'];
		var rate = null;
		if (url != null) {
			rate = Math.floor(errorsStatistics[url[0]] * 100 / total);
		}
		var description = 'Status: Down<br/>' + 
			(url != null ? 'Failure Rate: ' + errorsStatistics[url[0]] + ' of ' + total + ' (' + rate + '%)<br/>' : '') +
			'RequestURL: ' + siteInfo['RequestURL'] + '<br/>' + 
			'RequestStateDetail: ' + siteInfo['RequestStateDetail'];
		label.hover(
				function(event) {DisplayTipBox(event, description);}, 
				function(){HideTipBox();});
	});

	// refresh every minute
	setTimeout("renderSitesStatus()", 2*60*1000);
}

/**
 * Send the request to get the available studies
 */
function renderAvailableStudies() {
	// some clean up
	$('#paramsDiv').css('display', 'none');
	
	// send the request
	var url = HOME + '/query?action=getStudies';
	scanner.GET(url, true, postRenderAvailableStudies, null, null, 0);
}

/**
 * Render the available studies
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 * Example of the format of the received data:
   {"Study1":"Study1"}
 */
function postRenderAvailableStudies(data, textStatus, jqXHR, param) {
	availableStudies = data;
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	if (names.length > 0) {
		names.sort(compareIgnoreCase);
		loadDatasets(emptyValue);
		loadLibraries(emptyValue);
		loadMethods(emptyValue);
		loadSites(emptyValue, false);
		loadStudies(names);
		intervalVariable = setInterval(function(){checkStudiesRendering(names.length);},1);
	} else {
		$('#ui').hide();
		$('#authorizationWrapperDiv').show();
	}
}

function checkStudiesRendering(len) {
	if ($('#selectStudies').children().length == len) {
		clearInterval(intervalVariable);
		$('#selectStudies').attr('disabled', 'disabled');
	}
}

/**
 * Send the request to get the available datasets
 */
function renderAvailableDatasets() {
	// send the request
	var study = getSelectedStudyName();
	if (study != '') {
		var url = HOME + '/query?action=getDatasets&study=' + encodeSafeURIComponent(study);
		scanner.GET(url, true, postRenderAvailableDatasets, null, null, 0);
	}
}

/**
 * Render the available datasets
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 * Example of the format of the received data:
   {"Dataset1":"Study1_Dataset1"}
 */
function postRenderAvailableDatasets(data, textStatus, jqXHR, param) {
	availableDatasets = data;
	var datasets = [];
	$.each(data, function(dataset, name) {
		datasets.push(dataset);
	});
	datasets.sort(compareIgnoreCase);
	if (datasets.length > 0) {
		loadDatasets(datasets);
	}
}

/**
 * Send the request to get the available libraries
 */
function renderAvailableLibraries() {
	// send the request
	var dataset = getSelectedDatasetName();
	if (dataset != null && dataset.replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && dataset != emptyValue) {
		var url = HOME + '/query?action=getLibraries' +
			'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
			'&dataset=' + encodeSafeURIComponent(getSelectedDatasetName());
		scanner.GET(url, true, postRenderAvailableLibraries, null, null, 0);
	}
}

/**
 * Render the available libraries
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 * Example of the format of the received data:
   {"Oceans":"Study1_Dataset1_oceans",
    "GLORE":"Study1_Dataset1_glore"}
 */
function postRenderAvailableLibraries(data, textStatus, jqXHR, param) {
	availableLibraries = data;
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	loadLibraries(names);
}

/**
 * Send the request to get the available methods
 */
function renderAvailableMethods() {
	var lib = getSelectedLibraryName();
	if (lib != '' && lib.replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && lib != emptyValue) {
		//$('#libraryHelp').html(availableLibraries[lib]);
		var url = HOME + '/query?action=getMethods' +
			'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
			'&library=' + encodeSafeURIComponent(getSelectedLibraryName()) +
			'&dataset=' + encodeSafeURIComponent(getSelectedDatasetName());
		scanner.GET(url, true, postRenderAvailableMethods, null, null, 0);
	}
}

/**
 * Render the available methods
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 * Example of the format of the received data:
   {"Logistic Regression":"Study1_Dataset1_oceans_lr"}
 */
function postRenderAvailableMethods(data, textStatus, jqXHR, param) {
	availableMethods = data;
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	loadMethods(names);
}

/**
 * Send the request to get the available parameters
 */
function renderAvailableParameters() {
	// send the request
	var method = getSelectedMethodName();
	var dataset = getSelectedDatasetName();
	if (method != null && method.replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && method != emptyValue) {
		var url = HOME + '/query?action=getParameters&dataset=' + encodeSafeURIComponent(dataset);
		scanner.GET(url, true, postRenderAvailableParameters, null, null, 0);
	}
}

/**
 * Render the available parameters
 * 
 * @param data
 * 	the data returned from the server (a JSONArray)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 * Example of the format of the received data:
   [{"Study1_Dataset1_oceans_lr_dependentVariableName":{"minOccurs":1,
                                                      "values":["Outcome"],
                                                      "cname":"dependentVariableName",
                                                      "maxOccurs":1}},
     {"Study1_Dataset1_oceans_lr_independentVariableNames":{"minOccurs":0,
                                                         "values":["Age","CAD","Creatinine","Diabetes","LOS","Race_Cat"],
                                                         "cname":"independentVariableNames",
                                                         "maxOccurs":-1}}
   ]
 */
var linkedParameters = {};

function postRenderAvailableParameters(data, textStatus, jqXHR, param) {
	parametersBody = data;
	$('#paramsDiv').css('display', '');
	$('#statusQueryWrapperDiv').hide();
	$('#queryDiv').hide();
	var paramsDiv = $('#paramsDivContent');
	paramsDiv.html('');
	linkedParameters = {};
	
	var table = $('<table>');
	table.attr('id', 'table_');
	paramsDiv.append(table);

	$.each(data, function(i, param) {
		renderParam(paramsDiv, param, 1, table, null);
	});

	$.each(linkedParameters, function(id, linkedParameter) {
		disableLinkedVariableName(id, linkedParameter);
	});

}

function disableLinkedVariableName(id, linkedParameter) {
	var val = $('#'+id).val();
	if (val != null) {
		$('input:checkbox', $('#table_' + makeId(linkedParameter))).removeAttr('disabled');
		$('#param_' + makeId(linkedParameter) + '_' + makeId(val)).removeAttr('checked');
		$('#param_' + makeId(linkedParameter) + '_' + makeId(val)).attr('disabled', 'disabled');
		if (val == '') {
			$('#submitQueryButton').attr('disabled', 'disabled');
		} else {
			$('#submitQueryButton').removeAttr('disabled');
		}
	}
}

function renderParam(div, param, index, table, select) {
	$.each(param, function(key, value) {
		var metadata = value['metadata'];
		if (metadata['parameterType'] == 'group') {
			table.remove();
			var h = $('<h'+index+'>');
			div.append(h);
			h.html(metadata['cname']);
			var parTable = $('<table>');
			parTable.attr('id', 'table_'+makeId(metadata['cname']));
			div.append(parTable);
			var selectPar = null;
			if (metadata['linkedParameter'] != null) {
				var tr = $('<tr>');
				parTable.append(tr);
				var td = $('<td>');
				tr.append(td);
				selectPar = $('<select>');
				selectPar.attr('id', 'select_'+makeId(metadata['cname']));
				selectPar.change({'id': 'select_'+makeId(metadata['cname']),
							'linkedParameter': metadata['linkedParameter']}, function(event) {disableLinkedVariableName(event.data.id, event.data.linkedParameter);});
				td.append(selectPar);
				linkedParameters['select_'+makeId(metadata['cname'])] = metadata['linkedParameter'];
			}
			$.each(value['data'], function(i, elem) {
				renderParam(div, elem, index+1, parTable, selectPar);
			});
		} else if (metadata['parameterType'] == 'enum') {
			if (select != null) {
				var option = $('<option>');
				option.text(metadata['cname']);
				option.attr('value', metadata['text']);
				if (metadata['selected'] != null) {
					option.attr('selected', 'selected');
				}
				select.append(option);
				option.hover(
						function(event) {DisplaySelectTipBox(event, 'Variable Type: ' + metadata['variableType']);}, 
						function(){HideSelectTipBox();});
			} else {
				var trNo = $('tr', table).length;
				var tr = null;
				if (trNo == 0) {
					table.css('border-spacing', '10px 0px');
					tr = $('<tr>');
					table.append(tr);
				} else {
					tr = $($('tr', table)[trNo-1]);
					if ($('td', tr).length != 1) {
						tr = $('<tr>');
						table.append(tr);
					}
				}
				var td = $('<td>');
				tr.append(td);
				var tableId = table.attr('id');
				tableId = tableId.substring('table_'.length);
				var input = $('<input>');
				input.attr({'type': 'checkbox',
					'checked': 'checked',
					'parName': metadata['cname'],
					'id': 'param_' + tableId + '_' + makeId(metadata['cname']),
					'parValue': metadata['cname']});
				td.append(input);
				var label = $('<label>');
				td.append(label);
				label.html(metadata['cname']);
				label.hover(
						function(event) {DisplayTipBox(event, metadata['description']);}, 
						function(){HideTipBox();});
			}
		} else if (metadata['parameterType'] == 'auto') {
			var tr = $('<tr>');
			table.append(tr);
			var td = $('<td>');
			tr.append(td);
			td.html(metadata['cname'] + ':');
			td = $('<td>');
			tr.append(td);
			var input = $('<input>');
			input.attr({'type': 'text',
				'parName': metadata['cname'],
				'id': 'param_'+makeId(metadata['cname'])});
			input.val(++descriptionId);
			input.attr('disabled', 'disabled');
			td.append(input);
		} else {
			var tr = $('<tr>');
			table.append(tr);
			var td = $('<td>');
			tr.append(td);
			td.html(metadata['cname'] + ':');
			td = $('<td>');
			tr.append(td);
			var input = $('<input>');
			input.attr({'type': 'text',
				'parName': metadata['cname'],
				'id': 'param_'+makeId(metadata['cname'])});
			td.append(input);
		}
	});
}

/**
 * Send the request to get the available sites
 */
function renderAvailableSites() {
	var dataset = getSelectedDatasetName();
	var url = HOME + '/query?action=getSites' +
	'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
	'&dataset=' + encodeSafeURIComponent(getSelectedDatasetName());
	scanner.GET(url, true, postRenderSites, null, null, 0);
}

/**
 * Render the available sites
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 * Example of the format of the received data:
   {"Dataset1":"Study1_Dataset1"}
 */
function postRenderSites(data, textStatus, jqXHR, param) {
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	loadSites(names, false);
}

/**
 * Send the request to get the data from the SCANNER
 */
function submitQuery(div, replay, checkStatus, resultData) {
	if (div == 'queryDivContent') {
		$('#expandQueryResultsArrow').hide();
		$('#collapseQueryResultsArrow').show();
	}
	var obj = {};
	var param = {};
	if (replay == null) {
		param.spinner = $('#ajaxSpinnerImage');
		param.treeId = 'navigation';
		param.tableId = 'queryExample';
		param.statusTableId = 'statusQueryExample';
	} else {
		param.spinner = $('#ajaxAnalyzeSpinnerImage');
		param.treeId = 'analyzeNavigation';
		param.tableId = 'analyzeExample';
		param.statusTableId = 'statusAnalyzeExample';
	}
	obj['action'] = ($('#asyncCheckBox').attr('checked') == 'checked') ? 'getResultsAsync' : 'getResults';
	if (replay == null) {
		var sites = getSelectedSitesNames();
		if (sites.length == 0) {
			alert('Please select the sites.');
			return;
		}
		var params = getSelectedParameters();
		if (params == null) {
			return;
		}
		obj['parameters'] = params;
		obj['study'] = getSelectedStudyName();
		obj['dataset'] = getSelectedDatasetName();
		obj['library'] = getSelectedLibraryName();
		obj['method'] = getSelectedMethodName();
		obj['sites'] = valueToString(sites);
		$('#profileQueryCount').html('' + ++queriesCounter);
		if (!studiesCounter.contains(obj['study'])) {
			studiesCounter.push(obj['study']);
			$('#profileStudyCount').html('' + studiesCounter.length);
		}
		$('#profileLastActivity').html(getDateString(new Date()));
		param['analyze'] = obj;
	} else {
		obj['trxId'] = replay['trxId'][0];
		obj['parameters'] = replay['parameters'][0];
		obj['study'] = replay['study'][0];
		obj['dataset'] = replay['dataset'][0];
		obj['library'] = replay['library'][0];
		obj['method'] = replay['method'][0];
		obj['sites'] = replay['sites'][0];
	}
	param['resultDiv'] = $('#' + div);
	param['statusDiv'] = (div == 'queryDivContent') ? $('#statusQueryDivContent') : $('#statusReplayDivContent');
	param['resultDiv'].parent().hide();
	param['statusDiv'].parent().hide();
	param['statusDiv'].parent().parent().hide();
	param['library'] = obj['library'];
	param['checkStatus'] = checkStatus;
	param.spinner.parent().show();
	param.spinner.show();
	var url = HOME + '/query';
	if (resultData == null) {
		scanner.POST(url, obj, true, postSubmitQuery, param, null, 0);
	} else {
		postSubmitQuery(resultData, null, null, param);
	}
}

/**
 * Render the results
 * 
 * @param data
 * 	the data returned from the server (a JSONObject)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 */
function postSubmitQuery(data, textStatus, jqXHR, param) {
	param.spinner.hide();
	param.spinner.parent().hide();
	if (param['checkStatus'] != -1) {
		// check if completed
		var temp = $.parseJSON(data);
		var serviceResponse = temp['data']['ServiceResponses']['ServiceResponse'];
		if (!$.isArray(serviceResponse)) {
			serviceResponse = [];
			serviceResponse.push(temp['data']['ServiceResponses']['ServiceResponse']);
		}
		var complete = true;
		var state = null;
		$.each(serviceResponse, function(i, elem) {
			if (elem['ServiceResponseMetadata'] != null && elem['ServiceResponseMetadata']['RequestState'] != 'Complete') {
				complete = false;
				if (elem['ServiceResponseMetadata']['RequestState'] != 'Held') {
					state = elem['ServiceResponseMetadata']['RequestState'];
				}
				return false;
			}
		});

		if (complete) {
			postRefreshQueryStatus(param['checkStatus'], 'Complete');
		} else if (state != null) {
			postRefreshQueryStatus(param['checkStatus'], state);
		}
	} else {
		postResult = data;
	}
	var resultDiv = param['resultDiv'];
	var statusDiv = param['statusDiv'];
	data = $.parseJSON(data);
	var dict = {};
	dict['data'] = data['data'];
	dict['resultDiv'] = resultDiv;
	dict['tableId'] = param.tableId;
	dict['statusDiv'] = statusDiv;
	dict['statusTableId'] = param.statusTableId;
	var tab;
	var obj = param['analyze'];
	var index;
	if (obj != null) {
		obj['trxId'] = data['trxId'];
		obj['complete'] = checkComplete(data['data']);
		obj['hasError'] = checkError(data['data']);
		index = analyzeDataStore.items.length + 1;
		pushAnalyze(obj);
		queryResult = dict;
		tab = 'Query';
	} else {
		analyzeResult = dict;
		tab = 'Analyze';
	}
	var async = data['async'];
	data = data['data'];
	resultDiv.html('');
	var a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildTable("'+tab+'");');
	a.html('Data table');
	resultDiv.append(a);
	resultDiv.append('&nbsp;&nbsp;');
	a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildBoxPlot("'+tab+'");');
	a.html('Box plot');
	resultDiv.append(a);
	buildStatusDataTable(tab);
	buildDataTable(tab);
	//buildBoxPlot(tab);
	/*
		if (async != null) {
			scannerTabContainer.forward();
			intervalVariable = setInterval(function(){checkAnalyzeDataStore(index);},1);
		}
	*/
}

function checkAnalyzeDataStore(index) {
	if (analyzeDataStore.items.length == index) {
		clearInterval(intervalVariable);
		displayQueryStatus(index-1);
	}
}

function buildTable(tab) {
	var resultDiv;
	if (tab == 'Query') {
		resultDiv = queryResult['resultDiv'];
	} else {
		resultDiv = analyzeResult['resultDiv'];
	}
	resultDiv.html('');
	var a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildTable("'+tab+'");');
	a.html('Data table');
	resultDiv.append(a);
	resultDiv.append('&nbsp;&nbsp;');
	a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildBoxPlot("'+tab+'");');
	a.html('Box plot');
	resultDiv.append(a);
	buildStatusDataTable(tab);
	buildDataTable(tab);
}

function buildBoxPlot(tab) {
	var resultDiv, responseBody;
	if (tab == 'Query') {
		resultDiv = queryResult['resultDiv'];
		responseBody = queryResult['data'];
	} else {
		resultDiv = analyzeResult['resultDiv'];
		responseBody = analyzeResult['data'];
	}
	resultDiv.parent().show();
	resultDiv.html('');
	var a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildTable("'+tab+'");');
	a.html('Data table');
	resultDiv.append(a);
	resultDiv.append('&nbsp;&nbsp;');
	a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildBoxPlot("'+tab+'");');
	a.html('Box plot');
	resultDiv.append(a);
	var sitesCoeficients = [];
	var output = [];
	getOutput(responseBody, output);
	$.each(output, function(i, value) {
		var coeficient = value['Coefficient'];
		if (!$.isArray(coeficient)) {
			var temp = [];
			temp.push(coeficient);
			coeficient = temp;
		}
		sitesCoeficients.push(coeficient);
	});
	//var allColumns = ['p-value', 't-statistics', 'B', 'SE', 'degreeOfFreedom'];
	var allColumns = ['p-value'];
	//var allColumns = ['t-statistics'];
	$.each(allColumns, function(i, col) {
		resultDiv.append($('<br>'));
		var h3 = $('<h3>');
		resultDiv.append(h3);
		h3.html(col);
		var boxChart = $('<div>');
		boxChart.addClass('results_border');
		resultDiv.append(boxChart);
		drawBox(sitesCoeficients, col, boxChart);
	});
}

/**
 * Function to get the columns from a JSONObject 
 * 
 * @param res
 * 	the data to get the columns from
 * @param columns
 * 	the output parameter to collect the columns
 */
function getColumnsNames(res, columns) {
	if ($.isPlainObject(res)) {
		if (res['Output'] != null) {
			var coefficient = null;
			if ($.isArray(res['Output'])) {
				coefficient = res['Output'][0]['Coefficient'];
			} else {
				coefficient = res['Output']['Coefficient'];
			}
			$.each(coefficient[0], function(col, val) {
				columns.push(col);
			});
		} else {
			$.each(res, function(key, value) {
				getColumnsNames(value, columns);
				if (columns.length > 0) {
					return false;
				}
			});
		}
	} else if ($.isArray(res)) {
		getColumnsNames(res[0], columns);
	}	
}

/**
 * Get the column values of a JSONObject 
 * 
 * @param res
 * 	the data of a row
 * @param columns
 * 	the name of the columns
 * @param rows
 * 	the output parameter to collect the values
 * 
 * @return the array with the columns values
*/
function getRowsValues(res, columns, rows, sites) {
	var serviceResponse = res['ServiceResponses']['ServiceResponse'];
	if (!$.isArray(serviceResponse)) {
		serviceResponse = [];
		serviceResponse.push(res['ServiceResponses']['ServiceResponse']);
	}
	$.each(serviceResponse, function(i, elem) {
		if (elem['ServiceResponseData'] != null) {
			sites.push(elem['ServiceResponseMetadata']);
			getSiteRowsValues(elem['ServiceResponseData'], columns, rows);
		}
	});
}
function getSiteRowsValues(res, columns, rows) {
	if ($.isPlainObject(res)) {
		if (res['Output'] != null) {
			var coefficient = null;
			if ($.isArray(res['Output'])) {
				$.each(res['Output'], function(i, output) {
					coefficient = output['Coefficient'];
					var group = [];
					$.each(coefficient, function(i, obj) {
						var row = [];
						$.each(columns, function(j, col) {
							row.push(obj[col]);
						});
						group.push(row);
					});
					rows.push(group);
				});
			} else {
				coefficient = res['Output']['Coefficient'];
				var group = [];
				$.each(coefficient, function(i, obj) {
					var row = [];
					$.each(columns, function(j, col) {
						row.push(obj[col]);
					});
					group.push(row);
				});
				rows.push(group);
			}
		} else {
			$.each(res, function(key, value) {
				getSiteRowsValues(value, columns, rows);
			});
		}
	} else if ($.isArray(res)) {
		$.each(res, function(i, val) {
			getSiteRowsValues(val, columns, rows);
		});
	}	
}

/**
 * Build a data table for a JSONObject
 * 
 * @param res
 * 	the JSONObject for the tree
 * @param resultDiv
 * 	the div to place the tree
 * @param tableId
 * 	the id of the table
 */

function buildDataTable(tab) {
	var res, resultDiv, tableId, dict;
	if (tab == 'Query') {
		dict = queryResult;
	} else {
		dict = analyzeResult;
	}
	res = dict['data'];
	resultDiv = dict['resultDiv'];
	tableId = dict['tableId'];
	var columns = ['name', 'B', 'SE', 'p-value', 't-statistics', 'degreeOfFreedom'];
	//var columns = [];
	//getColumnsNames(res, columns);
	var datasets = [];
	var sites = [];
	getRowsValues(res, columns, datasets, sites);
	if (datasets.length == 0) {
		return;
	}
	if (tableId == 'queryExample') {
		if (oQueryTable != null) {
			$('#' + tableId).remove();
			oQueryTable = null;
		}
	} else {
		if (oAnalyzeTable != null) {
			$('#' + tableId).remove();
			oAnalyzeTable = null;
		}
	}
	resultDiv.parent().css('display', '');
	var table = $('<table>');
	resultDiv.append(table);
	table.attr({	'cellpadding': '0',
			'cellspacing': '0',
			'border': '0',
			'id': tableId}); 
	table.addClass('display');
	var thead = $('<thead>');
	table.append(thead);
	var tr = $('<tr>');
	thead.append(tr);
	var th = $('<th>');
	tr.append(th);
	th.html('Sample');
	for (var i=0; i < columns.length; i++) {
		var th = $('<th>');
		tr.append(th);
		th.html(columns[i]);
	}
	var tbody = $('<tbody>');
	table.append(tbody);
	var groupNo = 0;
	var display = true;
	$.each(datasets, function(i, dataset) {
		groupNo++;
		var tr = $('<tr>');
		tr.addClass('group');
		tbody.append(tr);
		var td = $('<td>');
		td.html(''+groupNo);
		tr.append(td);
		for (var k=0; k < columns.length; k++) {
			var td = $('<td>');
			td.html('');
			tr.append(td);
		}
		$.each(dataset, function(j, rows) {
			var tr = $('<tr>');
			tbody.append(tr);
			var td = $('<td>');
			tr.append(td);
			td.html(''+groupNo);
			$.each(rows, function(k, col) {
				var td = $('<td>');
				td.html(col);
				tr.append(td);
			});
		});
	});
	var oTable = $('#' + tableId).dataTable({
		'aLengthMenu': [
		                [-1],
		                ['All']
		                ],
		'iDisplayLength': -1,
		"fnDrawCallback": function ( oSettings ) {
		    if ( oSettings.aiDisplay.length == 0 )
		    {
		        return;
		    }
		     
		    var nTrs = $('#' + tableId + ' tbody tr');
		    var iColspan = nTrs[0].getElementsByTagName('td').length;
		    var sLastGroup = "";
		    var siteNo = 0;
		    for ( var i=0 ; i<nTrs.length ; i++ )
		    {
		        var iDisplayIndex = oSettings._iDisplayStart + i;
		        var sGroup = oSettings.aoData[ oSettings.aiDisplay[iDisplayIndex] ]._aData[0];
		        if ($(nTrs[i]).hasClass('group')) {
				var tr = $(nTrs[i]);
				tr.removeClass('group');
				tr.html('');
				var td = $('<td>');
				td.attr({'colSpan': columns.length+1});
				td.addClass('group');
				var siteInfo = sites[siteNo++];
				if (siteInfo != null) {
					var value = '';
					var siteName = siteInfo['RequestSiteInfo']['SiteName'];
					var url = siteInfo['RequestURL'];
					siteName = getSiteName(siteName, url);
					if (siteName != null) {
						value = 'Site Name: ' + siteName;
					}
					if (siteInfo['RequestSiteInfo']['SiteDescription'] != null) {
						if (value.length > 0) {
							value += '<br/>';
						}
						value += 'Site Description: ' + siteInfo['RequestSiteInfo']['SiteDescription'];
					}
					td.html(value);
				} else {
					td.html('&nbsp;');
				}
				tr.append(td);
		        }
		    }
		},
		"aoColumnDefs": [
		    { "bVisible": false, "aTargets": [ 0 ] }
		],
		"aaSortingFixed": [[ 0, 'asc' ]],
		"aaSorting": [[ 1, 'asc' ]],
		"bInfo": false,
		"bPaginate": false,
        "sDom": 'lfr<"giveHeight"t>ip'
	});
	if (tableId == 'queryExample') {
		oQueryTable = oTable;
	} else {
		oAnalyzeTable = oTable;
	}
}

function buildStatusDataTable(tab) {
	var res, resultDiv, tableId, dict;
	if (tab == 'Query') {
		dict = queryResult;
	} else {
		dict = analyzeResult;
	}
	res = dict['data'];
	resultDiv = dict['statusDiv'];
	tableId = dict['statusTableId'];
	var columns = ['Site', 'Site Description', 'State', 'State Detail'];
	var rows = [];
	var complete = true;
	var serviceResponse = res['ServiceResponses']['ServiceResponse'];
	if (!$.isArray(serviceResponse)) {
		serviceResponse = [];
		serviceResponse.push(res['ServiceResponses']['ServiceResponse']);
	}
	$.each(serviceResponse, function(i, elem) {
		var serviceResponseMetadata = elem['ServiceResponseMetadata'];
		if (serviceResponseMetadata != null) {
			var siteName = serviceResponseMetadata['RequestSiteInfo']['SiteName'];
			var url = serviceResponseMetadata['RequestURL'];
			siteName = getSiteName(siteName, url);
			var row = [];
			row.push(siteName);
			row.push(serviceResponseMetadata['RequestSiteInfo']['SiteDescription']);
			row.push(serviceResponseMetadata['RequestState']);
			row.push(serviceResponseMetadata['RequestStateDetail']);
			rows.push(row);
			if (serviceResponseMetadata['RequestState'] != 'Complete') {
				complete = false;
			}
		}
	});
	if (tab == 'Query') {
		$('#statusQueryWrapperDiv').show();
	} else {
		$('#statusReplayWrapperDiv').show();
	}
	if (tableId == 'statusQueryExample') {
		if (oStatusQueryTable != null) {
			$('#' + tableId).remove();
			oStatusQueryTable = null;
		}
	} else {
		if (oStatusAnalyzeTable != null) {
			$('#' + tableId).remove();
			oStatusAnalyzeTable = null;
		}
	}
	resultDiv.parent().css('display', '');
	resultDiv.parent().parent().css('display', '');
	resultDiv.html('');
	var table = $('<table>');
	resultDiv.append(table);
	table.attr({	'cellpadding': '0',
			'cellspacing': '0',
			'border': '0',
			'id': tableId}); 
	table.addClass('display');
	var thead = $('<thead>');
	table.append(thead);
	var tr = $('<tr>');
	thead.append(tr);
	for (var i=0; i < columns.length; i++) {
		var th = $('<th>');
		tr.append(th);
		th.html(columns[i]);
	}
	var tbody = $('<tbody>');
	table.append(tbody);
	$.each(rows, function(i, row) {
		var tr = $('<tr>');
		tbody.append(tr);
		$.each(row, function(j, col) {
			var td = $('<td>');
			td.html(col);
			tr.append(td);
		});
	});
	var oTable = $('#' + tableId).dataTable({
		'aLengthMenu': [
		                [-1],
		                ['All']
		                ],
		'iDisplayLength': -1,
		"bInfo": false,
		"bPaginate": false,
		"bFilter": false,
        'sDom': 'lfr<"giveHeight"t>ip'
	});
	if (tableId == 'statusQueryExample') {
		oStatusQueryTable = oTable;
	} else {
		oStatusAnalyzeTable = oTable;
	}
}

/**
 * Handle an error from the AJAX request
 * retry the request in case of timeout
 * maximum retries: 10
 * each retry is performed after an exponential delay
 * 
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param textStatus
 * 	the string describing the type of error
 * @param errorThrown
 * 	the textual portion of the HTTP status
 * @param retryCallback
 * 	the AJAX request to be retried
 * @param url
 * 	the request url
 * @param obj
 * 	the parameters (in a dictionary form) for the POST request
 * @param async
 * 	the operation type (sync or async)
 * @param successCallback
 * 	the success callback function
 * @param param
 * 	the parameters for the success callback function
 * @param errorCallback
 * 	the error callback function
 * @param count
 * 	the number of retries already performed
 */
function handleError(jqXHR, textStatus, errorThrown, retryCallback, url, obj, async, successCallback, param, errorCallback, count) {
	var retry = false;
	
	switch(jqXHR.status) {
	case 0:		// client timeout
	case 408:	// server timeout
	case 503:	// Service Unavailable
	case 504:	// Gateway Timeout
		retry = (count <= MAX_RETRIES);
		break;
	case 401:		// Unauthorized
		var err = jqXHR.getResponseHeader('X-Error-Description');
		if (err != null) {
			err = decodeURIComponent(err);
			if (err == 'The requested scanner API usage by unauthorized client requires authorization.') {
				window.location = '/scanner';
				return;
			}
		}
		break;
	case 403:	// Forbidden
		var err = jqXHR.responseText;
		if (err == 'unauthenticated session access forbidden') {
			window.location = '/scanner';
			return;
		}
		break;
	}
	
	if (!retry) {
		var msg = '';
		var err = jqXHR.status;
		if (err != null) {
			msg += 'Status: ' + err + '\n';
		}
		err = jqXHR.responseText;
		if (err != null) {
			msg += 'ResponseText: ' + err + '\n';
		}
		err = jqXHR.getResponseHeader('X-Error-Description');
		if (err != null) {
			msg += 'X-Error-Description: ' + decodeURIComponent(err) + '\n';
		}
		if (textStatus != null) {
			msg += 'TextStatus: ' + textStatus + '\n';
		}
		if (errorThrown != null) {
			msg += 'ErrorThrown: ' + errorThrown + '\n';
		}
		msg += 'URL: ' + url + '\n';
		document.body.style.cursor = "default";
		$('#ajaxSpinnerImage').hide();
		$('#ajaxSpinnerImage').parent().hide();
		$('#ajaxAnalyzeSpinnerImage').hide();
		$('#ajaxAnalyzeSpinnerImage').parent().hide();
		var alertMessage = getHTMLErrorMessage(jqXHR.responseText);
		$('#errorMessage').html(alertMessage);
		alertErrorDialog.dialog('open');
		$('.ui-widget-overlay').css('opacity', 1.0);
		//alert(msg);
	} else {
		var delay = Math.round(Math.ceil((0.75 + Math.random() * 0.5) * Math.pow(10, count) * 0.00001));
		setTimeout(function(){retryCallback(url, obj, async, successCallback, param, errorCallback, count+1);}, delay);
	}
}

function getHTMLErrorMessage(msg) {
	msg = msg.replace(/&lt;/g, '<');
	msg = msg.replace(/&gt;/g, '>');
	var index1 = msg.indexOf('<body>');
	if (index1 >= 0) {
		var index2 = msg.lastIndexOf('</body>');
		msg = msg.substring(index1+'<body>'.length, index2);
		// remove any head
		index1 = msg.indexOf('<head>');
		while (index1 >= 0) {
			index2 = msg.indexOf('</head>');
			msg = msg.substring(0, index1) + msg.substring(index2+'</head>'.length);
			index1 = msg.indexOf('<head>');
		}
		// remove any body
		msg = msg.replace(/<body>/g, '');
		msg = msg.replace(/<\/body>/g, '');
		msg = msg.replace(/<html>/g, '');
		msg = msg.replace(/<\/html>/g, '');
	}
	return msg;
}

/**
 * Functions to send AJAX requests
 * 
 * @param url
 * 	the request url
 * @param obj
 * 	the parameters (in a dictionary form) for the POST request
 * @param successCallback
 * 	the success callback function
 * @param param
 * 	the parameters for the success callback function
 * @param errorCallback
 * 	the error callback function
 * @param count
 * 	the number of retries already performed
 */
var scanner = {
		POST: function(url, obj, async, successCallback, param, errorCallback, count) {
			$.ajax({
				url: url,
				headers: {'User-agent': 'Scanner/1.0'},
				type: 'POST',
				data: obj,
				dataType: 'text',
				timeout: AJAX_TIMEOUT,
				async: async,
				success: function(data, textStatus, jqXHR) {
					successCallback(data, textStatus, jqXHR, param);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (errorCallback == null) {
						handleError(jqXHR, textStatus, errorThrown, scanner.POST, url, obj, async, successCallback, param, errorCallback, count);
					} else {
						errorCallback(jqXHR, textStatus, errorThrown, scanner.POST, url, obj, async, successCallback, param, errorCallback, count);
					}
				}
			});
		},
		GET: function(url, async, successCallback, param, errorCallback, count) {
			scanner.fetch(url, null, async, successCallback, param, errorCallback, count);
		},
		fetch: function(url, obj, async, successCallback, param, errorCallback, count) {
			$.ajax({
				url: url,
				headers: {'User-agent': 'Scanner/1.0'},
				timeout: AJAX_TIMEOUT,
				async: async,
				accepts: {text: 'application/json'},
				dataType: 'json',
				success: function(data, textStatus, jqXHR) {
					successCallback(data, textStatus, jqXHR, param);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (errorCallback == null) {
						handleError(jqXHR, textStatus, errorThrown, scanner.fetch, url, obj, async, successCallback, param, errorCallback, count);
					} else {
						errorCallback(jqXHR, textStatus, errorThrown, scanner.fetch, url, obj, async, successCallback, param, errorCallback, count);
					}
				}
			});
		},
		RETRIEVE: function(url, obj, async, successCallback, param, errorCallback, count) {
			$.ajax({
				url: url,
				headers: {'User-agent': 'Scanner/1.0'},
				timeout: AJAX_TIMEOUT,
				data: obj,
				async: async,
				accepts: {text: 'application/json'},
				dataType: 'json',
				success: function(data, textStatus, jqXHR) {
					successCallback(data, textStatus, jqXHR, param);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (errorCallback == null) {
						handleError(jqXHR, textStatus, errorThrown, scanner.fetch, url, obj, async, successCallback, param, errorCallback, count);
					} else {
						errorCallback(jqXHR, textStatus, errorThrown, scanner.fetch, url, obj, async, successCallback, param, errorCallback, count);
					}
				}
			});
		},
		DELETE: function(url, async, successCallback, param, errorCallback, count) {
			scanner.remove(url, null, async, successCallback, param, errorCallback, count);
		},
		remove: function(url, obj, async, successCallback, param, errorCallback, count) {
			$.ajax({
				url: url,
				headers: {'User-agent': 'Scanner/1.0'},
				type: 'DELETE',
				timeout: AJAX_TIMEOUT,
				async: async,
				accepts: {text: 'application/json'},
				dataType: 'json',
				success: function(data, textStatus, jqXHR) {
					successCallback(data, textStatus, jqXHR, param);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (errorCallback == null) {
						handleError(jqXHR, textStatus, errorThrown, scanner.remove, url, obj, async, successCallback, param, errorCallback, count);
					} else {
						errorCallback(jqXHR, textStatus, errorThrown, scanner.remove, url, obj, async, successCallback, param, errorCallback, count);
					}
				}
			});
		},
		PUT: function(url, obj, async, successCallback, param, errorCallback, count) {
			$.ajax({
				url: url,
				headers: {'User-agent': 'Scanner/1.0'},
				type: 'PUT',
				data: obj,
				dataType: 'json',
				timeout: AJAX_TIMEOUT,
				async: async,
				success: function(data, textStatus, jqXHR) {
					successCallback(data, textStatus, jqXHR, param);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (errorCallback == null) {
						handleError(jqXHR, textStatus, errorThrown, scanner.PUT, url, obj, async, successCallback, param, errorCallback, count);
					} else {
						errorCallback(jqXHR, textStatus, errorThrown, scanner.PUT, url, obj, async, successCallback, param, errorCallback, count);
					}
				}
			});
		}
};

function submitLogin() {
	var url = HOME + '/login';
	var obj = new Object();
	document.body.style.cursor = "wait";
	scanner.POST(url, obj, true, postSubmitLogin, null, null, 0);
}

function postSubmitLogin(data, textStatus, jqXHR, param) {
	document.body.style.cursor = "default";
	var res = $.parseJSON(data);
	var loginDiv = $('#loginForm');
	$('#errorDiv').remove();
	if (res['status'] == 'success') {
		$('#profileIdentity').html(res['mail']);
		$('#profileIdentityDescription').html(res['description'][0]);
		$('#profileRole').html(res['role']);
		$('#profileStudyCount').html('0');
		$('#profileQueryCount').html('0');
		$('#profileRoleDescription').html(res['description'][1]);
		$('#profilePrivacyBudget').html('xxxxxx');
		$('#profilePrivacyBudgetDescription').html(res['description'][2]);
		loginRegistry();
	} else {
		var errorDiv = $('<div>');
		loginDiv.append(errorDiv);
		errorDiv.attr({
			'id': 'errorDiv'
		});
		var b = $('<b>');
		errorDiv.append($('<br>'));
		errorDiv.append($('<br>'));
		errorDiv.append(b);
		b.css({
			'color': 'red'
		});
		b.html(res['status']);
	}
}

function setContacts() {
	var div = $('#contactsDiv');
	var h4 = $('<h4>');
	div.append(h4);
	h4.html('Studies related information');
	var studyId = 0;
	$.each(userRolesList, function(i, userRole) {
		var studyRole = userRole['studyRole'];
		if (studyId != studyRole['study']) {
			studyId = studyRole['study'];
			var h6 = $('<h6>');
			div.append(h6);
			var name = allStudiesDict[studyId]['studyName'];
			h6.html('Study ' + name);
		}
		addContact(div, userRole);
	});
}

function addContact(div, userRole) {
	var studyRole = userRole['studyRole'];
	var span = $('<span>');
	div.append(span);
	var user = scannerUsersDict[userRole['user']];
	span.html(studyRole['roleWithinStudy'] + ': ' + user['firstName'] + ' ' + user['lastName']);
	span.html(span.html() + ', Email: ' + user['email']);
	if (user['phone'] != null) {
		span.html(span.html() + ', Phone: ' + user['phone']);
	}
	span.append('<br>');
}

function loginRegistry() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'loginRegistry';
	scanner.POST(url, obj, true, postLoginRegistry, null, null, 0);
}

function postLoginRegistry(data, textStatus, jqXHR, param) {
	var res = $.parseJSON(data);
	if (res['status'] == 'success') {
		renderAvailableStudies();
		renderSitesStatus();
		initUsers();
	} else {
		alert(res['status']);
	}
}

function analyzeStudy() {
	$('#loginForm').css('display', 'none');
	$('#ui').css('visibility', 'visible');
	$('#ui').css('display', '');
	$('#logoutButton').show();
	$('#studyName').html('Study: ' + getSelectedStudyName());
	var description = availableStudies[getSelectedStudyName()];
	$('#studyName').hover(
			function(event) {DisplayTipBox(event, description);}, 
			function(){HideTipBox();});
	lastActiveStudy = getSelectedStudyName();
	displayMyStudies();
}

function registryUserLogout() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'logout';
	scanner.POST(url, obj, true, postRegistryUserLogout, null, null, 0);
}

function postRegistryUserLogout(data, textStatus, jqXHR, param) {
	var res = $.parseJSON(data);
	alert('logout: '+res['status']);
}


/**
 * Return a string representation of a JavaScript value 
 * 
 * @param val
 * 	the JavaScript value
  * 
 * @return string representation of a JavaScript value
*/
function valueToString(val) {
	if ($.isArray(val)) {
		return arrayToString(val);
	} else if ($.isPlainObject(val)) {
		return objectToString(val);
	} else if ($.isNumeric(val)) {
		return val;
	} else if ($.isFunction(val)) {
		return '"Function"';
	} else if($.isWindow(val)) {
		return '"Window"';
	} else if ($.isXMLDoc(val)) {
		return '"XMLDoc"';
	} else {
		var valType = $.type(val);
		if (valType == 'string') {
			return '"' + escapeDoubleQuotes(val) + '"';
		} else if (valType == 'boolean') {
			return val;
		} else if (valType == 'object') {
			return '"Object"';
		} else {
			return '"' + valType + '"';
		}
	}
}

/**
 * Return a string representation of a JavaScript array 
 * 
 * @param obj
 * 	the JavaScript array
  * 
 * @return string representation of a JavaScript array
*/
function arrayToString(obj) {
	var s = '[';
	var first = true;
	$.each(obj, function(i, val) {
		if (!first) {
			s += ',';
		}
		first = false;
		s += valueToString(val);
	});
	s += ']';
	return s;
}

/**
 * Return a string representation of a JavaScript object 
 * 
 * @param obj
 * 	the JavaScript object
  * 
 * @return string representation of a JavaScript object
*/
function objectToString(obj) {
	var s = '{';
	var first = true;
	$.each(obj, function(key, val) {
		if (!first) {
			s += ',';
		}
		first = false;
		s += '"' + key + '":' + valueToString(val);
	});
	s += '}';
	return s;
}

/**
 * Return an escaped string   
 * 
 * @param text
 * 	the string to be escaped
  * 
 * @return the escaped string
*/
function escapeDoubleQuotes(text) {
	return text.replace(/"/g, '\\"');
}

/**
 * Return an URL encoded string 
 * 
 * @param value
 * 	the JavaScript string
 * 
 * @return encoded URL string
*/
function encodeSafeURIComponent(value) {
	var ret = encodeURIComponent(value);
	$.each("~!()'", function(i, c) {
		ret = ret.replace(new RegExp('\\' + c, 'g'), escape(c));
	});
	return ret;
}

/**
 * Compares two strings lexicographically, ignoring case differences.
 * 
 * @param str1
 * 	the left operand string
 * @param str2
 * 	the right operand string
 * @return: 
 * 	0 if str1 == str2
 * 	-1 if the str1 < str2
 * 	1 if str1 > str2
 */
function compareIgnoreCase(str1, str2) {
	var val1 = str1.toLowerCase();
	var val2 = str2.toLowerCase();
	if (val1 == val2) {
		return 0;
	} else if (val1 < val2) {
		return -1;
	} else {
		return 1;
	}
}

/**
 * Compares two strings lexicographically, ignoring case differences.
 * 
 * @param str1
 * 	the left operand string
 * @param str2
 * 	the right operand string
 * @return: 
 * 	0 if str1 == str2
 * 	-1 if the str1 < str2
 * 	1 if str1 > str2
 */
function compareServiceResponseMetadata(obj1, obj2) {
	var serviceResponseMetadata = obj1['ServiceResponseMetadata'];
	var val1 = serviceResponseMetadata['RequestSiteInfo']['SiteName'];
	var url = serviceResponseMetadata['RequestURL'];
	val1 = getSiteName(val1, url);
	serviceResponseMetadata = obj2['ServiceResponseMetadata'];
	var val2 = serviceResponseMetadata['RequestSiteInfo']['SiteName'];
	url = serviceResponseMetadata['RequestURL'];
	val2 = getSiteName(val2, url);
	var val1 = val1.toLowerCase();
	var val2 = val2.toLowerCase();
	if (val1 == val2) {
		return 0;
	} else if (val1 < val2) {
		return -1;
	} else {
		return 1;
	}
}

/**
 * Expand a tree
 * 
 * @param id
 * 	the id of the root
 */
function expandAll(id) {
	$('#' + id).find('div.hitarea.expandable-hitarea').click();
	//$('#navigation').find('div.hitarea.expandable-hitarea').click();
	//$('#navigation').find('div.hitarea.tree-hitarea.expandable-hitarea').click();
}

/**
 * Build a tree for a JSONObject
 * 
 * @param res
 * 	the JSONObject for the tree
 * @param resultDiv
 * 	the div to place the tree
 * @param id
 * 	the id of the tree
 */
function buildTreeResult(res, resultDiv, id) {
	resultDiv.parent().css('display', '');
	resultDiv.html('');
	var h1 = $('<h1>');
	resultDiv.append(h1);
	h1.html('Response Results');
	var input = $('<input>');
	input.attr({'type': 'button',
		'value': 'Expand All'});
	input.val('Expand All');
	input.click(function(event) {expandAll(id);});
	resultDiv.append(input);
	resultDiv.append($('<br>'));
	resultDiv.append($('<br>'));
	var ul = $('<ul>');
	ul.attr({'id': id});
	resultDiv.append(ul);
	appendTreeItem(ul, res);
	$('#' + id).treeview({
		persist: 'location',
		collapsed: true,
		unique: false
	});
}

/**
 * Append an item to a tree
 * 
 * @param res
 * 	the JSONObject for the item
 * @param resultDiv
 * 	the div to place the item
 * 	the id of the tree
 */
function appendTreeItem(div, res) {
	if ($.isArray(res)) {
		var ul = $('<ul>');
		div.append(ul);
		$.each(res, function(i, item) {
			var root = ul;
			appendTreeItem(ul, item);
		});
	} else if ($.isPlainObject(res)) {
		$.each(res, function(key, value) {
			var li = $('<li>');
			div.append(li);
			var label = $('<label>');
			li.append(label);
			label.html(key);
			appendTreeItem(li, value);
		});
	} else {
		var ul = $('<ul>');
		div.append(ul);
		var li = $('<li>');
		ul.append(li);
		var label = $('<label>');
		li.append(label);
		label.html(res);
	}
}

/**
 * Load the studies dropdown box with the specified values
 * 
 * @param values
 * 	the array with the studies names
 * 	the id of the tree
 */
function loadStudies(values) {
	require(["dojo/ready", "dijit/form/MultiSelect", "dijit/form/Button", "dojo/dom", "dojo/_base/window"], function(ready, MultiSelect, Button, dom, win){
		ready(function(){
			if (studiesMultiSelect != null) studiesMultiSelect.destroyRecursive(true);
			selStudies = dom.byId('selectStudies');
			selStudies.innerHTML = '';
			$.each(values, function(i, val) {
				var c = win.doc.createElement('option');
				c.innerHTML = val;
				c.value = val;
				selStudies.appendChild(c);
			});
			studiesMultiSelect = new MultiSelect({ name: 'selectStudies', value: '' }, selStudies);
			studiesMultiSelect.watch('value', function () {
				loadDatasets(emptyValue);
				loadLibraries(emptyValue);
				loadMethods(emptyValue);
				loadSites(emptyValue, false);
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				var selValues = studiesMultiSelect.get('value');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableDatasets();
						$('#continueButton').removeAttr('disabled');
					} else if (selValues.length > 1) {
						studiesMultiSelect.set('value', '');
						$('#continueButton').attr('disabled', 'disabled');
					} else if (selValues.length == 0) {
						$('#continueButton').attr('disabled', 'disabled');
					}
				} 
			});
			studiesMultiSelect.startup();
		});
	});
}

/**
 * Load the datasets dropdown box with the specified values
 * 
 * @param values
 * 	the array with the datasets names
 * 	the id of the tree
 */
function loadDatasets(values) {
	require(["dojo/ready", "dijit/form/MultiSelect", "dijit/form/Button", "dojo/dom", "dojo/_base/window"], function(ready, MultiSelect, Button, dom, win){
		ready(function(){
			if (datasetsMultiSelect != null) datasetsMultiSelect.destroyRecursive(true);
			selDatasets = dom.byId('selectDatasets');
			selDatasets.innerHTML = '';
			$.each(values, function(i, val) {
				var c = win.doc.createElement('option');
				c.innerHTML = val;
				c.value = val;
				selDatasets.appendChild(c);
			});
			datasetsMultiSelect = new MultiSelect({ name: 'selectDatasets', value: '' }, selDatasets);
			datasetsMultiSelect.watch('value', function () { 
				loadLibraries(emptyValue);
				loadMethods(emptyValue);
				loadSites(emptyValue, false);
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				var selValues = datasetsMultiSelect.get('value');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableSites();
					} else if (selValues.length > 1) {
						datasetsMultiSelect.set('value', '');
					}
				} 
			});
			datasetsMultiSelect.startup();
		});
	});
}

/**
 * Load the libraries dropdown box with the specified values
 * 
 * @param values
 * 	the array with the libraries names
 * 	the id of the tree
 */
function loadLibraries(values) {
	require(["dojo/ready", "dijit/form/MultiSelect", "dijit/form/Button", "dojo/dom", "dojo/_base/window"], function(ready, MultiSelect, Button, dom, win){
		ready(function(){
			if (librariesMultiSelect != null) librariesMultiSelect.destroyRecursive(true);
			selLibraries = dom.byId('selectLibraries');
			selLibraries.innerHTML = '';
			$.each(values, function(i, val) {
				var c = win.doc.createElement('option');
				c.innerHTML = val;
				c.value = val;
				selLibraries.appendChild(c);
			});
			librariesMultiSelect = new MultiSelect({ name: 'selectLibraries', value: '' }, selLibraries);
			librariesMultiSelect.watch('value', function () {
				loadMethods(emptyValue);
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				var selValues = librariesMultiSelect.get('value');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableMethods();
					} else if (selValues.length > 1) {
						librariesMultiSelect.set('value', '');
					}
				} 
			});
			librariesMultiSelect.startup();
		});
	});
}

/**
 * Load the methods dropdown box with the specified values
 * 
 * @param values
 * 	the array with the methods names
 * 	the id of the tree
 */
function loadMethods(values) {
	require(["dojo/ready", "dijit/form/MultiSelect", "dijit/form/Button", "dojo/dom", "dojo/_base/window"], function(ready, MultiSelect, Button, dom, win){
		ready(function(){
			if (methodsMultiSelect != null) methodsMultiSelect.destroyRecursive(true);
			selMethods = dom.byId('selectMethods');
			selMethods.innerHTML = '';
			$.each(values, function(i, val) {
				var c = win.doc.createElement('option');
				c.innerHTML = val;
				c.value = val;
				selMethods.appendChild(c);
			});
			methodsMultiSelect = new MultiSelect({ name: 'selectMethods', value: '' }, selMethods);
			methodsMultiSelect.watch('value', function () {
				var selValues = methodsMultiSelect.get('value');
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableParameters();
					} else if (selValues.length > 1) {
						methodsMultiSelect.set('value', '');
					}
				} 
			});
			methodsMultiSelect.startup();
		});
	});
}

/**
 * Load the sites dropdown box with the specified values
 * 
 * @param values
 * 	the array with the sites names
 * 	the id of the tree
 */
function loadSites(values, selectAll) {
	require(["dojo/ready", "dijit/form/MultiSelect", "dijit/form/Button", "dojo/dom", "dojo/_base/window"], function(ready, MultiSelect, Button, dom, win){
		ready(function(){
			if (sitesMultiSelect != null) sitesMultiSelect.destroyRecursive(true);
			selSites = dom.byId('selectSites');
			selSites.innerHTML = '';
			$.each(values, function(i, val) {
				var c = win.doc.createElement('option');
				c.innerHTML = val;
				c.value = val;
				selSites.appendChild(c);
			});
			sitesMultiSelect = new MultiSelect({ name: 'selectSites'}, selSites);
			sitesMultiSelect.watch('value', function () {
				loadMethods(emptyValue);
				loadLibraries(emptyValue);
				var selValues = sitesMultiSelect.get('value');
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				if (selValues != null) { 
					if (selValues.length >= 1) {
						renderAvailableLibraries();
					}
				} 
			});
			if (!selectAll) {
				sitesMultiSelect.set('value', '');
			} else {
				sitesMultiSelect.set('value', values);
			}
			sitesMultiSelect.startup();
		});
	});
}

/**
 * Actions to be performed when the "Query" tab is clicked
 */
function showQuery() {
	$('#tabContainerWrapperDiv').height(200);
	scannerTabContainer.resize();
	$('#paramsWrapperDiv').show();
	$('#replayDivContent').html('');
	$('#replayDivWrapper').hide();
	$('#statusReplayWrapperDiv').hide();
	if (activeStudy != null) {
		if (lastActiveStudy != activeStudy['studyName']) {
			lastActiveStudy = activeStudy['studyName'];
			showAvailableStudies();
		}
		$('#studyName').unbind();
		var description = activeStudy['description'];
		if (description != null) {
			$('#studyName').hover(
					function(event) {DisplayTipBox(event, description);}, 
					function(){HideTipBox();});
		}
	}
}

/**
 * Actions to be performed when the "Query" tab is deselected
 */
function hideQuery() {
	$('#tabContainerWrapperDiv').height(300);
	scannerTabContainer.resize();
	$('#paramsWrapperDiv').hide();
	$('#replayDivWrapper').show();
	$('#replayTitle').hide();
	$('#expandResults').hide();
	$('#collapseResults').hide();
	$('#replayDivContent').hide();
}

/**
 * Get the string representation of a date
 * 
 * @param date
 * 	the date object
 * @return the string representation of the date 
 */
function getDateString(date) {
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	if (month < 10) {
		month = '0' + month;
	}
	var day = date.getDate();
	if (day < 10) {
		day = '0' + day;
	}
	var hours = date.getHours();
	var am_pm = ' AM';
	if (hours > 12) {
		hours -= 12;
		am_pm = ' PM';
	}
	var minutes = date.getMinutes();
	if (minutes < 10) {
		minutes = '0' + minutes;
	}
	return (year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + am_pm);
}

/**
 * Push an entry in the analyze table
 * 
 * @param obj
 * 	the object with the columns values
 */
function pushAnalyze(obj) {
	require(['dojox/grid/DataGrid', 'dojo/data/ItemFileWriteStore'], function(DataGrid, ItemFileWriteStore) {
		var index = analyzeDataStore.items.length;
		if (analyzeGrid != null) {
			analyzeGrid.destroyRecursive();
		}
		var item = {};
		item.trxId = obj.trxId;
		item.parameters = obj.parameters;
		item.action = obj.action;
		item.study = obj.study;
		item.dataset = obj.dataset;
		item.library = obj.library;
		item.method = obj.method;
		item.sites = obj.sites;
		item.date = getDateString(new Date());
		if (obj['complete']) {
			item.status = 'Complete';
			item.options = '<a href="javascript:analyzeQuery(' + index + ')" >See results</a>';
		} else if (obj['hasError']) {
			item.status = 'Error';
			item.options = '<a href="javascript:analyzeQuery(' + index + ')" >See results</a>';
		} else {
			item.status = 'In Progress';
			item.options = '<a href="javascript:refreshQueryStatus(' + index + ')" >Refresh</a>';
		}
		item.id = index;
		analyzeDataStore.items.unshift(item);
		var store = new ItemFileWriteStore({data: analyzeDataStore});
		analyzeGrid = new DataGrid({
			id: 'analyzeGrid',
			store: store,
			structure: analyzeLayout,
			escapeHTMLInData: false,
			rowSelector: '20px'});

		/*append the new grid to the div*/
		analyzeGrid.placeAt("analyzeGridDiv");

		/*Call startup() to render the grid*/
		analyzeGrid.startup();
	});
}

function refreshQueryStatus(index) {
	var tableIndex = analyzeDataStore.items.length - index - 1;
	var item = analyzeDataStore.items[tableIndex];
	$('#replayTitle').show();
	$('#expandResults').hide();
	$('#collapseResults').show();
	$('#replayDivContent').show();
	submitQuery('replayDivContent', item, index, null);
}

function displayQueryStatus(index) {
	var tableIndex = analyzeDataStore.items.length - index - 1;
	var item = analyzeDataStore.items[tableIndex];
	$('#replayTitle').show();
	$('#expandResults').hide();
	$('#collapseResults').show();
	submitQuery('replayDivContent', item, index, postResult);
}

function postRefreshQueryStatus(index, status) {
	var tableIndex = analyzeDataStore.items.length - index - 1;
	var item = analyzeDataStore.items[tableIndex];
	var val = [];
	if (status != 'Held') {
		val.push(status);
		item.status = val;
		val = [];
		val.push('<a href="javascript:analyzeQuery(' + index + ')" >See results</a>');
		item.options = val;
	}
	val = [];
	val.push(getDateString(new Date()));
	item.date = val;
	analyzeGrid.update();
}

/**
 * Function to be called when a query is replayed
 * 
 * @param index
 * 	the row index in the analyze table
 */
function analyzeQuery(index) {
	var tableIndex = analyzeDataStore.items.length - index - 1;
	var item = analyzeDataStore.items[tableIndex];
	$('#replayTitle').show();
	$('#expandResults').hide();
	$('#collapseResults').show();
	submitQuery('replayDivContent', item, -1, null);
}

function copyValue(val) {
	if ($.isArray(val)) {
		return copyArray(val);
	} else if ($.isPlainObject(val)) {
		return copyObject(val);
	} else if ($.isNumeric(val)) {
		return val;
	} else {
		var valType = $.type(val);
		if (valType == 'string') {
			return valType;
		} else {
			return '"' + valType + '"';
		}
	}
}


function copyArray(arr) {
	var ret = [];
	$.each(arr, function(i,elem) {
		ret.push(copyValue(elem));
	});
	return ret;
}

function copyObject(obj) {
	var ret = {};
	$.each(obj, function(key, val) {
		ret[key] = copyValue(val);
	});
	return ret;
}

function enableScanner() {
	if ($('#acceptConditions').attr('checked') == 'checked') {
		$('#selectStudies').removeAttr('disabled');
	} else {
		studiesMultiSelect.set('value', '');
		$('#continueButton').attr('disabled', 'disabled');
		$('#selectStudies').attr('disabled', 'disabled');
	}

}

function makeId(id) {
	var parts = id.split(' ');
	return parts.join('_');
}

/**
 * Display a flyover message
 * 
 * @param e
 * 	the event that triggered the flyover
 * @param content
 * 	the content to be displayed
 */
function DisplayTipBox(e, content) {
	tipBox.html(content);
	var dx = tipBox.width() + 30;
	var delta = dx + 10;
	dx = (e.clientX + delta > $(window).width()) ? $(window).width() - e.clientX - delta : 0;
	tipBox.css('left', String(parseInt(e.pageX + dx) + 'px'));
	tipBox.css('top', String(parseInt(e.pageY - tipBox.height() - 50) + 'px'));
	tipBox.css('display', 'block');
}

/**
 * Hide a flyover message
 */
function HideTipBox() {
	tipBox.css('display', 'none');
}

/**
 * Display a flyover message
 * 
 * @param e
 * 	the event that triggered the flyover
 * @param content
 * 	the content to be displayed
 */
function DisplaySelectTipBox(e, content) {
	tipBox.html(content);
	var left = ($(e.currentTarget).position().left + $(e.currentTarget).parent().width() + scrollbarWidth) + 'px';
	tipBox.css('left', left);
	tipBox.css('top', String(parseInt(e.pageY - tipBox.height() - 50) + 'px'));
	tipBox.css('display', 'block');
}

/**
 * Hide a flyover message
 */
function HideSelectTipBox() {
	tipBox.css('display', 'none');
}

function disableIndependentVariableName() {
	var val = $('#param_dependentVariableName').val();
	if (val != null) {
		$('input:checkbox', $('#param_independentVariableName')).removeAttr('disabled');
		$('#param_' + makeId(val)).removeAttr('checked');
		$('#param_' + makeId(val)).attr('disabled', 'disabled');
	}
}

function submitLogout() {
	var url = HOME + '/logout';
	document.body.style.cursor = "wait";
	scanner.POST(url, null, true, postSubmitLogout, null, null, 0);
}

function postSubmitLogout(data, textStatus, jqXHR, param) {
	document.body.style.cursor = "default";
	alert('Please close your browser to logoff.');
	$('#logoutButton').hide();
	window.location = HOME;
}

function drawBox(csv, column, boxDiv) {
	var boxChart = boxDiv.get(0);
	var margin = {top: 10, right: 50, bottom: 20, left: 50},
	    width = 120 - margin.left - margin.right,
	    height = 500 - margin.top - margin.bottom;

	var min = Infinity,
	    max = -Infinity;

	var chart = d3.box()
	    .whiskers(iqr(1.5))
	    .width(width)
	    .height(height);

		var data = [];
		min = Infinity;
		max = -Infinity;
		$.each(csv, function(i, site) {
			data[i] = [];
			$.each(site, function(k, row) {
				var s = +row[column];
				data[i].push(s);
				if (s > max) {
					max = s;
				}
				if (s < min) {
					min = s;
				}
			});
		});
	  chart.domain([min, max]);

	  var svg = d3.select(boxChart).selectAll("svg")
	      .data(data)
	    .enter().append("svg")
	      .attr("class", "box")
	      .attr("width", width + margin.left + margin.right)
	      .attr("height", height + margin.bottom + margin.top)
	    .append("g")
	      .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
	      .call(chart);
}

//Returns a function to compute the interquartile range.
function iqr(k) {
  return function(d, i) {
    var q1 = d.quartiles[0],
        q3 = d.quartiles[2],
        iqr = (q3 - q1) * k,
        i = -1,
        j = d.length;
    while (d[++i] < q1 - iqr);
    while (d[--j] > q3 + iqr);
    return [i, j];
  };
}

function getOutput(data, ret) {
	if ($.isPlainObject(data)) {
		$.each(data, function(key, value) {
			if (key == 'Output') {
				ret.push(value);
			} else {
				getOutput(value, ret);
			}
		});
	} else if ($.isArray(data)) {
		$.each(data, function(i, value) {
			if ($.isPlainObject(value)) {
				getOutput(value, ret);
			}
		});
	}
}

function hideProjects() {
	$('#tabContainerWrapperDiv').height(300);
	scannerTabContainer.resize();
}

function showProjects() {
	$('#tabContainerWrapperDiv').height(600);
	scannerTabContainer.resize();
	$('#paramsWrapperDiv').hide();
	$('#replayDivWrapper').hide();
	$('#statusReplayWrapperDiv').hide();

}

function manageStudy(hideMode) {
	if (activeStudy == null) {
		$('#manageStudyH2').html('There are no studies to manage.');
		$('#updateStudyDiv').hide();
		return;
	}
	$('#manageStudyH2').html('Manage Research Study ' + activeStudy['studyId'] + ': ' + activeStudy['studyName']);
	if (activeStudy['description'] != null) {
		$('#manageStudyH2').html($('#manageStudyH2').html() + ' - ' + activeStudy['description']);
	}
	$('#updateStudyDiv').show();
	$('.wizard_heading', $('#manageStudiesDiv')).unbind('click');
	if (hideMode) {
		$('.wizard_content', $('#manageStudiesDiv')).hide();
	}
	$('.wizard_heading', $('#manageStudiesDiv')).click(function() {
		$(this).next(".wizard_content").toggle();
	});
	$('.datepicker').datetimepicker({	dateFormat: 'yy-mm-dd',
		timeFormat: '',
		separator: '',
		changeYear: true,
		showTime: false,
		showHour: false,
		showMinute: false
	});
	
	$('#projectTitle').unbind('keyup');
	$('#projectTitle').keyup(function(event) {checkUpdateStudyButton();});
	$('#updateStudyPrincipalInvestigator').unbind('change');
	var select = $('#updateStudyPrincipalInvestigator');
	select.html('');
	var option = $('<option>');
	option.text('Enter Letters of Name...');
	option.attr('value', '');
	select.append(option);
	$.each(scannerUsersList, function(i, user) {
		option = $('<option>');
		option.text(user['firstName'] + ' ' + user['lastName']);
		option.attr('value', user['userId']);
		select.append(option);
	});
	select.change(function(event) {checkUpdateStudyButton();});
	
	$('#projectTitle').val(activeStudy['studyName']);
	$('#updateStudyIRBInput').val(activeStudy['irbId']);
	$('#updateStudyPrincipalInvestigator').val(activeStudy['principalInvestigator']);
	$('#updateStudyStatusType').val(activeStudy['studyStatusType']);
	$('#projectDescription').val('');
	$('#projectProtocol').val('');
	$('#projectStartDate').val('');
	$('#projectEndDate').val('');
	$('#projectClinicalTrialsId').val('');
	$('#projectAnalysisPlan').val('');
	$('#roleNameInput').unbind('keyup');
	
	if (activeStudy['description'] != null) {
		$('#projectDescription').val(activeStudy['description']);
	}
	if (activeStudy['protocol'] != null) {
		$('#projectProtocol').val(activeStudy['protocol']);
	}
	if (activeStudy['startDate'] != null) {
		$('#projectStartDate').val(activeStudy['startDate']);
	}
	if (activeStudy['endDate'] != null) {
		$('#projectEndDate').val(activeStudy['endDate']);
	}
	if (activeStudy['clinicalTrialsId'] != null) {
		$('#projectClinicalTrialsId').val(activeStudy['clinicalTrialsId']);
	}
	if (activeStudy['analysisPlan'] != null) {
		$('#projectAnalysisPlan').val(activeStudy['analysisPlan']);
	}
	var select = $('#staffNames');
	select.html('');
	select.change(function(event) {checkAddStaffButton();});
	var option = $('<option>');
	option.text('Enter Letters of Name.');
	option.attr('value', '');
	select.append(option);
	$.each(scannerUsersList, function(i, user) {
		option = $('<option>');
		option.text(user['firstName'] + ' ' + user['lastName']);
		option.attr('value', user['userId']);
		select.append(option);
	});
	var select = $('#staffRoles');
	select.html('');
	select.change(function(event) {checkAddStaffButton();});
	var option = $('<option>');
	option.text('Select Role...');
	option.attr('value', '');
	select.append(option);
	$.each(studyRolesList, function(i, studyRole) {
		if (studyRole['study'] == 1) {
			option = $('<option>');
			option.text(studyRole['roleWithinStudy']);
			option.attr('value', studyRole['roleId']);
			select.append(option);
		}
	});
	var select = $('#siteSelect');
	select.html('');
	select.change(function(event) {checkAddSiteButton();});
	var option = $('<option>');
	option.text('Select Site...');
	option.attr('value', '');
	select.append(option);
	$.each(sitesList, function(i, site) {
		option = $('<option>');
		option.text(site['siteName']);
		option.attr('value', site['siteId']);
		select.append(option);
	});
	var select = $('#modelNames');
	select.html('');
	select.change(function(event) {checkAddStudyProtocolButton();});
	var option = $('<option>');
	option.text('Estimate model...');
	option.attr('value', '');
	select.append(option);
	$.each(toolList, function(i, tool) {
		option = $('<option>');
		var lib = librariesDict[tool['toolParentLibrary']];
		option.text(lib['libraryName'] + ' - ' + tool['toolName']);
		option.attr('value', tool['toolId']);
		select.append(option);
	});
	var select = $('#datasetNames');
	select.html('');
	select.change(function(event) {checkAddStudyProtocolButton();});
	var option = $('<option>');
	option.text('Estimate model...');
	var option = $('<option>');
	option.text('using data set...');
	option.attr('value', '');
	select.append(option);
	$.each(datasetDefinitionList, function(i, dataset) {
		option = $('<option>');
		option.text(dataset['dataSetName']);
		option.attr('value', dataset['dataSetDefinitionId']);
		select.append(option);
	});
	var select = $('#datasetInstances');
	select.html('');
	select.change(function(event) {checkAddSiteProtocolButton();});
	var option = $('<option>');
	option.text('using data set instance...');
	option.attr('value', '');
	select.append(option);
	$.each(datasetInstancesList, function(i, datasetInstance) {
		option = $('<option>');
		option.text(datasetInstance['dataSetInstanceName']);
		option.attr('value', datasetInstance['dataSetInstanceId']);
		select.append(option);
	});
	option = $('<option>');
	option.text('Add new...');
	option.attr('value', 'Add new...');
	option.attr('id', 'add_new_dataset_instance_option');
	select.append(option);

	var select = $('#studySendOptions');
	select.html('');
	select.change(function(event) {checkAddStudyProtocolButton();});
	var option = $('<option>');
	option.text('Estimate model...');
	var option = $('<option>');
	option.text('and release results...');
	option.attr('value', '');
	select.append(option);
	$.each(accessModeList, function(i, accessMode) {
		option = $('<option>');
		option.text(accessMode['description']);
		option.attr('value', accessMode['accessModeId']);
		select.append(option);
	});
	$('#addStaffButton').attr('disabled', 'disabled');
	$('#addSiteProtocolButton').attr('disabled', 'disabled');
	$('#manageStudyStaffTbody').html('');
	$.each(activeStudy['staff'], function(i, staff) {
		addStaffRow(staff);
	});
	$('#manageSitesProtocolsTbody').html('');
	$('#viewSitesProtocolsTbody').html('');
	$('#manageStudyProtocolsTbody').html('');
	$.each(activeStudy['protocols'], function(i, protocol) {
		addProtocolRow(protocol);
		addProtocolViewRow(protocol);
	});
	
	if (activeStudy['studyPolicies'].length == 0) {
		var activePolicies = [];
		$.each(activeStudy['protocols'], function(i, protocol) {
			var hasPolicy = false;
			$.each(activePolicies, function(j, elem) {
				if (elem['tool'] == protocol['tool'] && elem['dataset'] == protocol['dataset'] && elem['accessMode'] == protocol['accessMode']) {
					hasPolicy = true;
					return false;
				}
			});
			if (!hasPolicy) {
				activePolicies.push(protocol);
				var obj = {};
				obj['tool'] = protocol['tool'];
				obj['dataset'] = protocol['dataset'];
				obj['accessMode'] = protocol['accessMode'];
				addProtocolPoliciesRow(obj, true);
			}
		});
	} else {
		$.each(activeStudy['studyPolicies'], function(i, policy) {
			addProtocolPoliciesRow(policy, false);
		});
	}
	
	addProtocolSelect();
	$('#manageStudySitesTbody').html('');
	if (activeStudy['sites'] != null) {
		$.each(activeStudy['sites'], function(key, site) {
			addSiteRow(site);
		});
	} 

	var select = $('#nodeNames');
	select.html('');
	select.change(function(event) {checkAddInstanceButton();});
	var option = $('<option>');
	option.text('for node...');
	option.attr('value', '');
	select.append(option);
	var selectNodes = [];
	$.each(datasetInstancesList, function(i, datasetInstance) {
		var node = datasetInstance['node'];
		var site = node['site'];
		if (!selectNodes.contains(site['siteName'] + ':' + node['nodeId'])) {
			selectNodes.push(site['siteName'] + ':' + node['nodeId']);
			option = $('<option>');
			option.text(site['siteName'] + ':' + node['nodeId']);
			option.attr('value', datasetInstance['dataSetInstanceId']);
			select.append(option);
		}
	});
	$('#instanceName').keyup(function(event) {checkAddInstanceButton();});
	$('#instanceDataSource').keyup(function(event) {checkAddInstanceButton();});
	$('#addInstanceButton').attr('disabled', 'disabled');
	$('#add_site_protocol_div').show();
	$('#add_instance_div').hide();
	if ($('#viewSitesProtocolsTbody').children().length == 0) {
		$('#viewSitesProtocols').hide();
	}
	if ($('#manageStudyProtocolsTbody').children().length == 0) {
		$('#manageStudyProtocols').hide();
	}
	if ($('#manageSitesProtocolsTbody').children().length == 0) {
		$('#manageSitesProtocols').hide();
	}
	checkUpdateStudyButton();
	checkAddStudyProtocolButton();
	checkAddSiteButton();
}

function checkAddRoleButton() {
	if ($('#roleNameInput').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0) {
		$('#addRoleButton').removeAttr('disabled');
	} else {
		$('#addRoleButton').attr('disabled', 'disabled');
	}
}

function checkCreateStudyButton() {
	if ($('#studyTitleInput').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && $('#createStudyPrincipalInvestigator').val() != '') {
		$('#createStudyButton').removeAttr('disabled');
	} else {
		$('#createStudyButton').attr('disabled', 'disabled');
	}
}

function checkUpdateStudyButton() {
	if ($('#projectTitle').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && $('#updateStudyPrincipalInvestigator').val() != '') {
		$('#updateStudyButton').removeAttr('disabled');
	} else {
		$('#updateStudyButton').attr('disabled', 'disabled');
	}
}

function checkAddStaffButton() {
	if ($('#staffNames').val() != '' && $('#staffRoles').val() != '') {
		$('#addStaffButton').removeAttr('disabled');
	} else {
		$('#addStaffButton').attr('disabled', 'disabled');
	}
}

function checkAddSiteButton() {
	if ($('#siteSelect').val() != '') {
		$('#addSiteButton').removeAttr('disabled');
	} else {
		$('#addSiteButton').attr('disabled', 'disabled');
	}
}

function checkAddSiteProtocolButton() {
	if ($('#datasetInstances').val() == 'Add new...') {
		$('#instanceName').val('');
		$('#nodeNames').val('');
		$('#instanceDataSource').val('');
		$('#instanceDescription').val('');
		$('#add_site_protocol_div').hide();
		$('#add_instance_div').show();
	} else if ($('#estimateStudyProtocolSelect').val() != '' && 
			$('#datasetInstances').val() != '' && $('#studyInvestigatorSelect').val() != '') {
		$('#addSiteProtocolButton').removeAttr('disabled');
	} else {
		$('#addSiteProtocolButton').attr('disabled', 'disabled');
	}
}

function checkAddStudyProtocolButton() {
	if ($('#modelNames').val() != '' && $('#datasetNames').val() != '' && 
			$('#studySendOptions').val() != '') {
		$('#addStudyProtocolButton').removeAttr('disabled');
	} else {
		$('#addStudyProtocolButton').attr('disabled', 'disabled');
	}
}

function checkAddInstanceButton() {
	if ($('#instanceName').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && $('#nodeNames').val() != '' && 
			$('#instanceDataSource').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0) {
		$('#addInstanceButton').removeAttr('disabled');
	} else {
		$('#addInstanceButton').attr('disabled', 'disabled');
	}
}

function addSite() {
	var site = sitesDict[$('#siteSelect').val()];
	activeStudy['sites'][site['siteId']] = site;
	$('#siteSelect').val('');
	checkAddSiteButton();
	addSiteRow(site);
	appendStudyContent(activeStudy);
}

function addStaff() {
	var obj = {};
	obj['userId'] = $('#staffNames').val();
	obj['role'] = studyRolesDict[$('#staffRoles').val()]['roleWithinStudy'];
	activeStudy['staff'].push(obj);
	addStaffRow(obj);
	updateBasicInfo();
}

function addStaffRow(obj) {
	var tbody = $('#manageStudyStaffTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	td.addClass('role_name');
	td.html(obj['role']);
	td = $('<td>');
	tr.append(td);
	td.html(scannerUsersDict[obj['userId']]['firstName'] + ' ' + scannerUsersDict[obj['userId']]['lastName']);
	td.attr('userId', obj['userId']);
	td = $('<td>');
	tr.append(td);
	var a = $('<a>');
	a.attr('href', 'mailto:' + scannerUsersDict[obj['userId']]['email']);
	a.html(scannerUsersDict[obj['userId']]['email']);
	td.append(a);
	td = $('<td>');
	tr.append(td);
	var button = $('<button>');
	button.click(function(event) {removeStaff($(this), obj);});
	button.html('Remove Staff');
	td.append(button);
	$('#staffNames').val('');
	$('#staffRoles').val('');
	$('#studySites').val('');
	$('#addStaffButton').attr('disabled', 'disabled');
}

function removeStaff(button, obj) {
	var staffValues = activeStudy['staff'];
	$.each(staffValues, function(i, elem) {
		if (elem['userId'] == obj['userId'] && elem['role'] == obj['role']) {
			staffValues.splice(i, 1);
			return false;
		}
	});
	button.parent().parent().remove();
	updateBasicInfo();
}

function getSiteName(siteName, url) {
	url = url.split('//');
	var protocol = url[0] + '//';
	url = url[1].split('/');
	var value = sitesMap[protocol + url[0]];
	if (value != null) {
		siteName = value;
	}
	return siteName;
}

function expandQueryResults(divId, expandId, colappseId) {
	$('#' + divId).show();
	$('#' + expandId).hide();
	$('#' + colappseId).show();
}

function collapseQueryResults(divId, expandId, colappseId) {
	$('#' + divId).hide();
	$('#' + expandId).show();
	$('#' + colappseId).hide();
}

function checkComplete(data) {
	var complete = true;
	var serviceResponse = data['ServiceResponses']['ServiceResponse'];
	if (!$.isArray(serviceResponse)) {
		serviceResponse = [];
		serviceResponse.push(data['ServiceResponses']['ServiceResponse']);
	}
	$.each(serviceResponse, function(i, elem) {
		var serviceResponseMetadata = elem['ServiceResponseMetadata'];
		if (serviceResponseMetadata != null) {
			if (serviceResponseMetadata['RequestState'] != 'Complete') {
				complete = false;
			}
		}
	});
	return complete;
	
}

function checkError(data) {
	var error = false;
	var serviceResponse = data['ServiceResponses']['ServiceResponse'];
	if (!$.isArray(serviceResponse)) {
		serviceResponse = [];
		serviceResponse.push(data['ServiceResponses']['ServiceResponse']);
	}
	$.each(serviceResponse, function(i, elem) {
		var serviceResponseMetadata = elem['ServiceResponseMetadata'];
		if (serviceResponseMetadata != null) {
			if (serviceResponseMetadata['RequestState'] == 'Error') {
				error = true;
			}
		}
	});
	return error;
	
}

/**
 * Load the datasets dropdown box with the specified values
 * 
 * @param values
 * 	the array with the datasets names
 * 	the id of the tree
 */
var scannerUsersDict = null;
var scannerUsersList = null;
var analysisPolicies = null;

var datasetInstancesDict = null;
var datasetInstancesList = null;

var toolsDict = null;
var toolList = null;

var librariesDict = null;

var datasetDefinitionList = null;
var datasetDefinitionDict = null;

var userRolesDict = null;
var userRolesList = null;

var studyRolesDict = null;
var studyRolesList = null;

var studyPoliciesList = null;
var studyPoliciesDict = null;

var accessModeList = null;
var accessModeDict = null;

var studyCounter = 200;

var lastActiveStudy = null;

var myStudies = null;
var allStudies = null;
var allStudiesDict = null;

var sitesList = null;
var sitesDict = null;

var instanceIdCounter = 0;

var activeStudy = null;

function createStudy() {
	obj = {};
	obj['action'] = 'createStudy';
	obj['studyName'] = $('#studyTitleInput').val();
	var irb = $('#createStudyIRBInput').val().replace(/^\s*/, "").replace(/\s*$/, "");
	if (irb.length > 0) {
		if (!isNaN(parseInt(irb)) && irb.length == ("" + parseInt(irb)).length) {
			obj['irbId'] = parseInt(irb);
		} else {
			alert('Invalid integer value for IRB: "' + $('#createStudyIRBInput').val() + '"');
			return;
		}
	}
	obj['principalInvestigator'] = $('#createStudyPrincipalInvestigator').val();
	obj['studyStatusType'] = $('#createStudyStatusType').val();
	var url =  HOME + '/registry';
	scanner.POST(url, obj, true, postCreateStudy, null, null, 0);
}

function postCreateStudy(data, textStatus, jqXHR, param) {
	data = $.parseJSON(data);
	if (data['studyId'] == null) {
		alert('Error Status Code: ' + data['errorStatusCode'] + '\nError Message: ' + data['errorMessage'] + '\nError Detail: ' + data['errorDetail']);
		return;
	}
	activeStudy = {};
	myStudies.push(activeStudy);
	activeStudy['studyPolicies'] = [];
	activeStudy['sites'] = {};
	activeStudy['staff'] = [];
	activeStudy['protocols'] = [];
	activeStudy['studyId'] = data['studyId'];
	activeStudy['studyName'] = data['studyName'];
	activeStudy['irbId'] = data['irbId'];
	activeStudy['principalInvestigator'] = data['principalInvestigator'];
	$('#projectTitle').val(data['studyName']);
	appendStudy(activeStudy);
	setActive(activeStudy['studyId']);
	$('#manageStudiesDivLink').click();
}

function updateStudy() {
	obj = {};
	obj['action'] = 'updateStudy';
	obj['studyName'] = $('#projectTitle').val();
	obj['studyId'] = activeStudy['studyId'];

	var irb = $('#updateStudyIRBInput').val().replace(/^\s*/, "").replace(/\s*$/, "");
	if (irb.length > 0) {
		if (!isNaN(parseInt(irb)) && irb.length == ("" + parseInt(irb)).length) {
			obj['irbId'] = parseInt(irb);
		} else {
			alert('Invalid integer value for IRB: "' + $('#updateStudyIRBInput').val() + '"');
			return;
		}
	}

	var clinicalTrialsId = $('#projectClinicalTrialsId').val().replace(/^\s*/, "").replace(/\s*$/, "");
	if (clinicalTrialsId.length > 0) {
		if (isNaN(parseInt(clinicalTrialsId)) || clinicalTrialsId.length != ("" + parseInt(clinicalTrialsId)).length) {
			alert('Invalid integer value for the Clinical Trials Id: "' + $('#projectClinicalTrialsId').val() + '"');
			return;
		} else {
			obj['clinicalTrialsId'] = parseInt(clinicalTrialsId);
		}
	} else {
		obj['clinicalTrialsId'] = '';
	}
	obj['principalInvestigator'] = $('#updateStudyPrincipalInvestigator').val();
	obj['studyStatusType'] = $('#updateStudyStatusType').val();
	obj['description'] = $('#projectDescription').val().replace(/^\s*/, "").replace(/\s*$/, "");
	obj['protocol'] = $('#projectProtocol').val().replace(/^\s*/, "").replace(/\s*$/, "");
	obj['startDate'] = $('#projectStartDate').val().replace(/^\s*/, "").replace(/\s*$/, "");
	obj['endDate'] = $('#projectEndDate').val().replace(/^\s*/, "").replace(/\s*$/, "");
	obj['analysisPlan'] = $('#projectAnalysisPlan').val().replace(/^\s*/, "").replace(/\s*$/, "");
	
	var url =  HOME + '/registry';
	scanner.POST(url, obj, true, postUpdateStudy, null, null, 0);
}

function postUpdateStudy(data, textStatus, jqXHR, param) {
	data = $.parseJSON(data);
	if (data['studyId'] == null) {
		alert('Error Status Code: ' + data['errorStatusCode'] + '\nError Message: ' + data['errorMessage'] + '\nError Detail: ' + data['errorDetail']);
		return;
	} else {
		alert('The study was successfully updated.');
	}
	activeStudy['studyName'] = data['studyName'];
	activeStudy['irbId'] = data['irbId'];
	activeStudy['principalInvestigator'] = data['principalInvestigator'];
	activeStudy['studyStatusType'] = data['studyStatusType'];
	activeStudy['description'] = data['description'];
	activeStudy['protocol'] = data['protocol'];
	activeStudy['startDate'] = data['startDate'];
	activeStudy['endDate'] = data['endDate'];
	activeStudy['clinicalTrialsId'] = data['clinicalTrialsId'];
	activeStudy['analysisPlan'] = data['analysisPlan'];
	
	$('#manageStudyH2').html('Manage Research Study ' + data['studyId'] + ': ' + data['studyName']);
	if (data['description'] != null) {
		$('#manageStudyH2').html($('#manageStudyH2').html() + ' - ' + data['description']);
	}
	$('#updateStudyIRBInput').val(activeStudy['irbId']);
	updateBasicInfo();
}

function addStudyProtocol() {
	var obj = {};
	obj['tool'] = $('#modelNames').val();
	obj['dataset'] = $('#datasetNames').val();
	obj['accessMode'] = $('#studySendOptions').val();
	addProtocolPoliciesRow(obj, true);
	$('#modelNames').val('');
	$('#datasetNames').val('');
	$('#studySendOptions').val('');
	addProtocolSelect();
}

function addSiteProtocol() {
	var obj = {};
	var protocol = activeStudy['studyPolicies'][$('#estimateStudyProtocolSelect').val()];
	obj['tool'] = protocol['tool'];
	obj['dataset'] = protocol['dataset'];
	obj['datasetInstance'] = $('#datasetInstances').val();
	obj['accessMode'] = protocol['accessMode'];
	obj['role'] = studyRolesDict[$('#studyInvestigatorSelect').val()];
	activeStudy['protocols'].push(obj);
	addProtocolRow(obj);
	if (activeStudy['studyId'] != 1) {
		addProtocolViewRow(obj);
	}
	updateBasicInfo();
}

function addProtocolRow(obj) {
	var node = datasetInstancesDict[obj['datasetInstance']]['node'];
	var site = node['site'];
	var tbody = $('#manageSitesProtocolsTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var lib = librariesDict[toolsDict[obj['tool']]['toolParentLibrary']];
	var method = lib['libraryName'] + ' - ' + toolsDict[obj['tool']]['toolName'];
	td.html(method);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(datasetDefinitionDict[obj['dataset']]['dataSetName']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(datasetInstancesDict[obj['datasetInstance']]['dataSource']);
	td = $('<td>');
	td.addClass('protocol_border');
	tr.append(td);
	td.html(site['siteName']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(node['nodeId']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var accessMode = accessModeDict[obj['accessMode']];
	td.html(accessMode['description']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var role = obj['role'];
	td.html(role['roleWithinStudy']);
	td = $('<td>');
	td.addClass('protocol_valign');
	tr.append(td);
	var button = $('<button>');
	button.click(function(event) {removeProtocol($(this), obj);});
	button.html('Remove');
	td.append(button);
	$('#estimateStudyProtocolSelect').val('');
	$('#datasetInstances').val('');
	$('#studyInvestigatorSelect').val('');
	$('#addSiteProtocolButton').attr('disabled', 'disabled');
	$('#manageSitesProtocols').show();
}

function addProtocolViewRow(obj) {
	var node = datasetInstancesDict[obj['datasetInstance']]['node'];
	var site = node['site'];
	var tbody = $('#viewSitesProtocolsTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var lib = librariesDict[toolsDict[obj['tool']]['toolParentLibrary']];
	var method = lib['libraryName'] + ' - ' + toolsDict[obj['tool']]['toolName'];
	td.html(method);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(datasetDefinitionDict[obj['dataset']]['dataSetName']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(datasetInstancesDict[obj['datasetInstance']]['dataSource']);
	td = $('<td>');
	td.addClass('protocol_border');
	tr.append(td);
	td.html(site['siteName']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(node['nodeId']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var accessMode = accessModeDict[obj['accessMode']];
	td.html(accessMode['description']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var role = obj['role'];
	td.html(role['roleWithinStudy']);
	$('#viewSitesProtocols').show();
}

function addProtocolPoliciesRow(obj, appendMode) {
	var tbody = $('#manageStudyProtocolsTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var lib = librariesDict[toolsDict[obj['tool']]['toolParentLibrary']];
	var method = lib['libraryName'] + ' - ' + toolsDict[obj['tool']]['toolName'];
	td.html(method);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(datasetDefinitionDict[obj['dataset']]['dataSetName']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	var accessMode = accessModeDict[obj['accessMode']];
	td.html(accessMode['description']);
	td = $('<td>');
	td.addClass('protocol_valign');
	tr.append(td);
	var button = $('<button>');
	button.click(function(event) {removePoliciesProtocol($(this), obj);});
	button.html('Remove');
	td.append(button);
	//$('.wizard_content_protocol', div).width(div.width());
	$('#modelNames').val('');
	$('#datasetNames').val('');
	$('#studySendOptions').val('');
	$('#addStudyProtocolButton').attr('disabled', 'disabled');
	$('#manageStudyProtocols').show();
	if (appendMode) {
		activeStudy['studyPolicies'].push(obj);
	}
	updateBasicInfo();
}

function addProtocolSelect() {
	var select = $('#estimateStudyProtocolSelect');
	select.html('');
	select.unbind();
	var option = $('<option>');
	option.text('Estimate...');
	option.attr('value', '');
	select.append(option);
	$.each(activeStudy['studyPolicies'], function(i, policy) {
		option = $('<option>');
		var lib = librariesDict[toolsDict[policy['tool']]['toolParentLibrary']];
		var method = lib['libraryName'] + ' ' + toolsDict[policy['tool']]['toolName'];
		var dataset = datasetDefinitionDict[policy['dataset']]['dataSetName'];
		var accessMode = accessModeDict[policy['accessMode']]['description'];
		option.text(method + ' on ' + dataset + ' ' + accessMode);
		option.attr('value', i);
		select.append(option);
	});
	select.change(function(event) {checkAddSiteProtocolButton();});
	
	var select = $('#studyInvestigatorSelect');
	select.html('');
	var option = $('<option>');
	option.text('and role...');
	option.attr('value', '');
	select.append(option);
	$.each(studyRolesList, function(i, studyRole) {
		if (studyRole['study'] == 1) {
			option = $('<option>');
			option.text(studyRole['roleWithinStudy']);
			option.attr('value', studyRole['roleId']);
			select.append(option);
		}
	});
	select.change(function(event) {checkAddSiteProtocolButton();});
}

function addStudyRoleRow(studyRole) {
	var tbody = $('#manageStudyRolesTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	var b = $('<b>');
	td.append(b);
	b.html(studyRole['roleWithinStudy']);
	td = $('<td>');
	td.addClass('protocol_valign');
	tr.append(td);
	var button = $('<button>');
	button.click(function(event) {removeStudyRole($(this), studyRole);});
	button.html('Remove Role');
	td.append(button);
}

function addSiteRow(site) {
	$.each($('option', $('#siteSelect')), function(i, option) {
		if ($(option).attr('value') == site['siteId']) {
			$(option).hide();
			return false;
		}
	});
	var tbody = $('#manageStudySitesTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	var b = $('<b>');
	td.append(b);
	b.html(site['siteName']);
	td = $('<td>');
	td.addClass('protocol_valign');
	tr.append(td);
	var button = $('<button>');
	button.click(function(event) {removeStudySite($(this), site);});
	button.html('Remove Site');
	td.append(button);
}

function removeStudySite(button, site) {
	button.parent().parent().remove();
	$.each($('option', $('#siteSelect')), function(i, option) {
		if ($(option).attr('value') == site['siteId']) {
			$(option).show();
			return false;
		}
	});
	delete activeStudy['sites'][site['siteId']];
	appendStudyContent(activeStudy);
}

function removeStudyRole(button, studyRole) {
	button.parent().parent().remove();
	updateStaffSelectRole(studyRole);
}

function updateStaffSelectRole(studyRole) {
	
}

function removeProtocol(button, obj) {
	var protocolValues = activeStudy['protocols'];
	var roleId = obj['role']['roleId'];
	$.each(protocolValues, function(i, elem) {
		var studyRoleId = elem['role']['roleId'];
		if (elem['tool'] == obj['tool'] && elem['dataset'] == obj['dataset'] && elem['datasetInstance'] == obj['datasetInstance'] && 
				roleId == studyRoleId && elem['accessMode'] == obj['accessMode']) {
			protocolValues.splice(i, 1);
			return false;
		}
	});
	manageStudy(false);
}

function removePoliciesProtocol(button, obj) {
	var protocolValues = activeStudy['studyPolicies'];
	$.each(protocolValues, function(i, elem) {
		if (elem['tool'] == obj['tool'] && elem['dataset'] == obj['dataset'] && 
				elem['accessMode'] == obj['accessMode']) {
			protocolValues.splice(i, 1);
			return false;
		}
	});
	protocolValues = activeStudy['protocols'];
	$.each(protocolValues, function(i, elem) {
		if (elem != null && elem['tool'] == obj['tool'] && elem['dataset'] == obj['dataset'] && 
				elem['accessMode'] == obj['accessMode']) {
			protocolValues.splice(i, 1);
		}
	});
	manageStudy(false);
}

function setActive(id) {
	$.each(myStudies, function(i, study) {
		if (study['studyId'] == id) {
			setActiveStudy(study['studyName']);
			return false;
		}
	});
}

function showAvailableStudies() {
	loadDatasets(emptyValue);
	loadLibraries(emptyValue);
	loadMethods(emptyValue);
	loadSites(emptyValue, false);
	$('#paramsDiv').css('display', 'none');
	$('#statusQueryWrapperDiv').css('display', 'none');
	$('#queryDiv').css('display', 'none');
	$('#studyName').html('Study: ' + getSelectedStudyName());
	renderAvailableDatasets();
}

function displayMyStudies() {
	var url = HOME + '/registry?action=getMyStudies';
	scanner.GET(url, true, postGetMyStudies, null, null, 0);
}

function postGetMyStudies(data, textStatus, jqXHR, param) {
	myStudies = data;
	var div = $('#lisyMyStudiesDiv');
	div.html('');
	$.each(myStudies, function(i, study) {
		study['studyStatusType'] = study['studyStatusType']['description'];
		study['staff'] = getStaff(study['studyId']);
		study['protocols'] = getPolicies(study['studyId']);
		study['sites'] = getSites(study['studyId']);
		study['studyPolicies'] = [];
		appendStudy(study);
	});
	setActiveStudy(getSelectedStudyName());
}

function collapseMyStudies() {
	$('.wizard_content', $('#lisyMyStudiesDiv')).hide();
}

function appendStudy(study) {
	var div = $('#lisyMyStudiesDiv');
	var p = $('<p>');
	div.append(p);
	p.addClass('wizard_heading');
	p.attr('id', 'Study_p_' + study['studyId']);
	var value = 'Study ' + study['studyId'] + ': ' + study['studyName'];
	if (study['description'] != null) {
		value += ' - ' + study['description'];
	}
	var label = $('<label>');
	label.html(value);
	p.append(label);
	var span = $('<span>');
	p.append(span);
	span.addClass('active');
	span.attr({	'id': 'Study_span_' + study['studyId'],
				'study_id': study['studyId']
	});
	var a = $('<a>');
	span.append(a);
	a.attr('href', 'javascript:setActive(' + study['studyId'] + ');');
	a.html('Set Active');
	var contentDiv = $('<div>');
	contentDiv.attr('id', 'Study_div_' + study['studyId']);
	div.append(contentDiv);
	contentDiv.addClass('wizard_content');
	p.click(function() {
		$(this).next(".wizard_content").toggle();
	});
	appendStudyContent(study);
}

function updateBasicInfo() {
	var label = $('label', $('#Study_p_' + activeStudy['studyId']));
	var value = 'Study ' + activeStudy['studyId'] + ': ' + activeStudy['studyName'];
	if (activeStudy['description'] != null) {
		value += ' - ' + activeStudy['description'];
	}
	label.html(value);
	appendStudyContent(activeStudy);
}

function setActiveStudy(name) {
	$.each($('span.active', $('#lisyMyStudiesDiv')), function (i, elem) {
		var span = $(elem);
		span.html('');
		var a = $('<a>');
		span.append(a);
		a.attr('href', 'javascript:setActive(' + span.attr('study_id') + ');');
		a.html('Set Active');
	});
	$.each(myStudies, function(i, study) {
		if (study['studyName'] == name) {
			activeStudy = study;
			return false;
		}
	});
	var span = $('#Study_span_' + activeStudy['studyId']);
	span.html('');
	var b = $('<b>');
	span.append(b);
	b.html('Active');
	collapseMyStudies();
	manageStudy(true);
}

function appendStudyContent(study) {
	var contentDiv = $('#Study_div_' + study['studyId']);
	contentDiv.html('');	
	var h2 = $('<h2>');
	contentDiv.append(h2);
	h2.html('Basic Information');
	var ol = $('<ol>');
	contentDiv.append(ol);
	ol.addClass('blank');
	
	var li = $('<li>');
	ol.append(li);
	var b = $('<b>');
	li.append(b);
	b.html('Title: ');
	var label = $('<label>');
	li.append(label);
	label.html(study['studyName']);
	
	if (study['description'] != null) {
		var li = $('<li>');
		ol.append(li);
		var b = $('<b>');
		li.append(b);
		b.html('Description: ');
		var label = $('<label>');
		li.append(label);
		label.html(study['description']);
	}

	if (study['startDate'] != null) {
		var li = $('<li>');
		ol.append(li);
		var b = $('<b>');
		li.append(b);
		b.html('Start Date: ');
		var label = $('<label>');
		li.append(label);
		label.html(study['startDate']);
	}
	
	if (study['endDate'] != null) {
		var li = $('<li>');
		ol.append(li);
		var b = $('<b>');
		li.append(b);
		b.html('End Date: ');
		var label = $('<label>');
		li.append(label);
		label.html(study['endDate']);
	}
	
	var h2 = $('<h2>');
	contentDiv.append(h2);
	h2.html('Investigators');
	var ol = $('<ol>');
	contentDiv.append(ol);
	ol.addClass('blank');
	$.each(study['staff'], function(i, staff) {
		var li = $('<li>');
		ol.append(li);
		var b = $('<b>');
		li.append(b);
		b.html(staff['role'] + ': ');
		var label = $('<label>');
		li.append(label);
		var user = scannerUsersDict[staff['userId']];
		label.html(user['firstName'] + ' ' + user['lastName']);
	});
	
	var h2 = $('<h2>');
	contentDiv.append(h2);
	h2.html('Protocol');
	var datasets = [];
	var models = [];
	var sites = [];
	var datasetIds = [];
	$.each(datasetDefinitionList, function(i, dataset) {
		if (dataset['originatingStudy'] == study['studyId']) {
			datasets.push(dataset['dataSetName']);
			datasetIds.push(dataset['dataSetDefinitionId']);
		}
	});
	var studyModels = [];
	var studyModelsIds = [];
	$.each(study['studyPolicies'], function(i, obj) {
		var toolId = obj['tool'];
		if (!studyModelsIds.contains(toolId)) {
			studyModelsIds.push(toolId);
			studyModels.push(toolsDict[toolId]);
		}
	});
	studyModels.sort(compareTools);
	$.each(studyModels, function(i, tool) {
		var lib = librariesDict[tool['toolParentLibrary']];
		models.push(lib['libraryName'] + ' - ' + tool['toolName']);
	});
	if (study['sites'] != null) {
		$.each(study['sites'], function(i, site) {
			sites.push(site['siteName']);
		});
	} 
	var ol = $('<ol>');
	contentDiv.append(ol);
	ol.addClass('blank');
	
	var li = $('<li>');
	ol.append(li);
	var b = $('<b>');
	li.append(b);
	b.html('Data Sets:');
	var ol1 = $('<ol>');
	li.append(ol1);
	ol1.addClass('blank');
	$.each(datasets, function (i, dataset) {
		var li1 = $('<li>');
		ol1.append(li1);
		li1.html(dataset);
	});
	
	var li = $('<li>');
	ol.append(li);
	var b = $('<b>');
	li.append(b);
	b.html('Models:');
	var ol1 = $('<ol>');
	li.append(ol1);
	ol1.addClass('blank');
	$.each(models, function (i, model) {
		var li1 = $('<li>');
		ol1.append(li1);
		li1.html(model);
	});
	
	var li = $('<li>');
	ol.append(li);
	var b = $('<b>');
	li.append(b);
	b.html('Participating Sites:');
	var ol1 = $('<ol>');
	li.append(ol1);
	ol1.addClass('blank');
	$.each(sites, function (i, site) {
		var li1 = $('<li>');
		ol1.append(li1);
		li1.html(site);
	});
	contentDiv.hide();
}

function addInstance() {
	addDatasetInstance($('#instanceName').val(), $('#instanceDataSource').val(), $('#nodeNames').val(), $('#instanceDescription').val());
}

function addDatasetInstance(name, datasource, datasetInstance, description) {
	var obj = {};
	obj['dataSetInstanceId'] = ++instanceIdCounter;
	obj['dataSetInstanceName'] = name;
	obj['description'] = description;
	obj['dataSource'] = datasource;
	obj['node'] = datasetInstancesDict[datasetInstance]['node'];
	datasetInstancesList.unshift(obj);
	datasetInstancesDict[instanceIdCounter] = obj;
	var firstOption = $($('option', $('#datasetInstances'))[0]);
	var option = $('<option>');
	option.text(name);
	option.attr('value', instanceIdCounter);
	option.insertAfter(firstOption);
	$('#datasetInstances').val(instanceIdCounter);
	$('#add_site_protocol_div').show();
	$('#add_instance_div').hide();
}

function cancelInstance() {
	$('#datasetInstances').val('');
	$('#add_site_protocol_div').show();
	$('#add_instance_div').hide();
}

function getScrollbarWidth() {
    var div = $('<div style="width:50px;height:50px;overflow:hidden;position:absolute;top:-200px;left:-200px;"><div style="height:100px;"></div>');
    // Append our div, do our calculation and then remove it
    $('body').append(div);
    var w1 = $('div', div).innerWidth();
    div.css('overflow-y', 'scroll');
    var w2 = $('div', div).innerWidth();
    $(div).remove();
    return Math.ceil((w1 - w2) * 3 / 2);
}

function initUsers() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getUsers';
	scanner.RETRIEVE(url, obj, true, postInitUsers, null, null, 0);
}

function postInitUsers(data, textStatus, jqXHR, param) {
	scannerUsersList = data;
	scannerUsersDict = {};
	$.each(data, function(i, user) {
		scannerUsersDict[user['userId']] = user;
	});
	scannerUsersList.sort(compareUsers);
	initDatasetInstances();
}

function initDatasetInstances() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getDatasetInstances';
	scanner.RETRIEVE(url, obj, true, postInitDatasetInstances, null, null, 0);
}

function postInitDatasetInstances(data, textStatus, jqXHR, param) {
	datasetInstancesList = data;
	datasetInstancesDict = {};
	$.each(data, function(i, datasetInstance) {
		if (datasetInstance['dataSetInstanceId'] > instanceIdCounter) {
			instanceIdCounter = datasetInstance['dataSetInstanceId'];
		}
		datasetInstancesDict[datasetInstance['dataSetInstanceId']] = datasetInstance;
	});
	datasetInstancesList.sort(compareNodes);
	initLibraries();
}

function initLibraries() {
	var url = HOME + '/registry';
	var obj = new Object();
	obj['action'] = 'getAllLibraries';
	scanner.RETRIEVE(url, obj, true, postInitLibraries, null, null, 0);
}

function postInitLibraries(data, textStatus, jqXHR, param) {
	librariesDict = {};
	$.each(data, function(i, lib) {
		librariesDict[lib['libraryId']] = lib;
	});
	initTools();
}

function initTools() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getTools';
	scanner.RETRIEVE(url, obj, true, postInitTools, null, null, 0);
}

function postInitTools(data, textStatus, jqXHR, param) {
	toolList = data;
	toolsDict = {};
	$.each(data, function(i, tool) {
		toolsDict[tool['toolId']] = tool;
	});
	toolList.sort(compareTools);
	initDatasets();
}

function initDatasets() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getDatasetDefinitions';
	scanner.RETRIEVE(url, obj, true, postInitDatasets, null, null, 0);
}

function postInitDatasets(data, textStatus, jqXHR, param) {
	datasetDefinitionList = data;
	datasetDefinitionDict = {};
	$.each(data, function(i, dataset) {
		datasetDefinitionDict[dataset['dataSetDefinitionId']] = dataset;
	});
	datasetDefinitionList.sort(compareDatasetDefinitions);
	initUserRoles();
}

function initUserRoles() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getUserRoles';
	scanner.RETRIEVE(url, obj, true, postInitUserRoles, null, null, 0);
}

function postInitUserRoles(data, textStatus, jqXHR, param) {
	userRolesList = data;
	userRolesDict = {};
	$.each(data, function(i, userRole) {
		userRolesDict[userRole['userRoleId']] = userRole;
	});
	userRolesList.sort(compareRoles);
	initStudyPolicies();
}

function initStudyPolicies() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getStudyPolicies';
	scanner.RETRIEVE(url, obj, true, postInitStudyPolicies, null, null, 0);
}

function postInitStudyPolicies(data, textStatus, jqXHR, param) {
	studyPoliciesList = data;
	studyPoliciesDict = {};
	var ids = [];
	accessModeList = [];
	accessModeDict = {};
	$.each(data, function(i, policy) {
		studyPoliciesDict[policy['studyPolicyStatementId']] = policy;
		var accessMode = policy['accessMode'];
		var id = accessMode['accessModeId'];
		if (!ids.contains(id)) {
			ids.push(id);
			accessModeList.push(accessMode);
			accessModeDict[id] = accessMode;
		}
	});
	accessModeList.sort(compareAccessMode);
	initStudyRoles();
}

function initStudyRoles() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getStudyRoles';
	scanner.RETRIEVE(url, obj, true, postInitStudyRoles, null, null, 0);
}

function postInitStudyRoles(data, textStatus, jqXHR, param) {
	studyRolesList = data;
	studyRolesDict = {};
	$.each(data, function(i, role) {
		studyRolesDict[role['roleId']] = role;
	});
	studyRolesList.sort(compareStudyRoles);
	initAnalysisPolicies();
}

function initAnalysisPolicies() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'getAnalysisPolicies';
	scanner.RETRIEVE(url, obj, true, postInitAnalysisPolicies, null, null, 0);
}

function postInitAnalysisPolicies(data, textStatus, jqXHR, param) {
	analysisPolicies = data;
	analysisPolicies.sort(compareAnalysisPolicies);
	initStudies();
}

function initStudies() {
	var url = HOME + '/registry';
	var obj = new Object();
	obj['action'] = 'getAllStudies';
	scanner.RETRIEVE(url, obj, true, postInitStudies, null, null, 0);
}

function postInitStudies(data, textStatus, jqXHR, param) {
	allStudies = data;
	allStudiesDict = {};
	$.each(allStudies, function(i, study) {
		allStudiesDict[study['studyId']] = study;
	});
	initSites();
}

function initSites() {
	var url = HOME + '/registry';
	var obj = new Object();
	obj['action'] = 'getAllSites';
	scanner.RETRIEVE(url, obj, true, postInitSites, null, null, 0);
}

function postInitSites(data, textStatus, jqXHR, param) {
	sitesList = data;
	sitesDict = {};
	$.each(sitesList, function(i, site) {
		sitesDict[site['siteId']] = site;
	});
	setContacts();
}

function compareUsers(user1, user2) {
	var val1 = user1['lastName'] + ' ' + user1['firstName'];
	var val2 = user2['lastName'] + ' ' + user2['firstName'];
	return compareIgnoreCase(val1, val2);
}

function compareNodes(datasetInstance1, datasetInstance2) {
	var node1 = datasetInstance1['node'];
	var node2 = datasetInstance2['node'];
	var val1 = node1['site'] + ':' + node1['nodeId'];
	var val2 = node2['site'] + ':' + node2['nodeId'];
	return compareIgnoreCase(val1, val2);
}

function compareTools(tool1, tool2) {
	var lib1 = librariesDict[tool1['toolParentLibrary']];
	var lib2 = librariesDict[tool2['toolParentLibrary']];
	var val1 = lib1['libraryName'] + tool1['toolName'];
	var val2 = lib2['libraryName'] + tool2['toolName'];
	return compareIgnoreCase(val1, val2);
}

function compareDatasetDefinitions(dataset1, dataset2) {
	var val1 = dataset1['dataSetName'];
	var val2 = dataset2['dataSetName'];
	return compareIgnoreCase(val1, val2);
}

function compareRoles(role1, role2) {
	var studyRole1 = role1['studyRole'];
	var studyRole2 = role2['studyRole'];
	var ret = compareNumbers(studyRole1['study'], studyRole2['study']);
	if (ret == 0) {
		ret = compareNumbers(studyRole1['roleId'], studyRole2['roleId']);
		if (ret == 0) {
			var user1 = scannerUsersDict[role1['user']];
			var user2 = scannerUsersDict[role2['user']];
			ret = compareUsers(user1, user2);
		}
	}
	return ret;
}

function compareStudyRoles(studyRole1, studyRole2) {
	var ret = compareNumbers(studyRole1['study'], studyRole2['study']);
	if (ret == 0) {
		ret = compareIgnoreCase(studyRole1['roleWithinStudy'], studyRole2['roleWithinStudy']);
	}
	return ret;
}

function compareAccessMode(accessMode1, accessMode2) {
	var val1 = accessMode1['description'];
	var val2 = accessMode2['description'];
	return compareIgnoreCase(val1, val2);
}

function compareAnalysisPolicies(analysisPolicy1, analysisPolicy2) {
	var studyPolicy1 = studyPoliciesDict[analysisPolicy1['parentStudyPolicyStatement']];
	var studyPolicy2 = studyPoliciesDict[analysisPolicy2['parentStudyPolicyStatement']];
	var ret = compareNumbers(studyPolicy1['study'], studyPolicy2['study']);
	if (ret == 0) {
		ret = compareNumbers(studyPolicy1['dataSetDefinition'], studyPolicy2['dataSetDefinition']);
		if (ret == 0) {
			ret = compareNumbers(analysisPolicy1['analysisTool'], analysisPolicy2['analysisTool']);
			if (ret == 0) {
				ret = compareNumbers(analysisPolicy1['dataSetInstance'], analysisPolicy2['dataSetInstance']);
				if (ret == 0) {
					ret = compareNumbers(analysisPolicy1['accessMode']['accessModeId'], analysisPolicy2['accessMode']['accessModeId']);
				}
			}
		}
	}
	return ret;
}

function compareNumbers(val1, val2) {
	var ret = 0;
	if (val1 < val2) {
		ret = -1;
	} else if (val1 > val2) {
		ret = 1;
	}
	return ret;
}

function getStaff(study) {
	var ret = [];
	$.each(userRolesList, function(i, userRole) {
		var studyRole = userRole['studyRole'];
		if (studyRole['study'] == study) {
			var roleId = studyRole['roleId'];
			var obj = {};
			obj['userId'] = userRole['user'];
			obj['role'] = studyRole['roleWithinStudy'];
			ret.push(obj);
		}
	});
	return ret;
}

function getStudyPolicies(study) {
	var ret = [];
	var policyDict = {};
	$.each(analysisPolicies, function(i, policy) {
		var parentStudyPolicyStatement = policy['parentStudyPolicyStatement'];
		$.each(studyPoliciesList, function(j, studyPolicy) {
			if (studyPolicy['studyPolicyStatementId'] == parentStudyPolicyStatement && studyPolicy['study'] == study) {
				var obj = {};
				obj['dataset'] = studyPolicy['dataSetDefinition'];
				obj['tool'] = policy['analysisTool'];
				obj['accessMode'] = policy['accessMode']['accessModeId'];
				var key = 'tool=' + obj['tool'] + ',dataset=' + obj['dataset'] + ',accessMode=' + obj['accessMode'];
				if (policyDict[key] == null) {
					policyDict[key] = obj;
					ret.push(obj);
				}
				return false;
			}
		});
	});
	return ret;
}

function getPolicies(study) {
	var ret = [];
	var policyDict = {};
	$.each(analysisPolicies, function(i, policy) {
		var parentStudyPolicyStatement = policy['parentStudyPolicyStatement'];
		$.each(studyPoliciesList, function(j, studyPolicy) {
			if (studyPolicy['studyPolicyStatementId'] == parentStudyPolicyStatement && studyPolicy['study'] == study) {
				var obj = {};
				if (study == 1) {
					obj['role'] = studyRolesDict[3];
				}
				obj['dataset'] = studyPolicy['dataSetDefinition'];
				obj['tool'] = policy['analysisTool'];
				obj['datasetInstance'] = policy['dataSetInstance'];
				obj['accessMode'] = policy['accessMode']['accessModeId'];
				if (policyDict[obj['tool']] == null) {
					policyDict[obj['tool']] = [];
				}
				if (!policyDict[obj['tool']].contains(obj['datasetInstance'])) {
					policyDict[obj['tool']].push(obj['datasetInstance']);
					ret.push(obj);
				}
				return false;
			}
		});
	});
	return ret;
}

function getSites(study) {
	var ret = {};
	var datasetIds = [];
	$.each(datasetDefinitionList, function(i, dataset) {
		if (dataset['originatingStudy'] == study) {
			datasetIds.push(dataset['dataSetDefinitionId']);
		}
	});
	$.each(datasetInstancesList, function(i, instance) {
		if (datasetIds.contains(instance['dataSetDefinition'])) {
			var node = instance['node'];
			var site = node['site'];
			ret[site['siteId']] = site;
		}
	});
	return ret;
}

function showPrepToResearch() {
	$('#tabContainerWrapperDiv').height(1100);
	scannerTabContainer.resize();
	$('#paramsWrapperDiv').hide();
	$('#replayDivWrapper').show();
	$('#replayTitle').hide();
	$('#expandResults').hide();
	$('#collapseResults').hide();
	$('#replayDivContent').hide();
}

