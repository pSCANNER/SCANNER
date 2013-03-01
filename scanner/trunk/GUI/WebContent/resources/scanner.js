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

var oTable = null;

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

var emptyValue = ['&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'];

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
	var params = {};
	$.each($('input:checked', $('#paramsDivContent')), function(i, elem) {
		var param = $(elem);
		var name = param.attr('parName');
		var value = param.attr('parValue');
		var minOccurs = param.attr('minOccurs');
		var maxOccurs = param.attr('maxOccurs');
		if (minOccurs == 1 && maxOccurs == 1) {
			params[name] = value;
		} else {
			if (params[name] == null) {
				params[name] = [];
			}
			params[name].push(value);
		}
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
		var url = HOME + '/query?action=getLibraries';
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
		var url = HOME + '/query?action=getMethods&library=' + encodeSafeURIComponent(lib);
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
	if (func != '') {
		var url = HOME + '/query?action=getParameters&method=' + encodeSafeURIComponent(func) + '&library=' + encodeSafeURIComponent(lib);
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
	$('#paramsHelpDiv').css('display', '');
	$('#paramsDiv').css('display', '');
	var paramsTitle = $('#paramsTitle');
	paramsTitle.css('display', '');
	paramsTitle.html(getSelectedLibraryName() + '/' + getSelectedMethodName());
	var paramsDiv = $('#paramsDivContent');
	var paramsHelp = $('#paramsHelp');
	paramsDiv.html('');
	paramsHelp.html('');
	$.each(data, function(i, param) {
		$.each(param, function(key, res) {
			var h2 = $('<h2>');
			paramsDiv.append(h2);
			h2.html(res['cname']);
			paramsHelp.append($('<br/><br/>')).append(res['description']);
			var value = res['values'];
			var minOccurs = res['minOccurs'];
			var maxOccurs = res['maxOccurs'];
			$.each(value, function(j, val) {
				var div = $('<div>');
				paramsDiv.append(div);
				var input = $('<input>');
				input.attr({'type': 'checkbox',
					'checked': 'checked',
					'parName': res['cname'],
					'parValue': val,
					'minOccurs': minOccurs,
					'maxOccurs': maxOccurs});
				if (minOccurs == 1 && maxOccurs == 1) {
					input.attr('disabled', 'disabled');
				}
				div.append(input);
				var label = $('<label>');
				div.append(label);
				label.html(val);
			});
		});
	});
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
function submitQuery() {
	var sites = getSelectedSitesNames();
	if (sites.length == 0) {
		alert('Please select the sites.');
		return;
	}
	var params = getSelectedParameters();
	var obj = {};
	obj['parameters'] = params;
	obj['action'] = 'getResults';
	obj['study'] = getSelectedStudyName();
	obj['dataset'] = getSelectedDatasetName();
	obj['library'] = getSelectedLibraryName();
	obj['method'] = getSelectedMethodName();
	obj['sites'] = valueToString(sites);
	var url = HOME + '/query';
	$('#ajaxSpinnerImage').show();
	scanner.POST(url, obj, true, postSubmitQuery, null, null, 0);
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
	$('#ajaxSpinnerImage').hide();
	data = $.parseJSON(data);
	if (getSelectedLibraryName() == 'Oceans') {
		buildDataTable(data);
	} else {
		buildTreeResult(data);
	}
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

function postResourceAction(data, textStatus, jqXHR, param) {
	document.body.style.cursor = "default";
	var res = $.parseJSON(data);
	alert(res.status);
}

function postGetResourceAction(data, textStatus, jqXHR, param) {
	document.body.style.cursor = "default";
	alert(valueToString(data));
}

function getTableColumns(res) {
	var outerColumns = new Array();
	var columns = new Array();
	getTableColumnsFrom(res, columns, outerColumns);
	return {	'columns': columns,
			'group': outerColumns.join('_')
		};
}

function getTableColumnsFrom(res, columns, outerColumns) {
	if ($.isPlainObject(res)) {
		$.each(res, function(key, value) {
			if ($.isPlainObject(value) || $.isArray(value)) {
				outerColumns.push(key);
				getTableColumnsFrom(value, columns, outerColumns);
			} else {
				columns.push(key);
			}
		});
	} else if ($.isArray(res)) {
		getTableColumnsFrom(res[0], columns, outerColumns);
	}
}

function getTableColumnsValues(res) {
	var columnsValues = new Array();
	getTableColumnsValuesFrom(res, columnsValues);
	$.each(columnsValues, function(i, val) {
		if ($.isArray(val)) {
			$.each(val, function(j, value) {
				if ($.isArray(value)) {
					$.each(value, function(k, tr) {
					});
				}
			});
		}
	});
	return columnsValues;
}

function getTableColumnsValuesFrom(res, columnsValues) {
	if ($.isArray(res)) {
		$.each(res, function(i, values) {
			getTableColumnsValuesFrom(values, columnsValues);
		});
	} else if ($.isPlainObject(res)) {
		var arr = new Array();
		columnsValues.push(arr);
		$.each(res, function(key, value) {
			if ($.isPlainObject(value) || $.isArray(value)) {
				getTableColumnsValuesFrom(value, arr);
			} else {
				arr.push(value);
			}
		});
	}
}

function buildDataTable(res) {
	if (oTable != null) {
		//oTable.fnDestroy(true);
		$('#example').remove();
		oTable = null;
	}
	$('#resultDiv').css('display', '');
	var resultDiv = $('#resultDivContent');
	resultDiv.html('');
	var table = $('<table>');
	resultDiv.append(table);
	table.attr({	'cellpadding': '0',
			'cellspacing': '0',
			'border': '0',
			'id': 'example'}); 
	table.addClass('display');
	var thead = $('<thead>');
	table.append(thead);
	var tr = $('<tr>');
	thead.append(tr);
	var header = getTableColumns(res);
	var group = header['group'];
	var columns = header['columns'];
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
	var datasets = getTableColumnsValues(res)[0];
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
	oTable = $('#example').dataTable({
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
		     
		    var nTrs = $('#example tbody tr');
		    var iColspan = nTrs[0].getElementsByTagName('td').length;
		    var sLastGroup = "";
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
				td.html(group);
				tr.append(td);
		        }
		    }
		},
		"aoColumnDefs": [
		    { "bVisible": false, "aTargets": [ 0 ] }
		],
		"aaSortingFixed": [[ 0, 'asc' ]],
		"aaSorting": [[ 1, 'asc' ]],
        	"sDom": 'lfr<"giveHeight"t>ip'
	});
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
		alert(msg);
		document.body.style.cursor = "default";
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

function escapeDoubleQuotes(text) {
	return text.replace(/"/g, '\\"');
}

function encodeSafeURIComponent(value) {
	var ret = encodeURIComponent(value);
	$.each("~!()'", function(i, c) {
		ret = ret.replace(new RegExp('\\' + c, 'g'), escape(c));
	});
	return ret;
}
/**
 * Compares two strings lexicographically, ignoring case differences.
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

function expandAll() {
	$('#navigation').find('div.hitarea.expandable-hitarea').click();
	//$('#navigation').find('div.hitarea.tree-hitarea.expandable-hitarea').click();
}

function buildTreeResult(res) {
	$('#resultDiv').css('display', '');
	var resultDiv = $('#resultDivContent');
	resultDiv.html('');
	var h1 = $('<h1>');
	resultDiv.append(h1);
	h1.html('Response Results');
	var input = $('<input>');
	input.attr({'type': 'button',
		'value': 'Expand All'});
	input.val('Expand All');
	input.click(function(event) {expandAll();});
	resultDiv.append(input);
	resultDiv.append($('<br>'));
	resultDiv.append($('<br>'));
	var ul = $('<ul>');
	ul.attr({'id': 'navigation'});
	resultDiv.append(ul);
	appendTreeItem(ul, res);
	$('#navigation').treeview({
		persist: 'location',
		collapsed: true,
		unique: false
	});
}

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

function showQuery() {
	$('#queryHelpWrapperDiv').show();
	$('#paramsWrapperDiv').show();
	$('#resultWrapperDiv').show();
}

function hideQuery() {
	$('#queryHelpWrapperDiv').hide();
	$('#paramsWrapperDiv').hide();
	$('#resultWrapperDiv').hide();
}

