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

var slicesLayout = [[
                     {'name': '#', 'field': 'no', 'width': '5%'},
                     {'name': 'Study', 'field': 'study', 'width': '15%'},
                     {'name': 'Dataset', 'field': 'dataset', 'width': '15%'},
                     {'name': 'Library', 'field': 'library','width': '10%'},
                     {'name': 'Method', 'field': 'method', 'width': '15%'},
                     {'name': 'Site', 'field': 'site', 'width': '10%'},
                     {'name': 'Datasource', 'field': 'datasource', 'width': '15%'},
                     {'name': 'Users', 'field': 'users', 'width': '15%'}
                     ]];

var projectsLayoutOld = [[
                       {'name': 'Project Title', 'field': 'title', 'width': '20%'},
                       {'name': 'Principal Investigator', 'field': 'principal', 'width': '30%'},
                       {'name': 'Project Description', 'field': 'description', 'width': '50%'}
                       ]];

var projectsLayout = [[
                       {'name': 'Project Title', 'field': 'title', 'width': '20%'},
                       {'name': 'Principal Status', 'field': 'status', 'width': '20%'},
                       {'name': 'Principal Investigator', 'field': 'principal', 'width': '20%'},
                       {'name': 'Project Description', 'field': 'description', 'width': '40%'}
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
			} else if (divId == 'manageStudiesDiv') {
				manageStudy();
			} else if (divId == 'myStudiesDiv') {
				collapseMyStudies();
			}
		}
	});
	submitLogin();
}

/**
 * @return the value of the selected study
 */
function getSelectedStudyName() {
	return studiesMultiSelect.get('value')[0];
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
	$('#datasetHelp').html('');
	$('#libraryHelp').html('');
	$('#methodHelp').html('');
	$('#paramsTitle').css('display', 'none');
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
	loadDatasets(datasets);
}

/**
 * Send the request to get the available libraries
 */
function renderAvailableLibraries() {
	// send the request
	var method = getSelectedMethodName();
	if (method != null && method.replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && method != emptyValue) {
		$('#methodHelp').html(availableMethods[getSelectedMethodName()]);
		var sites = getSelectedSitesNames();
		var sitesURL = '';
		if (sites.length > 0) {
			sitesURL = '&site=';
			var encodedSites = [];
			$.each(sites, function(i, site) {
				encodedSites.push(encodeSafeURIComponent(site));
			});
			sitesURL += valueToString(encodedSites);
		}
		var url = HOME + '/query?action=getLibraries' + sitesURL +
		'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
		'&method=' + encodeSafeURIComponent(getSelectedMethodName()) +
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
	var sites = getSelectedSitesNames();
	var sitesURL = '';
	if (sites.length > 0) {
		sitesURL = '&site=';
		var encodedSites = [];
		$.each(sites, function(i, site) {
			encodedSites.push(encodeSafeURIComponent(site));
		});
		sitesURL += valueToString(encodedSites);
	}
	var url = HOME + '/query?action=getMethods' + sitesURL +
		'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
		'&dataset=' + encodeSafeURIComponent(getSelectedDatasetName());
	scanner.GET(url, true, postRenderAvailableMethods, null, null, 0);
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
	var func = getSelectedMethodName();
	var lib = getSelectedLibraryName();
	var dataset = getSelectedDatasetName();
	if (lib != '' && lib.replace(/^\s*/, "").replace(/\s*$/, "").length > 0 && lib != emptyValue) {
		$('#libraryHelp').html(availableLibraries[lib]);
		var url = HOME + '/query?action=getParameters&method=' + encodeSafeURIComponent(func) + 
			'&library=' + encodeSafeURIComponent(lib);
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
	var paramsTitle = $('#paramsTitle');
	paramsTitle.css('display', '');
	paramsTitle.html(getSelectedLibraryName() + '/' + getSelectedMethodName());
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
	}
}

function renderParam(div, param, index, table, select) {
	$.each(param, function(key, value) {
		var metadata = value['metadata'];
		if (metadata['quickHelp'] == 'true') {
			//$('#paramsHelp').append(metadata['description']).append($('<br/><br/>'));
		}
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
				option.attr('value', metadata['cname']);
				if (metadata['selected'] != null) {
					option.attr('selected', 'selected');
				}
				select.append(option);
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
	$('#datasetHelp').html(availableDatasets[dataset]);
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
		$('#queryDivContent').hide();
		$('#expandQueryResultsArrow').show();
		$('#collapseQueryResultsArrow').hide();
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
	a.attr('href', 'javascript:buildBoxPlot("'+tab+'");');
	a.html('Box plot');
	resultDiv.append(a);
	resultDiv.append('&nbsp;&nbsp;');
	a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildTable("'+tab+'");');
	a.html('Data table');
	resultDiv.append(a);
	buildStatusDataTable(tab);
	buildBoxPlot(tab);
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
	a.attr('href', 'javascript:buildBoxPlot("'+tab+'");');
	a.html('Box plot');
	resultDiv.append(a);
	resultDiv.append('&nbsp;&nbsp;');
	a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildTable("'+tab+'");');
	a.html('Data table');
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
	a.attr('href', 'javascript:buildBoxPlot("'+tab+'");');
	a.html('Box plot');
	resultDiv.append(a);
	resultDiv.append('&nbsp;&nbsp;');
	a = $('<a>');
	a.addClass('link-style banner-text');
	a.attr('href', 'javascript:buildTable("'+tab+'");');
	a.html('Data table');
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

function setContacts(contacts) {
	var studies = {};
	var sites = {};
	var master = [];
	$.each(contacts, function(i, contact) {
		var rtype = contact['rtype'];
		if (rtype == 'study') {
			studies[contact['cname']] = contact;
		} else if (rtype == 'site') {
			sites[contact['cname']] = contact;
		} else if (rtype == 'master') {
			master.push(contact);
		}
	});
	var div = $('#contactsDiv');
	var h4 = $('<h4>');
	div.append(h4);
	h4.html('Project contact information');
	addContact(div, master[0]);
	h4 = $('<h4>');
	div.append(h4);
	h4.html('Participating site contact information');
	var names = [];
	$.each(sites, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	$.each(names, function(i, name) {
		var h6 = $('<h6>');
		div.append(h6);
		h6.html('Site ' + name);
		addContact(div, sites[name]);
	});
	h4 = $('<h4>');
	div.append(h4);
	h4.html('Studies related information');
	names = [];
	$.each(studies, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	$.each(names, function(i, name) {
		var h6 = $('<h6>');
		div.append(h6);
		h6.html('Study ' + name);
		addContact(div, studies[name]);
	});
}

function addContact(div, elem) {
	var table = $('<table>');
	table.addClass('contactsTable');
	div.append(table);
	var tbody = $('<tbody>');
	table.append(tbody);
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	var table1 = $('<table>');
	table1.addClass('contactsTable');
	td.append(table1);
	var tbody1 = $('<tbody>');
	table1.append(tbody1);
	var tr1 = $('<tr>');
	tbody1.append(tr1);
	var td1 = $('<td>');
	tr1.append(td1);
	td1.html(elem['title'] + ':');
	td1 = $('<td>');
	tr1.append(td1);
	td1.html(elem['contact']);
	tr1 = $('<tr>');
	tbody1.append(tr1);
	td1 = $('<td>');
	tr1.append(td1);
	td1.html('Email:');
	td1 = $('<td>');
	tr1.append(td1);
	td1.html(elem['email']);
	tr1 = $('<tr>');
	tbody1.append(tr1);
	td1 = $('<td>');
	tr1.append(td1);
	td1.html('Phone:');
	td1 = $('<td>');
	tr1.append(td1);
	td1.html(elem['phone']);
	tr1 = $('<tr>');
	tbody1.append(tr1);
	td1 = $('<td>');
	tr1.append(td1);
	td1.html('Website:');
	td1 = $('<td>');
	tr1.append(td1);
	var a = $('<a>');
	td1.append(a);
	a.attr({'href': elem['website'],
		'target': '_newtab2'
	});
	a.html(elem['website']);
	td = $('<td>');
	td.attr('valign', 'top');
	tr.append(td);
	var table2 = $('<table>');
	//table2.attr({'cellspacing': '10'});
	table2.addClass('contactsTable');
	td.append(table2);
	var tbody2 = $('<tbody>');
	table2.append(tbody2);
	var tr2 = $('<tr>');
	tbody2.append(tr2);
	var td2 = $('<td>');
	tr2.append(td2);
	td2.attr('valign', 'top');
	td2.html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Address:');
	td2 = $('<td>');
	tr2.append(td2);
	td2.html(elem['address']);
	if (elem['agreement'] != null) {
		tr2 = $('<tr>');
		tbody2.append(tr2);
		td2 = $('<td>');
		tr2.append(td2);
		td2.attr('valign', 'top');
		td2.html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Agreement:');
		td2 = $('<td>');
		tr2.append(td2);
		var a = $('<a>');
		td2.append(a);
		a.attr({'href': elem['agreement'],
			'target': '_newtab2'
		});
		a.html(elem['agreement']);
	} else if (elem['approvals'] != null) {
		tr2 = $('<tr>');
		tbody2.append(tr2);
		td2 = $('<td>');
		tr2.append(td2);
		td2.attr('valign', 'top');
		td2.html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Approvals:');
		td2 = $('<td>');
		tr2.append(td2);
		var a = $('<a>');
		td2.append(a);
		a.attr({'href': elem['approvals'],
			'target': '_newtab2'
		});
		a.html(elem['approvals']);
	}
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
		setContacts(res['contacts']);
		renderAvailableStudies();
		renderSitesStatus();
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
	displayMyStudies();
}

function downloadFile() {
	var url = HOME + '/query';
	var obj = new Object();
	obj['action'] = 'file';
	scanner.POST(url, obj, true, postDownloadFile, null, null, 0);
}

function postDownloadFile(data, textStatus, jqXHR, param) {
	var res = $.parseJSON(data);
	if (res['status'] == 'success') {
		registryUserLogout();
	} else {
		alert(res['status']);
	}
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
				$('#datasetHelp').html('');
				$('#libraryHelp').html('');
				$('#methodHelp').html('');
				$('#paramsTitle').css('display', 'none');
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
				$('#datasetHelp').html('');
				$('#libraryHelp').html('');
				$('#methodHelp').html('');
				$('#paramsTitle').css('display', 'none');
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
				$('#libraryHelp').html('');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				var selValues = librariesMultiSelect.get('value');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableParameters();
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
				loadLibraries(emptyValue, false);
				var selValues = methodsMultiSelect.get('value');
				$('#methodHelp').html('');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableLibraries();
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
				loadMethods(emptyValue, false);
				loadLibraries(emptyValue, false);
				var selValues = sitesMultiSelect.get('value');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#statusQueryWrapperDiv').css('display', 'none');
				$('#queryDiv').css('display', 'none');
				if (selValues != null) { 
					if (selValues.length >= 1) {
						renderAvailableMethods();
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
	$('#paramsWrapperDiv').show();
	$('#replayDivContent').html('');
	$('#replayDivWrapper').hide();
	$('#statusReplayWrapperDiv').hide();
}

/**
 * Actions to be performed when the "Query" tab is deselected
 */
function hideQuery() {
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
	$('#expandResults').show();
	$('#collapseResults').hide();
	$('#replayDivContent').hide();
	submitQuery('replayDivContent', item, index, null);
}

function displayQueryStatus(index) {
	var tableIndex = analyzeDataStore.items.length - index - 1;
	var item = analyzeDataStore.items[tableIndex];
	$('#replayTitle').show();
	$('#expandResults').show();
	$('#collapseResults').hide();
	$('#replayDivContent').hide();
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
	$('#expandResults').show();
	$('#collapseResults').hide();
	$('#replayDivContent').hide();
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

function manageStudy() {
	if (activeStudy == null) {
		$('#manageStudyH2').html('There are no studies to manage.');
		$('#updateStudyDiv').hide();
		return;
	}
	$('#manageStudyH2').html('Manage Research Study ' + activeStudy['id'] + ': ' + activeStudy['title']);
	if (activeStudy['description'] != null) {
		$('#manageStudyH2').html($('#manageStudyH2').html() + ' - ' + activeStudy['description']);
	}
	$('#updateStudyDiv').show();
	$('.wizard_heading', $('#manageStudiesDiv')).unbind('click');
	$('.wizard_content', $('#manageStudiesDiv')).hide();
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
	$('#projectTitle').val(activeStudy['title']);
	$('#projectDescription').val('');
	$('#projectStartDate').val('');
	$('#projectEndDate').val('');
	if (activeStudy['description'] != null) {
		$('#projectDescription').val(activeStudy['description']);
	}
	if (activeStudy['startDate'] != null) {
		$('#projectStartDate').val(activeStudy['startDate']);
	}
	if (activeStudy['endDate'] != null) {
		$('#projectEndDate').val(activeStudy['endDate']);
	}
	var select = $('#staffNames');
	select.html('');
	select.change(function(event) {checkAddStaffButton();});
	var option = $('<option>');
	option.text('Enter Letters of Name.');
	option.attr('value', '');
	select.append(option);
	$.each(investigatorsList, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	var select = $('#staffRoles');
	select.html('');
	select.change(function(event) {checkAddStaffButton();});
	var option = $('<option>');
	option.text('Select Role...');
	option.attr('value', '');
	select.append(option);
	$.each(investigatorsRoles, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	var select = $('#studySites');
	select.html('');
	select.change(function(event) {checkAddStaffButton();});
	var option = $('<option>');
	option.text('...add site');
	option.attr('value', '');
	select.append(option);
	$.each(siteNames, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	
	var select = $('#modelNames');
	select.html('');
	select.change(function(event) {checkAddProtocolButton();});
	var option = $('<option>');
	option.text('Estimate model...');
	option.attr('value', '');
	select.append(option);
	$.each(modelNames, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	var select = $('#siteNames');
	select.html('');
	select.change(function(event) {checkAddProtocolButton();});
	var option = $('<option>');
	option.text('for site...');
	option.attr('value', '');
	select.append(option);
	$.each(siteNames, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	var select = $('#datasetNames');
	select.html('');
	select.change(function(event) {checkAddProtocolButton();});
	var option = $('<option>');
	option.text('using data set...');
	option.attr('value', '');
	select.append(option);
	$.each(datasetNames, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	var select = $('#datasetInstances');
	select.html('');
	select.change(function(event) {checkAddProtocolButton();});
	var option = $('<option>');
	option.text('and data set instance...');
	option.attr('value', '');
	select.append(option);
	$.each(datasetInstances, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	var select = $('#studySendOptions');
	select.html('');
	select.change(function(event) {checkAddProtocolButton();});
	var option = $('<option>');
	option.text('and release results...');
	option.attr('value', '');
	select.append(option);
	$.each(releaseResultsPolicy, function(i, name) {
		option = $('<option>');
		option.text(name);
		option.attr('value', name);
		select.append(option);
	});
	$('#addStaffButton').attr('disabled', 'disabled');
	$('#addProtocolButton').attr('disabled', 'disabled');
	if ($('#manageStudyProtocolsTbody').children().length == 0) {
		$('#manageStudyProtocols').hide();
	}
	$('#manageStudyStaffTbody').html('');
	$.each(activeStudy['staff'], function(i, staff) {
		addStaffRow(staff);
	});
	$('#manageStudyProtocolsTbody').html('');
	$.each(activeStudy['protocol'], function(i, protocol) {
		addProtocolRow(protocol);
	});
}

function checkCreateStudyButton() {
	if ($('#studyTitleInput').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0) {
		$('#createStudyButton').removeAttr('disabled');
	} else {
		$('#createStudyButton').attr('disabled', 'disabled');
	}
}

function checkAddStaffButton() {
	if ($('#staffNames').val() != '' && $('#staffRoles').val() != '' && $('#studySites').val() != '') {
		$('#addStaffButton').removeAttr('disabled');
	} else {
		$('#addStaffButton').attr('disabled', 'disabled');
	}
}

function checkAddProtocolButton() {
	if ($('#modelNames').val() != '' && $('#siteNames').val() != '' && $('#datasetNames').val() != '' && 
			$('#datasetInstances').val() != '' && $('#studySendOptions').val() != '') {
		$('#addProtocolButton').removeAttr('disabled');
	} else {
		$('#addProtocolButton').attr('disabled', 'disabled');
	}
}

function addStaff() {
	var obj = {};
	obj['name'] = $('#staffNames').val();
	obj['role'] = $('#staffRoles').val();
	obj['site'] = $('#studySites').val();
	activeStudy['staff'].push(obj);
	addStaffRow(obj);
	updateBasicInfo(activeStudy);
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
	td.html(obj['name']);
	td = $('<td>');
	tr.append(td);
	td.html(obj['site']);
	td = $('<td>');
	tr.append(td);
	var a = $('<a>');
	a.attr('href', 'mailto:' + investigatorsMails[obj['name']] + '@' + obj['site'].toLowerCase());
	a.html(investigatorsMails[obj['name']] + '@' + obj['site'].toLowerCase());
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
		if (elem['name'] == obj['name'] && elem['role'] == obj['role'] && elem['site'] == obj['site']) {
			staffValues.splice(i, 1);
			return false;
		}
	});
	button.parent().parent().remove();
	updateBasicInfo(activeStudy);
}

function manageStudies() {
	alert('Not yet implemented: "manageStudies".');
}

function manageDatasets() {
	alert('Not yet implemented: "manageDatasets".');
}

function manageSites() {
	alert('Not yet implemented: "manageSites".');
}

function getSiteName(siteName, url) {
	url = url.split('//');
	url = url[1].split('/');
	var value = sitesMap[url[0]];
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

/**
 * Load the datasets dropdown box with the specified values
 * 
 * @param values
 * 	the array with the datasets names
 * 	the id of the tree
 */
var investigatorsList = ['Chris Anderson',
                         'Jordan Harris',
                         'Bill Johnston',
                         'Jose Martinez',
                         'Joe Miller',
                         'John Smith',
                         'Mark Thomas',
                         'Mary Williams'
                         ];

var investigatorsMails = {'Chris Anderson': 'canderson',
                         'Jordan Harris': 'jharris',
                         'Bill Johnston': 'bjohnston',
                         'Jose Martinez': 'jmartinez',
                         'Joe Miller': 'jmiller',
                         'John Smith': 'jsmith',
                         'Mark Thomas': 'mthomas',
                         'Mary Williams': 'mwilliams'
};

var investigatorsRoles = ['Site PI',
                          'Co Investigator',
                          'Co PI',
                          'Project Manager',
                          'Research Assistant',
                          'Delegate'
                          ];

var siteNames = ['UCSD',
                 'Lahey',
                 'RAND',
                 'USC'
                          ];

var modelNames = ['GLORE - Cox PH',
                  'GLORE - Logit',
                  'OCEANS - Logit',
                  'OCEANS - PH'
                  ];

var datasetNames = ['BEARI SN Diagnostic',
                    'Med Surveillance',
                    'Med Surveillance V1',
                    'MTM Simulated',
                    'MTM DS V1.2',
                    'MTM DS V2.1'
                    ];

var datasetInstances = ['MTM 1',
                        'MTM 2',
                        'MTM 3',
                        'MTM V1.0 NODE17 SITE2',
	                    'MTM V1.3 NODE17 SITE2',
	                    'MTM V1.4',
	                    'Add new...'
	                    ];

var datasetInstancesMap = {	'MTM 1': {'url': 'https://scanner-node3.misd.isi.edu:8888/scanner', 'node': 'RAND'},
							'MTM 2': {'url': 'https://scanner-node2.misd.isi.edu:8888/scanner', 'node': 'UCSD'},
							'MTM 3': {'url': 'https://scanner-node1.misd.isi.edu:8888/scanner', 'node': 'USC'},
							'MTM V1.0 NODE17 SITE2': {'url': 'https://a.b.com/n12s2v1.0', 'node': 'ISI1'},
		                    'MTM V1.3 NODE17 SITE2': {'url': 'https://a.b.com/n12s2v1.3', 'node': 'ISI2'},
		                    'MTM V1.4': {'url': 'https://a.b.com/mtmv1.0', 'node': 'USC1'}
							};

var releaseResultsPolicy = ['instantly',
		                    'manually'
		                    ];

var studyCounter = 200;

var myStudies = [ {"staff":[{"name":"Bill Johnston","role":"Project Manager","site":"UCSD"},
                            {"name":"Mark Thomas","role":"Site PI","site":"USC"}],
                   "protocol":[{"method":"OCEANS - Logit","site":"UCSD","dataset":"MTM Simulated","url":"https://scanner-node3.misd.isi.edu:8888/scanner","node":"RAND","releasePolicy":"instantly"},
                               {"method":"OCEANS - Logit","site":"RAND","dataset":"MTM Simulated","url":"https://scanner-node2.misd.isi.edu:8888/scanner","node":"UCSD","releasePolicy":"instantly"},
                               {"method":"OCEANS - Logit","site":"USC","dataset":"MTM Simulated","url":"https://scanner-node1.misd.isi.edu:8888/scanner","node":"USC","releasePolicy":"manually"},
                               {"method":"GLORE - Logit","site":"UCSD","dataset":"MTM Simulated","url":"https://scanner-node3.misd.isi.edu:8888/scanner","node":"RAND","releasePolicy":"instantly"},
                               {"method":"GLORE - Logit","site":"RAND","dataset":"MTM Simulated","url":"https://scanner-node2.misd.isi.edu:8888/scanner","node":"UCSD","releasePolicy":"instantly"},
                               {"method":"GLORE - Logit","site":"USC","dataset":"MTM Simulated","url":"https://scanner-node1.misd.isi.edu:8888/scanner","node":"USC","releasePolicy":"instantly"}],
                   "id":200,
                   "title":"MTM",
                   "description":"Test",
                   "startDate":"2013-08-14",
                   "endDate":"2013-09-14"
                   }
                 ];

var activeStudy = null;

function createStudy() {
	activeStudy = {};
	myStudies.push(activeStudy);
	activeStudy['staff'] = [];
	activeStudy['protocol'] = [];
	activeStudy['id'] = ++studyCounter;
	activeStudy['title'] = $('#studyTitleInput').val();
	$('#projectTitle').val($('#studyTitleInput').val());
	appendStudy(activeStudy);
	setActive(activeStudy['id']);
	$('#manageStudiesDivLink').click();
	//sendStudy(obj);
	
}

function updateStudy() {
	if ($('#projectTitle').val().replace(/^\s*/, "").replace(/\s*$/, "").length == 0) {
		alert('The study "Title" can not be empty.');
		return;
	}
	activeStudy['title'] = $('#projectTitle').val();
	$('#manageStudyH2').html('Manage Research Study ' + activeStudy['id'] + ': ' + activeStudy['title']);
	if ($('#projectDescription').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0) {
		activeStudy['description'] = $('#projectDescription').val();
		$('#manageStudyH2').html($('#manageStudyH2').html() + ' - ' + activeStudy['description']);
	}
	if ($('#projectStartDate').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0) {
		activeStudy['startDate'] = $('#projectStartDate').val();
	}
	if ($('#projectEndDate').val().replace(/^\s*/, "").replace(/\s*$/, "").length > 0) {
		activeStudy['endDate'] = $('#projectEndDate').val();
	}
	updateBasicInfo(activeStudy);
	//sendStudy(obj);
	
}

function addProtocol() {
	var obj = {};
	obj['method'] = $('#modelNames').val();
	obj['site'] = $('#siteNames').val();
	obj['dataset'] = $('#datasetNames').val();
	obj['url'] = datasetInstancesMap[$('#datasetInstances').val()]['url'];
	obj['node'] = datasetInstancesMap[$('#datasetInstances').val()]['node'];
	obj['releasePolicy'] = $('#studySendOptions').val();
	activeStudy['protocol'].push(obj);
	addProtocolRow(obj);
	updateBasicInfo(activeStudy);
}

function addProtocolRow(obj) {
	var tbody = $('#manageStudyProtocolsTbody');
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html('Estimate ' + obj['method']);
	td = $('<td>');
	td.addClass('protocol_border');
	tr.append(td);
	td.html('for ' + obj['site']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html('using ' + obj['dataset']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(obj['url']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html(obj['node']);
	td = $('<td>');
	tr.append(td);
	td.addClass('protocol_border');
	td.html('release results ' + obj['releasePolicy']);
	td = $('<td>');
	td.addClass('protocol_valign');
	tr.append(td);
	var div = $('<div>');
	div.addClass('protocol_valign');
	td.append(div);
	var button = $('<button>');
	div.append(button);
	button.html('Status');
	button.addClass('wizard_heading_protocol');
	var statusDiv = $('<div>');
	div.append(statusDiv);
	statusDiv.addClass('wizard_content_protocol');
	statusDiv.html(obj['releasePolicy'] == 'instantly' ? 'Approved' : 'Waiting for approval');
	$('.wizard_content_protocol', div).hide();
	$('.wizard_heading_protocol', div).click(function() {
		$(this).next(".wizard_content_protocol").toggle();
	});
	td = $('<td>');
	td.addClass('protocol_valign');
	tr.append(td);
	var button = $('<button>');
	button.click(function(event) {removeProtocol($(this), obj);});
	button.html('Remove');
	td.append(button);
	$('.wizard_content_protocol', div).width(div.width());
	$('#modelNames').val('');
	$('#siteNames').val('');
	$('#datasetNames').val('');
	$('#datasetInstances').val('');
	$('#studySendOptions').val('');
	$('#addProtocolButton').attr('disabled', 'disabled');
	$('#manageStudyProtocols').show();
}

function removeProtocol(button, obj) {
	var protocolValues = activeStudy['protocol'];
	$.each(protocolValues, function(i, elem) {
		if (elem['method'] == obj['method'] && elem['dataset'] == obj['dataset'] && elem['site'] == obj['site'] && 
				elem['url'] == obj['url'] && elem['node'] == obj['node'] && elem['releasePolicy'] == obj['releasePolicy']) {
			protocolValues.splice(i, 1);
			return false;
		}
	});
	button.parent().parent().remove();
	if ($('#manageStudyProtocolsTbody').children().length == 0) {
		$('#manageStudyProtocols').hide();
	}
	updateBasicInfo(activeStudy);
}

function setActive(id) {
	$.each(myStudies, function(i, study) {
		if (study['id'] == id) {
			setActiveStudy(study['title']);
			return false;
		}
	});
}

function displayMyStudies() {
	var div = $('#lisyMyStudiesDiv');
	div.html('');
	$.each(myStudies, function(i, study) {
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
	p.attr('id', 'Study_p_' + study['id']);
	var value = 'Study ' + study['id'] + ': ' + study['title'];
	if (study['description'] != null) {
		value += ' - ' + study['description'];
	}
	var label = $('<label>');
	label.html(value);
	p.append(label);
	var span = $('<span>');
	p.append(span);
	span.addClass('active');
	span.attr({	'id': 'Study_span_' + study['id'],
				'study_id': study['id']
	});
	var a = $('<a>');
	span.append(a);
	a.attr('href', 'javascript:setActive(' + study['id'] + ');');
	a.html('Set Active');
	var contentDiv = $('<div>');
	contentDiv.attr('id', 'Study_div_' + study['id']);
	div.append(contentDiv);
	contentDiv.addClass('wizard_content');
	p.click(function() {
		$(this).next(".wizard_content").toggle();
	});
	appendStudyContent(study);
}

function updateBasicInfo(study) {
	var label = $('label', $('#Study_p_' + study['id']));
	var value = 'Study ' + study['id'] + ': ' + study['title'];
	if (study['description'] != null) {
		value += ' - ' + study['description'];
	}
	label.html(value);
	appendStudyContent(study);
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
		if (study['title'] == name) {
			activeStudy = study;
			return false;
		}
	});
	var span = $('#Study_span_' + activeStudy['id']);
	span.html('');
	var b = $('<b>');
	span.append(b);
	b.html('Active');
	collapseMyStudies();
	manageStudy();
}

function appendStudyContent(study) {
	var contentDiv = $('#Study_div_' + study['id']);
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
	label.html(study['title']);
	
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
	if (study['staff'] != null && study['staff'].length > 0) {
		var h2 = $('<h2>');
		contentDiv.append(h2);
		h2.html('Investigators');
		var ol = $('<ol>');
		contentDiv.append(ol);
		ol.addClass('blank');
		$.each(study['staff'], function (i, staff) {
			var li = $('<li>');
			ol.append(li);
			var b = $('<b>');
			li.append(b);
			b.html(staff['role'] + ': ');
			var label = $('<label>');
			li.append(label);
			label.html(staff['name']);
		});
	}
	if (study['protocol'] != null && study['protocol'].length > 0) {
		var h2 = $('<h2>');
		contentDiv.append(h2);
		h2.html('Protocol');
		var datasets = [];
		var models = [];
		var sites = [];
		$.each(study['protocol'], function (i, protocol) {
			var model = protocol['method'];
			if (!models.contains(model)) {
				models.push(model);
			}
			var site = protocol['site'];
			if (!sites.contains(site)) {
				sites.push(site);
			}
			var dataset = protocol['dataset'];
			if (!datasets.contains(dataset)) {
				datasets.push(dataset);
			}
		});
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
	}
	contentDiv.hide();
}

