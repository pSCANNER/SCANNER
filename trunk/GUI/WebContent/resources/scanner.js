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
var oHistoryTable = null;

var oErrorQueryTable = null;
var oErrorHistoryTable = null;

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

var availableStudies = {};
var availableDatasets = {};
var availableLibraries = {};
var availableMethods = {};
var availableSites = {};
var availableParameters = {};

var descriptionId = 0;
var tipBox;
var alertErrorDialog;

var emptyValue = ['&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'];

var historyLayout = [[
                      {'name': 'Date', 'field': 'date', 'width': '15%'},
                      {'name': 'Study', 'field': 'study', 'width': '15%'},
                      {'name': 'Dataset', 'field': 'dataset', 'width': '15%'},
                      {'name': 'Library', 'field': 'library','width': '10%'},
                      {'name': 'Method', 'field': 'method', 'width': '15%'},
                      {'name': 'Options', 'field': 'options', 'width': '15%'},
                      {'name': 'Privacy budget', 'field': 'budget', 'width': '15%'}
                      ]];

var historyDataStore = {
		identifier: "id",
		items: []
};

var historyGrid = null;
var queriesCounter = 0;
var studiesCounter = [];

var parametersDescription = null;
var parametersBody = null;

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

/**
 * @return the string representing the JSONObject of the selected parameters
 * Example: 
   {
    "dependentVariableName":"Outcome",
    "independentVariableNames":["Age","Race_Cat","Creatinine","CAD","LOS","Diabetes"]
   }
 */
function getSelectedParameters() {
	var params = copyObject(parametersBody);
	$.each(params, function(key2, res2) {
		$.each(res2, function(key3, res3) {
			$.each(res3, function(key, res) {
				if ($.type(res) == 'string') {
					//params[key2][key3][key] = '' + $('#param_'+key).html();
					params[key2][key3][key] = '' + $('#param_'+makeId(key)).val();
				}
				else if (res.length == 1 && res[0] == 'string') {
					/*
					if ($.isNumeric($('#param_'+key).val())) {
						params[key2][key3][key] = '"' + $('#param_'+key).val() + '"';
					} else {
						params[key2][key3][key] = $('#param_'+key).val();
					}
					*/
					params[key2][key3][key] = $('#param_'+makeId(key)).val();
				} else {
					var values = [];
					var checkboxes = $('input:checkbox', $('#param_'+makeId(key))).length;
					if (checkboxes > 0) {
						$.each($('input:checked', $('#param_'+makeId(key))), function(i, elem) {
							values.push($(elem).attr('parValue'));
						});
					} else {
						values = $('#param_'+makeId(key)).val();
					}
					params[key2][key3][key] = values;
				}
			});
		});
	});
	return valueToString(params);
}

/**
 * Send the request to get the available studies
 */
function renderAvailableStudies() {
	// some clean up
	$('#studyHelp').html('');
	$('#datasetHelp').html('');
	$('#libraryHelp').html('');
	$('#methodHelp').html('');
	$('#paramsHelpDiv').css('display', 'none');
	$('#paramsTitle').css('display', 'none');
	$('#paramsDiv').css('display', 'none');
	$('#resultDiv').css('display', 'none');
	$('#errorDiv').css('display', 'none');
	
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
	names.sort(compareIgnoreCase);
	loadDatasets(emptyValue);
	loadLibraries(emptyValue);
	loadMethods(emptyValue);
	loadSites(emptyValue, false);
	loadStudies(names);
}

/**
 * Send the request to get the available datasets
 */
function renderAvailableDatasets() {
	// send the request
	var study = getSelectedStudyName();
	$('#studyHelp').html(availableStudies[study]);
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
	var dataset = getSelectedDatasetName();
	$('#datasetHelp').html(availableDatasets[dataset]);
	if (dataset != null) {
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
	// send the request
	var lib = getSelectedLibraryName();
	$('#libraryHelp').html(availableLibraries[lib]);
	if (lib != null) {
		var url = HOME + '/query?action=getMethods&library=' + encodeSafeURIComponent(lib) +
			'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
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
	var func = getSelectedMethodName();
	var lib = getSelectedLibraryName();
	var dataset = getSelectedDatasetName();
	if (func != '') {
		var url = HOME + '/query?action=getParameters&method=' + encodeSafeURIComponent(func) + 
			'&library=' + encodeSafeURIComponent(lib) +
			'&dataset=' + encodeSafeURIComponent(dataset);
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
function postRenderAvailableParameters(data, textStatus, jqXHR, param) {
	parametersDescription = data['description'];
	parametersBody = data['params'];
	$('#paramsHelpDiv').css('display', '');
	$('#paramsDiv').css('display', '');
	var paramsTitle = $('#paramsTitle');
	paramsTitle.css('display', '');
	paramsTitle.html(getSelectedLibraryName() + '/' + getSelectedMethodName());
	var paramsDiv = $('#paramsDivContent');
	var paramsHelp = $('#paramsHelp');
	paramsDiv.html('');
	paramsHelp.html('');
	$.each(parametersBody, function(key2, res2) {
		var h2 = $('<h2>');
		paramsDiv.append(h2);
		var title = key2;
		if (key2 == 'logisticRegressionInput') {
			title = 'Logistic Regression Input';
		}
		h2.html(title);
		$.each(res2, function(key3, res3) {
			var h3 = $('<h3>');
			paramsDiv.append(h3);
			var title = key3;
			if (key3 == 'inputParameters') {
				title = 'Input Parameters';
			} else if (key3 == 'inputDescription') {
				title = 'Input Description';
			}
			h3.html(title);
			var stringDiv = null;
			var tbody = null;
			$.each(res3, function(key, res) {
				if (parametersDescription[key] != null) {
					paramsHelp.append(parametersDescription[key]).append($('<br/><br/>'));
				}
				if ($.type(res) == 'string') {
					var h4 = $('<h4>');
					paramsDiv.append(h4);
					var title = key;
					if (key == 'dependentVariableName') {
						title = 'Dependent Variable';
					} else if (key == 'independentVariableName') {
						title = 'Independent Variables';
					}
					h4.html(title);
					if (key == 'dependentVariableName') {
						var dependentVariableName = $('<select>');
						dependentVariableName.attr('id', 'param_'+makeId(key));
						paramsDiv.append(dependentVariableName);
						var option = $('<option>');
						option.text(res);
						option.attr('value', res);
						dependentVariableName.append(option);
					} else {
						var checkboxDiv = $('<div>');
						//checkboxDiv.attr({'id': 'param_'+key});
						paramsDiv.append(checkboxDiv);
						var div = $('<div>');
						checkboxDiv.append(div);
						var input = $('<input>');
						input.attr({'type': 'checkbox',
							'checked': 'checked',
							'parName': res,
							'parValue': res});
						div.append(input);
						var label = $('<label>');
						label.attr({'id': 'param_'+makeId(key)});
						div.append(label);
						label.html(res);
					}
				} else if (res.length == 1 && res[0] == 'string') {
					if (stringDiv == null) {
						stringDiv = $('<div>');
						paramsDiv.append(stringDiv);
						var table = $('<table>');
						stringDiv.append(table);
						tbody = $('<tbody>');
						table.append(tbody);
					}
					var tr = $('<tr>');
					tbody.append(tr);
					var td = $('<td>');
					tr.append(td);
					td.html(key + ':');
					var td = $('<td>');
					tr.append(td);
					var input = $('<input>');
					input.attr({'type': 'text',
						'parName': key,
						'id': 'param_'+makeId(key)});
					if (key == 'id' && key3 == 'inputDescription') {
						input.val(++descriptionId);
						input.attr('disabled', 'disabled');
					}
					td.append(input);
				} else {
					var h4 = $('<h4>');
					paramsDiv.append(h4);
					var title = key;
					if (key == 'dependentVariableName') {
						title = 'Dependent Variable';
					} else if (key == 'independentVariableName') {
						title = 'Independent Variables';
					}
					h4.html(title);
					if (key == 'dependentVariableName') {
						var dependentVariableName = $('<select>');
						dependentVariableName.change(function(event) {disableIndependentVariableName();});
						dependentVariableName.attr('id', 'param_'+makeId(key));
						paramsDiv.append(dependentVariableName);
						res.sort(compareIgnoreCase);
						$.each(res, function(j, val) {
							var option = $('<option>');
							var arr = val.split('<br/>');
							option.text(arr[0]);
							option.attr('value', arr[0]);
							var description = '';
							if (arr.length > 1) {
								description = arr[1];
							}
							option.attr('description', description);
							dependentVariableName.append(option);
						});
					} else if (key == 'independentVariableName') {
						var checkboxDiv = $('<div>');
						checkboxDiv.attr({'id': 'param_'+makeId(key)});
						paramsDiv.append(checkboxDiv);
						res.sort(compareIgnoreCase);
						$.each(res, function(j, val) {
							var div = $('<div>');
							checkboxDiv.append(div);
							var arr = val.split('<br/>');
							var input = $('<input>');
							input.attr({'type': 'checkbox',
								'checked': 'checked',
								'parName': arr[0],
								'parValue': arr[0],
								'id': 'param_' + makeId(arr[0])});
							div.append(input);
							var label = $('<label>');
							div.append(label);
							label.html(arr[0]);
							var description = '';
							if (arr.length > 1) {
								description = arr[1];
							}
							label.hover(
									function(event) {DisplayTipBox(event, description);}, 
									function(){HideTipBox();});
						});
					}
				}
			});
		});
	});
	disableIndependentVariableName();
}

/**
 * Send the request to get the available sites
 */
function renderAvailableSites() {
	$('#methodHelp').html(availableMethods[getSelectedMethodName()]);
	var url = HOME + '/query?action=getSites' +
			'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
			'&dataset=' + encodeSafeURIComponent(getSelectedDatasetName()) +
			'&library=' + encodeSafeURIComponent(getSelectedLibraryName()) +
			'&method=' + encodeSafeURIComponent(getSelectedMethodName());
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
	loadSites(names, true);
}

/**
 * Send the request to get the data from the SCANNER
 */
function submitQuery(div, replay) {
	var obj = {};
	var param = {};
	if (replay == null) {
		param.spinner = $('#ajaxSpinnerImage');
		param.treeId = 'navigation';
		param.tableId = 'queryExample';
		param.errorTableId = 'errorQueryExample';
	} else {
		param.spinner = $('#ajaxHistorySpinnerImage');
		param.treeId = 'historyNavigation';
		param.tableId = 'historyExample';
		param.errorTableId = 'errorHistoryExample';
	}
	obj['action'] = 'getResults';
	if (replay == null) {
		var sites = getSelectedSitesNames();
		if (sites.length == 0) {
			alert('Please select the sites.');
			return;
		}
		var params = getSelectedParameters();
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
		param['history'] = obj;
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
	param['errorDiv'] = $('#' + (div == 'resultDivContent' ? 'errorDivContent' : 'errorReplayDivContent'));
	param['resultDiv'].parent().hide();
	param['errorDiv'].parent().hide();
	param['library'] = obj['library'];
	param.spinner.show();
	var url = HOME + '/query';
	scanner.POST(url, obj, true, postSubmitQuery, param, null, 0);
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
	var resultDiv = param['resultDiv'];
	var errorDiv = param['errorDiv'];
	data = $.parseJSON(data);
	var obj = param['history'];
	if (obj != null) {
		obj['trxId'] = data['trxId'];
		pushHistory(obj);
	}
	data = data['data'];
	buildDataTable(data, resultDiv, param.tableId);
	buildErrorDataTable(data, errorDiv, param.errorTableId);
}

/**
 * Create a study entry
 * 
 * @param name
 * 	the name of the study
 */
function createStudy(name) {
	if (name == null) {
		alert('Please provide a name for the study.');
		return;
	}
	var url = HOME + '/registry?action=createStudy' +
				'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a dataset entry
 * 
 * @param name
 * 	the name of the dataset
 * @param study
 * 	the study of the dataset
 */
function createDataset(name, study) {
	if (name == null) {
		alert('Please provide a name for the dataset.');
		return;
	}
	if (study == null) {
		alert('Please provide a study for the dataset.');
		return;
	}
	var url = HOME + '/registry?action=createDataset' +
			'&study=' + encodeSafeURIComponent(study) +
			'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a library entry
 * 
 * @param name
 * 	the name of the library
 * @param rpath
 * 	the URL path of the library
 */
function createLibrary(name, rpath) {
	if (name == null) {
		alert('Please provide a name for the library.');
		return;
	}
	if (rpath == null) {
		alert('Please provide a URL path for the library.');
		return;
	}
	var url = HOME + '/registry?action=createLibrary' +
			'&rpath=' + encodeSafeURIComponent(rpath) +
			'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a method entry
 * 
 * @param name
 * 	the name of the method
 * @param rpath
 * 	the URL path of the method
 * @param lib
 * 	the library of the method
 */
function createMethod(name, lib, rpath) {
	if (name == null) {
		alert('Please provide a name for the method.');
		return;
	}
	if (lib == null) {
		alert('Please provide a library for the method.');
		return;
	}
	if (rpath == null) {
		alert('Please provide a URL path for the method.');
		return;
	}
	var url = HOME + '/registry?action=createMethod' +
					'&rpath=' + encodeSafeURIComponent(rpath) +
					'&cname=' + encodeSafeURIComponent(name) +
					'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a master entry
 * 
 * @param rURL
 * 	the URL of the master
 */
function createMaster(rURL) {
	if (rURL == null) {
		alert('Please provide a URL for the master node.');
		return;
	}
	var url = HOME + '/registry?action=createMaster&rURL=' + encodeSafeURIComponent(rURL);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a site entry
 * 
 * @param name
 * 	the name of the site
 * @param rURL
 * 	the URL of the site
 */
function createSite(name, rURL) {
	if (name == null) {
		alert('Please provide a name for the site.');
		return;
	}
	if (rURL == null) {
		alert('Please provide a URL for the site.');
		return;
	}
	var url = HOME + '/registry?action=createSite' +
			'&rURL=' + encodeSafeURIComponent(rURL) +
			'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a worker entry
 * 
 * @param study
 * 	the study of the worker
 * @param dataset
 * 	the dataset of the worker
 * @param lib
 * 	the library of the worker
 * @param func
 * 	the method of the worker
 * @param site
 * 	the site of the worker
 * @param datasource
 * 	the data source of the worker
 * @param rURL
 * 	the URL of the worker
 */
function createWorker(study, dataset, lib, func, site, datasource, rURL) {
	if (study == null) {
		alert('Please provide a study for the worker.');
		return;
	}
	if (dataset == null) {
		alert('Please provide a dataset for the worker.');
		return;
	}
	if (lib == null) {
		alert('Please provide a library for the worker.');
		return;
	}
	if (func == null) {
		alert('Please provide a method for the worker.');
		return;
	}
	if (site == null) {
		alert('Please provide a site for the worker.');
		return;
	}
	if (datasource == null) {
		alert('Please provide a data source for the worker.');
		return;
	}
	if (rURL == null) {
		alert('Please provide a URL for the worker.');
		return;
	}
	var url = HOME + '/registry?action=createWorker' +
					'&study=' + encodeSafeURIComponent(study) +
					'&dataset=' + encodeSafeURIComponent(dataset) +
					'&library=' + encodeSafeURIComponent(lib) +
					'&method=' + encodeSafeURIComponent(func) +
					'&site=' + encodeSafeURIComponent(site) +
					'&datasource=' + encodeSafeURIComponent(datasource) +
					'&rURL=' + encodeSafeURIComponent(rURL);
					
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Create a parameter entry
 * 
 * @param name
 * 	the name of the parameter
 * @param func
 * 	the method of the parameter
 * @param lib
 * 	the library of the parameter
 * @param minOccurs
 * 	the minOccurs of the parameter (default is 1)
 * @param maxOccurs
 * 	the maxOccurs of the parameter (default is 1)
 * @param values
 * 	the values of the parameter
 */
function createParameter(name, func, lib, minOccurs, maxOccurs, values) {
	if (name == null) {
		alert('Please provide a name for the parameter.');
		return;
	}
	if (func == null) {
		alert('Please provide a method for the parameter.');
		return;
	}
	if (lib == null) {
		alert('Please provide a library for the parameter.');
		return;
	}
	if (minOccurs == null) {
		minOccurs = 1;
	}
	if (maxOccurs == null) {
		maxOccurs = 1;
	}
	var url = HOME + '/registry?action=createParameter' +
					'&cname=' + encodeSafeURIComponent(name) +
					'&method=' + encodeSafeURIComponent(func) +
					'&library=' + encodeSafeURIComponent(lib) +
					'&minOccurs=' + minOccurs +
					'&maxOccurs=' + maxOccurs;
	if (values != null && values.length > 0) {
		url += '&values=';
		var values = arrayToString(values);
		url += encodeSafeURIComponent(values);
	}
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a worker entry
 * 
 * @param study
 * 	the study of the worker
 * @param dataset
 * 	the dataset of the worker
 * @param lib
 * 	the library of the worker
 * @param func
 * 	the method of the worker
 * @param site
 * 	the site of the worker
 */
function deleteWorker(study, dataset, lib, func, site) {
	if (study == null) {
		alert('Please provide a study for the worker.');
		return;
	}
	if (dataset == null) {
		alert('Please provide a dataset for the worker.');
		return;
	}
	if (lib == null) {
		alert('Please provide a library for the worker.');
		return;
	}
	if (func == null) {
		alert('Please provide a method for the worker.');
		return;
	}
	if (site == null) {
		alert('Please provide a site for the worker.');
		return;
	}
	var url = HOME + '/registry?action=deleteWorker' +
		'&study=' + encodeSafeURIComponent(study) +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&library=' + encodeSafeURIComponent(lib) +
		'&method=' + encodeSafeURIComponent(func) +
		'&site=' + encodeSafeURIComponent(site);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a parameter entry
 * 
 * @param name
 * 	the name of the parameter
 * @param func
 * 	the method of the parameter
 * @param lib
 * 	the library of the parameter
 */
function deleteParameter(name, func, lib) {
	if (name == null) {
		alert('Please provide a name for the parameter.');
		return;
	}
	if (func == null) {
		alert('Please provide a method for the parameter.');
		return;
	}
	if (lib == null) {
		alert('Please provide a library for the parameter.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteParameter' +
		'&library=' + encodeSafeURIComponent(lib) +
		'&method=' + encodeSafeURIComponent(func);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a master entry
 * 
 */
function deleteMaster() {
	var url = HOME + '/registry?action=deleteMaster';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a method entry
 * 
 * @param name
 * 	the name of the method
 * @param lib
 * 	the library of the method
 */
function deleteMethod(name, lib) {
	if (name == null) {
		alert('Please provide a name for the method.');
		return;
	}
	if (lib == null) {
		alert('Please provide a library for the method.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteMethod' +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a library entry
 * 
 * @param name
 * 	the name of the library
 */
function deleteLibrary(name) {
	if (name == null) {
		alert('Please provide a name for the library.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteLibrary';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a site entry
 * 
 * @param name
 * 	the name of the site
 */
function deleteSite(name) {
	if (name == null) {
		alert('Please provide a name for the site.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteSite';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a dataset entry
 * 
 * @param name
 * 	the name of the dataset
 * @param study
 * 	the study of the dataset
 */
function deleteDataset(name, study) {
	if (name == null) {
		alert('Please provide a name for the dataset.');
		return;
	}
	if (study == null) {
		alert('Please provide a study for the dataset.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteDataset' +
		'&study=' + encodeSafeURIComponent(study);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Delete a study entry
 * 
 * @param name
 * 	the name of the study
 */
function deleteStudy(name) {
	if (name == null) {
		alert('Please provide a name for the study.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteStudy';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Update a library entry
 * 
 * @param id
 * 	the id of the library
 * @param name
 * 	the name of the library
 * @param rpath
 * 	the URL path of the library
 */
function updateLibrary(id, name, rpath) {
	if (id == null) {
		alert('Please provide the library id.');
		return;
	}
	var url = HOME + '/registry?action=updateLibrary&id=' + encodeSafeURIComponent(id);
	if (name != null) {
		url += '&cname=' + encodeSafeURIComponent(name);
	}
	if (rpath != null) {
		url += '&rpath=' + encodeSafeURIComponent(rpath);
	}
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Update a method entry
 * 
 * @param id
 * 	the id of the method
 * @param name
 * 	the name of the method
 * @param lib
 * 	the library of the method
 * @param rpath
 * 	the URL path of the method
 */
function updateMethod(id, name, lib, rpath) {
	if (id == null) {
		alert('Please provide the method id.');
		return;
	}
	var url = HOME + '/registry?action=updateMethod&id=' + encodeSafeURIComponent(id);
	if (name != null) {
		url += '&cname=' + encodeSafeURIComponent(name);
	}
	if (rpath != null) {
		url += '&rpath=' + encodeSafeURIComponent(rpath);
	}
	if (lib != null) {
		url += '&library=' + encodeSafeURIComponent(lib);
	}
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Update a master entry
 * 
 * @param rURL
 * 	the URL of the master
 */
function updateMaster(rURL) {
	if (rURL == null) {
		alert('Please provide the master URL.');
		return;
	}
	var url = HOME + '/registry?rURL=' + encodeSafeURIComponent(rURL) + '&action=updateMaster';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Update a site entry
 * 
 * @param id
 * 	the id of the site
 * @param name
 * 	the name of the site
 * @param rpath
 * 	the URL path of the site
 */
function updateSite(id, name, rURL) {
	if (id == null) {
		alert('Please provide the site id.');
		return;
	}
	var url = HOME + '/registry?action=updateSite&id=' + encodeSafeURIComponent(id);
	if (name != null) {
		url += '&cname=' + encodeSafeURIComponent(name);
	}
	if (rURL != null) {
		url += '&rURL=' + encodeSafeURIComponent(rURL);
	}
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Update a worker entry
 * 
 * @param id
 * 	the id of the worker
 * @param study
 * 	the study of the worker
 * @param dataset
 * 	the dataset of the worker
 * @param lib
 * 	the library of the worker
 * @param func
 * 	the method of the worker
 * @param site
 * 	the site of the worker
 * @param datasource
 * 	the data source of the worker
 * @param rURL
 * 	the URL of the worker
 */
function updateWorker(id, study, dataset, lib, func, site, datasource, rURL) {
	if (id == null) {
		alert('Please provide the worker id.');
		return;
	}
	var url = HOME + '/registry?action=updateWorker&id=' + encodeSafeURIComponent(id);
	if (study != null) {
		url += '&study=' + encodeSafeURIComponent(study);
	}
	if (dataset != null) {
		url += '&dataset=' + encodeSafeURIComponent(dataset);
	}
	if (lib != null) {
		url += '&library=' + encodeSafeURIComponent(lib);
	}
	if (func != null) {
		url += '&method=' + encodeSafeURIComponent(func);
	}
	if (site != null) {
		url += '&site=' + encodeSafeURIComponent(site);
	}
	if (datasource != null) {
		url += '&datasource=' + encodeSafeURIComponent(datasource);
	}
	if (rURL != null) {
		url += '&rURL=' + encodeSafeURIComponent(rURL);
	}
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Update a parameter entry
 * 
 * @param id
 * 	the id of the parameter
 * @param name
 * 	the name of the parameter
 * @param func
 * 	the method of the parameter
 * @param lib
 * 	the library of the parameter
 * @param minOccurs
 * 	the minOccurs of the parameter
 * @param maxOccurs
 * 	the maxOccurs of the parameter
 * @param values
 * 	the values of the parameter
 */
function updateParameter(id, name, func, lib, minOccurs, maxOccurs, values) {
	if (id == null) {
		alert('Please provide the parameter id.');
		return;
	}
	var url = HOME + '/registry?action=updateParameter&id=' + encodeSafeURIComponent(id);
	if (name != null) {
		url += '&cname=' + encodeSafeURIComponent(name);
	}
	if (func != null) {
		url += '&method=' + encodeSafeURIComponent(func);
	}
	if (lib != null) {
		url += '&library=' + encodeSafeURIComponent(lib);
	}
	if (minOccurs != null) {
		url += '&minOccurs=' + minOccurs;
	}
	if (maxOccurs != null) {
		url += '&maxOccurs=' + maxOccurs;
	}
	if (values != null && values.length > 0) {
		url += '&values=';
		var values = arrayToString(values);
		url += encodeSafeURIComponent(values);
	}
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

/**
 * Get a study entry
 * 
 * @param name
 * 	the name of the study
 */
function getStudy(name) {
	if (name == null) {
		alert('Please provide the name of the study.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getStudy';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get a dataset entry
 * 
 * @param name
 * 	the name of the dataset
 * @param study
 * 	the study of the dataset
 */
function getDataset(name, study) {
	if (name == null) {
		alert('Please provide the name of the dataset.');
		return;
	}
	if (study == null) {
		alert('Please provide the study of the dataset.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getDataset' +
		'&study=' + encodeSafeURIComponent(study);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get a library entry
 * 
 * @param name
 * 	the name of the dataset
 */
function getLibrary(name) {
	if (name == null) {
		alert('Please provide the name of the library.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getLibrary';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get a method entry
 * 
 * @param name
 * 	the name of the method
 * @param lib
 * 	the library of the method
 */
function getMethod(name, lib) {
	if (name == null) {
		alert('Please provide the name of the method.');
		return;
	}
	if (lib == null) {
		alert('Please provide the library of the method.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getMethod' +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get the master entry
 */
function getMaster() {
	var url = HOME + '/registry?action=getMaster';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get a parameter entry
 * 
 * @param name
 * 	the name of the parameter
 * @param func
 * 	the method of the parameter
 * @param lib
 * 	the library of the parameter
 */
function getParameter(name, func, lib) {
	if (name == null) {
		alert('Please provide the name of the parameter.');
		return;
	}
	if (func == null) {
		alert('Please provide the method of the parameter.');
		return;
	}
	if (lib == null) {
		alert('Please provide the library of the parameter.');
		return;
	}
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getParameter' +
		'&method=' + encodeSafeURIComponent(func) +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get a worker entry
 * 
 * @param study
 * 	the study of the worker
 * @param dataset
 * 	the dataset of the worker
 * @param lib
 * 	the library of the worker
 * @param func
 * 	the method of the worker
 * @param site
 * 	the site of the worker
 */
function getWorker(study, dataset, lib, func, site) {
	if (study == null) {
		alert('Please provide the study of the worker.');
		return;
	}
	if (dataset == null) {
		alert('Please provide the dataset of the worker.');
		return;
	}
	if (lib == null) {
		alert('Please provide the library of the worker.');
		return;
	}
	if (func == null) {
		alert('Please provide the method of the worker.');
		return;
	}
	if (site == null) {
		alert('Please provide the site of the worker.');
		return;
	}
	var url = HOME + '/registry?study=' + encodeSafeURIComponent(study) + 
		'&action=getWorker' +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&method=' + encodeSafeURIComponent(func) +
		'&site=' + encodeSafeURIComponent(site) +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the studies
 * 
 */
function getStudies() {
	var url = HOME + '/registry?action=getStudies';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the datasets of a study 
 * 
 * @param study
 * 	the study of the dataset
 */
function getDatasets(study) {
	if (study == null) {
		alert('Please provide the study of the datasets.');
		return;
	}
	var url = HOME + '/registry?action=getDatasets' +
		'&study=' + encodeSafeURIComponent(study);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the libraries 
 * 
 */
function getLibraries() {
	var url = HOME + '/registry?action=getLibraries';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the methods of a library 
 * 
 * @param lib
 * 	the library of the methods
 */
function getMethods(lib) {
	if (lib == null) {
		alert('Please provide the library of the methods.');
		return;
	}
	var url = HOME + '/registry?action=getMethods' +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the parameters of a method 
 * 
 * @param func
 * 	the method of the parameters
 * @param lib
 * 	the library of the parameters
 */
function getParameters(func, lib) {
	if (func == null) {
		alert('Please provide the method of the parameters.');
		return;
	}
	if (lib == null) {
		alert('Please provide the library of the parameters.');
		return;
	}
	var url = HOME + '/registry?action=getParameters' +
		'&method=' + encodeSafeURIComponent(func) +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the sites of a query 
 * 
 * @param study
 * 	the study of the query
 * @param dataset
 * 	the dataset of the query
 * @param func
 * 	the method of the query
 * @param lib
 * 	the library of the query
 */
function getSites(study, dataset, lib, func) {
	if (study == null) {
		alert('Please provide the study of the sites.');
		return;
	}
	if (dataset == null) {
		alert('Please provide the dataset of the sites.');
		return;
	}
	if (lib == null) {
		alert('Please provide the library of the sites.');
		return;
	}
	if (func == null) {
		alert('Please provide the method of the sites.');
		return;
	}
	var url = HOME + '/registry?action=getSites' +
		'&study=' + encodeSafeURIComponent(study) +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&library=' + encodeSafeURIComponent(lib) +
		'&method=' + encodeSafeURIComponent(func);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the workers of a query 
 * 
 * @param study
 * 	the study of the query
 * @param dataset
 * 	the dataset of the query
 * @param func
 * 	the method of the query
 * @param lib
 * 	the library of the query
 * @param sites
 * 	the sites of the query
 */
function getWorkers(study, dataset, lib, func, sites) {
	if (study == null) {
		alert('Please provide the study of the workers.');
		return;
	}
	if (dataset == null) {
		alert('Please provide the dataset of the workers.');
		return;
	}
	if (lib == null) {
		alert('Please provide the library of the workers.');
		return;
	}
	if (func == null) {
		alert('Please provide the method of the workers.');
		return;
	}
	var url = HOME + '/registry?study=' + encodeSafeURIComponent(study) + 
		'&action=getWorkers' +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&method=' + encodeSafeURIComponent(func) +
		'&library=' + encodeSafeURIComponent(lib);
	if (sites != null && sites.length > 0) {
		url += '&sites=';
		var values = arrayToString(sites);
		url += encodeSafeURIComponent(values);
	}
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the libraries of a site
 * 
 */
function getNodeLibraries(site) {
	if (site == null) {
		alert('Please provide a site name.');
		return;
	}
	var url = HOME + '/registry?action=getNodeLibraries' +
		'&site=' + encodeSafeURIComponent(site);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the datasets of a site 
 * 
 */
function getNodeExtracts(site) {
	if (site == null) {
		alert('Please provide a site name.');
		return;
	}
	var url = HOME + '/registry?action=getNodeExtracts' +
		'&site=' + encodeSafeURIComponent(site);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Get all the sites of a dataset 
 * 
 */
function getDatasetSites(study, dataset) {
	if (study == null) {
		alert('Please provide the study of the sites.');
		return;
	}
	if (dataset == null) {
		alert('Please provide the dataset of the sites.');
		return;
	}
	var url = HOME + '/registry?action=getDatasetSites' +
		'&study=' + encodeSafeURIComponent(study) +
		'&dataset=' + encodeSafeURIComponent(dataset);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

/**
 * Function called after a GUI POST request was issued 
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 */
function postResourceAction(data, textStatus, jqXHR, param) {
	document.body.style.cursor = "default";
	var res = $.parseJSON(data);
	alert(res.status);
}

/**
 * Function called after a GUI GET request was issued 
 * 
 * @param data
 * 	the data returned from the server (a JSONObject having as keys the display names)
 * @param textStatus
 * 	the string describing the status
 * @param jqXHR
 * 	the jQuery XMLHttpRequest
 * @param param
 * 	the parameters to be used by the callback success function
 */
function postGetResourceAction(data, textStatus, jqXHR, param) {
	document.body.style.cursor = "default";
	alert(valueToString(data));
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
				if (columns.lengthlength > 0) {
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
	if ($.isPlainObject(res)) {
		if (res['Output'] != null) {
			var coefficient = null;
			var siteInfo = null;
			if ($.isArray(res['Output'])) {
				$.each(res['Output'], function(i, output) {
					coefficient = output['Coefficient'];
					siteInfo = output['SiteInfo'];
					var group = [];
					$.each(coefficient, function(i, obj) {
						var row = [];
						$.each(columns, function(j, col) {
							row.push(obj[col]);
						});
						group.push(row);
					});
					rows.push(group);
					var site = [];
					if (siteInfo != null) {
						site.push(siteInfo);
					}
					sites.push(site);
				});
			} else {
				coefficient = res['Output']['Coefficient'];
				var group = [];
				siteInfo = res['Output']['SiteInfo'];
				$.each(coefficient, function(i, obj) {
					var row = [];
					$.each(columns, function(j, col) {
						row.push(obj[col]);
					});
					group.push(row);
				});
				rows.push(group);
				var site = [];
				if (siteInfo != null) {
					site.push(siteInfo);
				}
				sites.push(site);
			}
		} else {
			$.each(res, function(key, value) {
				getRowsValues(value, columns, rows, sites);
			});
		}
	} else if ($.isArray(res)) {
		$.each(res, function(i, val) {
			getRowsValues(val, columns, rows, sites);
		});
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
function getRowsErrors(res, columns, rows, sites) {
	if ($.isPlainObject(res)) {
		if (res['Error'] != null) {
			var siteInfo = null;
			if ($.isArray(res['Error'])) {
				var arr = res['Error'];
				$.each(arr, function(i, obj) {
					siteInfo = obj['SiteInfo'];
					var row = [];
					$.each(columns, function(j, col) {
						row.push(obj[col] == null ? '' : obj[col]);
					});
					rows.push(row);
					var site = [];
					if (siteInfo != null) {
						site.push(siteInfo);
					}
					sites.push(site);
				});
				
			} else if ($.isPlainObject(res['Error'])) {
				var obj = res['Error'];
				siteInfo = obj['SiteInfo'];
				var row = [];
				$.each(columns, function(j, col) {
					row.push(obj[col] == null ? '' : obj[col]);
				});
				rows.push(row);
				var site = [];
				if (siteInfo != null) {
					site.push(siteInfo);
				}
				sites.push(site);
			}
		} else {
			$.each(res, function(key, value) {
				getRowsErrors(value, columns, rows, sites);
			});
		}
	} else if ($.isArray(res)) {
		$.each(res, function(i, val) {
			getRowsErrors(val, columns, rows, sites);
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

function buildDataTable(res, resultDiv, tableId) {
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
		if (oHistoryTable != null) {
			$('#' + tableId).remove();
			oHistoryTable = null;
		}
	}
	resultDiv.parent().css('display', '');
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
				var site = sites[siteNo++];
				if (site.length > 0) {
					var value = '';
					var siteInfo = site[0];
					if (siteInfo['SiteName'] != null) {
						value = 'Site Name: ' + siteInfo['SiteName'];
					}
					if (siteInfo['SiteDescription'] != null) {
						if (value.length > 0) {
							value += '<br/>';
						}
						value += 'Site Description: ' + siteInfo['SiteDescription'];
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
		oHistoryTable = oTable;
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

function buildErrorDataTable(res, resultDiv, tableId) {
	var columns = ['ErrorSource', 'ErrorType', 'ErrorCode', 'ErrorDescription'];
	var rows = [];
	var sites = [];
	getRowsErrors(res, columns, rows, sites);
	if (rows.length == 0) {
		return;
	}
	if (tableId == 'errorQueryExample') {
		if (oErrorQueryTable != null) {
			$('#' + tableId).remove();
			oErrorQueryTable = null;
		}
	} else {
		if (oErrorHistoryTable != null) {
			$('#' + tableId).remove();
			oErrorHistoryTable = null;
		}
	}
	resultDiv.parent().css('display', '');
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
	$.each(rows, function(i, row) {
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
		tr = $('<tr>');
		tbody.append(tr);
		var td = $('<td>');
		tr.append(td);
		td.html(''+groupNo);
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
				var site = sites[siteNo++];
				if (site.length > 0) {
					var value = '';
					var siteInfo = site[0];
					if (siteInfo['SiteName'] != null) {
						value = 'Site Name: ' + siteInfo['SiteName'];
					}
					if (siteInfo['SiteDescription'] != null) {
						if (value.length > 0) {
							value += '<br/>';
						}
						value += 'Site Description: ' + siteInfo['SiteDescription'];
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
        'sDom': 'lfr<"giveHeight"t>ip'
	});
	if (tableId == 'errorQueryExample') {
		oErrorQueryTable = oTable;
	} else {
		oErrorHistoryTable = oTable;
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
		var alertMessage = jqXHR.responseText;
		var index1 = jqXHR.responseText.indexOf('<body>');
		if (index1 >= 0) {
			var index2 = jqXHR.responseText.indexOf('</body>');
			if (index2 >= 0) {
				alertMessage = alertMessage.substring(index1+'<body>'.length, index2);
			}
		}
		$('#errorMessage').html(alertMessage);
		alertErrorDialog.dialog('open');
		$('.ui-widget-overlay').css('opacity', 1.0);
		//alert(msg);
	} else {
		var delay = Math.round(Math.ceil((0.75 + Math.random() * 0.5) * Math.pow(10, count) * 0.00001));
		setTimeout(function(){retryCallback(url, obj, async, successCallback, param, errorCallback, count+1);}, delay);
	}
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
	var user = $('#username').val();
	var password = $('#password').val();
	var url = HOME + '/login';
	var obj = new Object();
	obj['username'] = user;
	obj['password'] = password;
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
		$('#loginForm').css('display', 'none');
		$('#ui').css('visibility', 'visible');
		$('#ui').css('display', '');
		setContacts(res['contacts']);
		renderAvailableStudies();
	} else {
		alert(res['status']);
	}
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
				$('#studyHelp').html('');
				$('#datasetHelp').html('');
				$('#libraryHelp').html('');
				$('#methodHelp').html('');
				$('#paramsHelpDiv').css('display', 'none');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#resultDiv').css('display', 'none');
				$('#errorDiv').css('display', 'none');
				var selValues = studiesMultiSelect.get('value');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableDatasets();
					} else if (selValues.length > 1) {
						studiesMultiSelect.set('value', '');
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
				$('#paramsHelpDiv').css('display', 'none');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#resultDiv').css('display', 'none');
				$('#errorDiv').css('display', 'none');
				var selValues = datasetsMultiSelect.get('value');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableLibraries();
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
				loadSites(emptyValue, false);
				$('#libraryHelp').html('');
				$('#methodHelp').html('');
				$('#paramsHelpDiv').css('display', 'none');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#resultDiv').css('display', 'none');
				$('#errorDiv').css('display', 'none');
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
				loadSites(emptyValue, false);
				var selValues = methodsMultiSelect.get('value');
				$('#methodHelp').html('');
				$('#paramsHelpDiv').css('display', 'none');
				$('#paramsTitle').css('display', 'none');
				$('#paramsDiv').css('display', 'none');
				$('#resultDiv').css('display', 'none');
				$('#errorDiv').css('display', 'none');
				if (selValues != null) { 
					if (selValues.length == 1) {
						renderAvailableSites();
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
	$('#resultWrapperDiv').show();
	$('#errorWrapperDiv').show();
	$('#replayDivContent').html('');
	$('#replayDivWrapper').hide();
	$('#errorReplayWrapperDiv').hide();
}

/**
 * Actions to be performed when the "Query" tab is deselected
 */
function hideQuery() {
	$('#paramsWrapperDiv').hide();
	$('#resultWrapperDiv').hide();
	$('#errorWrapperDiv').hide();
	$('#replayDivWrapper').show();
	$('#errorReplayWrapperDiv').show();
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
 * Push an entry in the history table
 * 
 * @param obj
 * 	the object with the columns values
 */
function pushHistory(obj) {
	require(['dojox/grid/DataGrid', 'dojo/data/ItemFileWriteStore'], function(DataGrid, ItemFileWriteStore) {
		var index = historyDataStore.items.length;
		if (historyGrid != null) {
			historyGrid.destroyRecursive();
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
		item.options = '<a href="javascript:replayQuery(' + index + ')" >See results</a>';
		item.budget = 'xxxxx';
		item.id = index;
		historyDataStore.items.unshift(item);
		var store = new ItemFileWriteStore({data: historyDataStore});
		historyGrid = new DataGrid({
			id: 'historyGrid',
			store: store,
			structure: historyLayout,
			escapeHTMLInData: false,
			rowSelector: '20px'});

		/*append the new grid to the div*/
		historyGrid.placeAt("historyGridDiv");

		/*Call startup() to render the grid*/
		historyGrid.startup();
	});
}

/**
 * Function to be called when a query is replayed
 * 
 * @param index
 * 	the row index in the history table
 */
function replayQuery(index) {
	var tableIndex = historyDataStore.items.length - index - 1;
	var item = historyDataStore.items[tableIndex];
	submitQuery('replayDivContent', item);
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
		$('#continueButton').removeAttr('disabled');
	} else {
		$('#continueButton').attr('disabled', 'disabled');
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
