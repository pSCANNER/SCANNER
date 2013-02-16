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

function initScanner() {
	var val = '' + window.location;
	var len = val.length - 1;
	if (val[len] == '/') {
		val = val.substr(0, len);
	}
	HOME = val;
	renderLogin();
}

/**
 * @return the value of the selected study
 */
function getSelectedStudy() {
	return $('input:radio[name=studies]:checked').val();
}

/**
 * @return the value of the selected dataset
 */
function getSelectedDataset() {
	return $('input:radio[name=datasets]:checked').val();
}

/**
 * @return the value of the selected library
 */
function getSelectedLibrary() {
	return $('input:radio[name=libs]:checked').val();
}

/**
 * @return the value of the selected functions
 */
function getSelectedFunction() {
	return $('input:radio[name=funcs]:checked').val();
}

/**
 * @return the value of the selected site
 */
function getSelectedSites() {
	var sites = [];
	$.each($('input:checkbox[name=sites]:checked'), function(i, inp) {
		sites.push($(inp).val());
	});
	return sites;
}

/**
 * @return the value of the selected study
 */
function getSelectedStudyName() {
	return $('input:radio[name=studies]:checked').parent().next().html();
}

/**
 * @return the value of the selected dataset
 */
function getSelectedDatasetName() {
	return $('input:radio[name=datasets]:checked').parent().next().html();
}

/**
 * @return the value of the selected library
 */
function getSelectedLibraryName() {
	return $('input:radio[name=libs]:checked').parent().next().html();
}

/**
 * @return the value of the selected functions
 */
function getSelectedFunctionName() {
	return $('input:radio[name=funcs]:checked').parent().next().html();
}

/**
 * @return the value of the selected functions
 */
function getSelectedSitesNames() {
	var sites = [];
	$.each($('input:checkbox[name=sites]:checked'), function(i, inp) {
		sites.push($(inp).parent().next().html());
	});
	return sites;
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
	$.each($('input:checked', $('#paramsDiv')), function(i, elem) {
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
	$('#studyDiv').html('');
	$('#datasetDiv').html('');
	$('#libDiv').html('');
	$('#funcDiv').html('');
	$('#siteDiv').html('');
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();
	
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
	var div = $('#ui');
	div.html('');
	renderSelectTable();
	var studyDiv = $('#studyDiv');
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	var table = $('<table>');
	studyDiv.append(table);
	$.each(names, function(i, name) {
		var tr = $('<tr>');
		table.append(tr);
		var td = $('<td>');
		tr.append(td);
		var input = $('<input>');
		input.attr({
			'type': 'radio',
			'name': 'studies',
			'value': data[name]
		});
		input.click(function(event) {renderAvailableDatasets();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
}

/**
 * Send the request to get the available datasets
 */
function renderAvailableDatasets() {
	// some clean up
	$('#datasetDiv').html('');
	$('#libDiv').html('');
	$('#funcDiv').html('');
	$('#siteDiv').html('');
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();

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
	var datasetDiv = $('#datasetDiv');
	var table = $('<table>');
	datasetDiv.append(table);
	var datasets = [];
	$.each(data, function(dataset, name) {
		datasets.push(dataset);
	});
	datasets.sort(compareIgnoreCase);
	$.each(datasets, function(i, name) {
		var tr = $('<tr>');
		table.append(tr);
		var td = $('<td>');
		tr.append(td);
		var input = $('<input>');
		input.attr({
			'type': 'radio',
			'name': 'datasets',
			'value': data[name]
		});
		input.click(function(event) {renderAvailableLibraries();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
}

/**
 * Send the request to get the available libraries
 */
function renderAvailableLibraries() {
	// some clean up
	$('#libDiv').html('');
	$('#funcDiv').html('');
	$('#siteDiv').html('');
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();

	// send the request
	var dataset = getSelectedDatasetName();
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
	var libDiv = $('#libDiv');
	var table = $('<table>');
	libDiv.append(table);
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	$.each(names, function(i, name) {
		var tr = $('<tr>');
		table.append(tr);
		var td = $('<td>');
		tr.append(td);
		var input = $('<input>');
		input.attr({
			'type': 'radio',
			'name': 'libs',
			'value': data[name]
		});
		input.click(function(event) {renderAvailableFunctions();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
}

/**
 * Send the request to get the available functions
 */
function renderAvailableFunctions() {
	// some clean up
	$('#funcDiv').html('');
	$('#siteDiv').html('');
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();
	
	// send the request
	var lib = getSelectedLibraryName();
	if (lib != null) {
		var url = HOME + '/query?action=getFunctions&library=' + encodeSafeURIComponent(lib);
		scanner.GET(url, true, postRenderAvailableFunctions, null, null, 0);
	}
}

/**
 * Render the available functions
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
function postRenderAvailableFunctions(data, textStatus, jqXHR, param) {
	var funcDiv = $('#funcDiv');
	var table = $('<table>');
	funcDiv.append(table);
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	$.each(names, function(i, name) {
		var tr = $('<tr>');
		table.append(tr);
		var td = $('<td>');
		tr.append(td);
		var input = $('<input>');
		input.attr({
			'type': 'radio',
			'name': 'funcs',
			'value': data[name]
		});
		input.click(function(event) {renderAvailableSites(); renderAvailableParameters();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
}

/**
 * Send the request to get the available parameters
 */
function renderAvailableParameters() {
	// some clean up
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();
	
	// send the request
	var func = getSelectedFunctionName();
	var lib = getSelectedLibraryName();
	if (func != '') {
		var url = HOME + '/query?action=getParameters&function=' + encodeSafeURIComponent(func) + '&library=' + encodeSafeURIComponent(lib);
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
	var uidiv = $('#ui');
	var paramsDiv = $('<div>');
	paramsDiv.attr({'id': 'paramsDiv'});
	uidiv.append(paramsDiv);
	var h1 = $('<h1>');
	paramsDiv.append(h1);
	h1.html('Request Parameters');
	$.each(data, function(i, param) {
		$.each(param, function(key, res) {
			var h2 = $('<h2>');
			paramsDiv.append(h2);
			h2.html(res['cname']);
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
		var h2 = $('<h2>');
	});
	
	paramsDiv.append('<br>');
	paramsDiv.append('<br>');
	var buttonsDiv = $('<div>');
	buttonsDiv.attr({'id': 'buttonsDiv'});
	uidiv.append(buttonsDiv);
	var input = $('<input>');
	input.attr({'type': 'button',
		'value': 'Submit'});
	input.val('Submit');
	input.click(function(event) {submitQuery();});
	buttonsDiv.append(input);
	var input = $('<input>');
	input.attr({'type': 'button',
		'value': 'Clear'});
	input.val('Clear');
	input.click(function(event) {renderAvailableStudies();});
	buttonsDiv.append(input);
	buttonsDiv.append('<br>');
	buttonsDiv.append('<br>');
}

/**
 * Send the request to get the available sites
 */
function renderAvailableSites() {
	var url = HOME + '/query?action=getSites' +
			'&study=' + encodeSafeURIComponent(getSelectedStudyName()) +
			'&dataset=' + encodeSafeURIComponent(getSelectedDatasetName()) +
			'&library=' + encodeSafeURIComponent(getSelectedLibraryName()) +
			'&function=' + encodeSafeURIComponent(getSelectedFunctionName());
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
	var siteDiv = $('#siteDiv');
	var table = $('<table>');
	siteDiv.append(table);
	var names = [];
	$.each(data, function(name, value) {
		names.push(name);
	});
	names.sort(compareIgnoreCase);
	$.each(names, function(i, name) {
		var tr = $('<tr>');
		table.append(tr);
		var td = $('<td>');
		tr.append(td);
		var input = $('<input>');
		input.attr({
			'type': 'checkbox',
			'name': 'sites',
			'checked': 'checked',
			'value': data[name]
		});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
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
	obj['function'] = getSelectedFunctionName();
	obj['sites'] = valueToString(sites);
	var url = HOME + '/query';
	$('*', $('#paramsDiv')).css('cursor', 'wait');
	$('*', $('#buttonsDiv')).css('cursor', 'wait');
	document.body.style.cursor = "wait";
	scanner.POST(url, obj, true, postSubmitQuery, null, null, 0);
}

/**
 * Render the available studies
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
	$('*', $('#paramsDiv')).css('cursor', 'default');
	$('*', $('#buttonsDiv')).css('cursor', 'default');
	document.body.style.cursor = "default";
	data = $.parseJSON(data);
	if (getSelectedLibraryName() == 'Oceans') {
		buildDataTable(data);
	} else {
		buildTreeResult(data);
	}
}

function createStudy(name) {
	var url = HOME + '/registry?action=createStudy' +
				'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function createDataset(name, study) {
	var url = HOME + '/registry?action=createDataset' +
			'&study=' + encodeSafeURIComponent(study) +
			'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function createLibrary(name, rpath) {
	var url = HOME + '/registry?action=createLibrary' +
			'&rpath=' + encodeSafeURIComponent(rpath) +
			'&cname=' + encodeSafeURIComponent(name);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function createFunction(name, lib, rpath) {
	var url = HOME + '/registry?action=createFunction' +
					'&rpath=' + encodeSafeURIComponent(rpath) +
					'&cname=' + encodeSafeURIComponent(name) +
					'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function createMaster(rURL) {
	var url = HOME + '/registry?action=createMaster&rURL=' + encodeSafeURIComponent(rURL);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function createWorker(study, dataset, lib, func, site, datasource, rURL) {
	var url = HOME + '/registry?action=createWorker' +
					'&study=' + encodeSafeURIComponent(study) +
					'&dataset=' + encodeSafeURIComponent(dataset) +
					'&library=' + encodeSafeURIComponent(lib) +
					'&function=' + encodeSafeURIComponent(func) +
					'&site=' + encodeSafeURIComponent(site) +
					'&datasource=' + encodeSafeURIComponent(datasource) +
					'&rURL=' + encodeSafeURIComponent(rURL);
					
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function createParameter(name, func, lib, minOccurs, maxOccurs, values) {
	var url = HOME + '/registry?action=createParameter' +
					'&cname=' + encodeSafeURIComponent(name) +
					'&function=' + encodeSafeURIComponent(func) +
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

function deleteWorker(study, dataset, lib, func, site) {
	var url = HOME + '/registry?action=deleteWorker' +
		'&study=' + encodeSafeURIComponent(study) +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&library=' + encodeSafeURIComponent(lib) +
		'&function=' + encodeSafeURIComponent(func) +
		'&site=' + encodeSafeURIComponent(site);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function deleteParameter(name, func, lib) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteParameter' +
		'&library=' + encodeSafeURIComponent(lib) +
		'&function=' + encodeSafeURIComponent(func);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function deleteMaster() {
	var url = HOME + '/registry?action=deleteMaster';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function deleteFunction(name, lib) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteFunction' +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function deleteLibrary(name) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteLibrary';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function deleteDataset(name, study) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteDataset' +
		'&study=' + encodeSafeURIComponent(study);
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function deleteStudy(name) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=deleteStudy';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function updateLibrary(id, name, rpath) {
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

function updateFunction(id, name, lib, rpath) {
	var url = HOME + '/registry?action=updateFunction&id=' + encodeSafeURIComponent(id);
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

function updateMaster(rURL) {
	var url = HOME + '/registry?rURL=' + encodeSafeURIComponent(rURL) + '&action=updateMaster';
	document.body.style.cursor = "wait";
	scanner.POST(url, {}, true, postResourceAction, null, null, 0);
}

function updateWorker(id, study, dataset, lib, func, site, datasource, rURL) {
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
		url += '&function=' + encodeSafeURIComponent(func);
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

function updateParameter(id, name, func, lib, minOccurs, maxOccurs, values) {
	var url = HOME + '/registry?action=updateParameter&id=' + encodeSafeURIComponent(id);
	if (name != null) {
		url += '&cname=' + encodeSafeURIComponent(name);
	}
	if (func != null) {
		url += '&function=' + encodeSafeURIComponent(func);
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

function getStudy(name) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getStudy';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getDataset(name, study) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getDataset' +
		'&study=' + encodeSafeURIComponent(study);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getLibrary(name) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getLibrary';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getFunction(name, lib) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getFunction' +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getMaster() {
	var url = HOME + '/registry?action=getMaster';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getParameter(name, func, lib) {
	var url = HOME + '/registry?cname=' + encodeSafeURIComponent(name) + 
		'&action=getParameter' +
		'&function=' + encodeSafeURIComponent(func) +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getWorker(study, dataset, lib, func, site) {
	var url = HOME + '/registry?study=' + encodeSafeURIComponent(study) + 
		'&action=getWorker' +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&function=' + encodeSafeURIComponent(func) +
		'&site=' + encodeSafeURIComponent(site) +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getStudies() {
	var url = HOME + '/registry?action=getStudies';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getDatasets(study) {
	var url = HOME + '/registry?action=getDatasets' +
		'&study=' + encodeSafeURIComponent(study);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getLibraries() {
	var url = HOME + '/registry?action=getLibraries';
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getFunctions(lib) {
	var url = HOME + '/registry?action=getFunctions' +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getParameters(func, lib) {
	var url = HOME + '/registry?action=getParameters' +
		'&function=' + encodeSafeURIComponent(func) +
		'&library=' + encodeSafeURIComponent(lib);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getSites(study, dataset, lib, func) {
	var url = HOME + '/registry?action=getSites' +
		'&study=' + encodeSafeURIComponent(study) +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&library=' + encodeSafeURIComponent(lib) +
		'&function=' + encodeSafeURIComponent(func);
	document.body.style.cursor = "wait";
	scanner.GET(url, true, postGetResourceAction, null, null, 0);
}

function getWorkers(study, dataset, lib, func, sites) {
	var url = HOME + '/registry?study=' + encodeSafeURIComponent(study) + 
		'&action=getWorkers' +
		'&dataset=' + encodeSafeURIComponent(dataset) +
		'&function=' + encodeSafeURIComponent(func) +
		'&library=' + encodeSafeURIComponent(lib);
	if (sites != null && sites.length > 0) {
		url += '&sites=';
		var values = arrayToString(sites);
		url += encodeSafeURIComponent(values);
	}
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
	$('#resultDiv').remove();
	var div = $('#ui');
	var resultDiv = $('<div>');
	resultDiv.attr({'id': 'resultDiv'});
	div.append(resultDiv);
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

function renderLogin() {
	var uiDiv = $('#ui');
	uiDiv.html('');
	var h2 = $('<h2>');
	uiDiv.append(h2);
	h2.html('Log In');
	var fieldset = $('<fieldset>');
	uiDiv.append(fieldset);
	var legend = $('<legend>');
	fieldset.append(legend);
	legend.html('Login');
	var table = $('<table>');
	fieldset.append(table);
	var tr = $('<tr>');
	table.append(tr);
	var td = $('<td>');
	tr.append(td);
	td.html('Username: ');
	var input = $('<input>');
	input.attr({'type': 'text',
		'id': 'username',
		'name': 'username',
		'size': 15
	});
	td.append(input);
	tr = $('<tr>');
	table.append(tr);
	td = $('<td>');
	tr.append(td);
	td.html('Password: ');
	var input = $('<input>');
	input.attr({'type': 'password',
		'id': 'password',
		'name': 'password',
		'size': 15
	});
	td.append(input);
	tr = $('<tr>');
	table.append(tr);
	td = $('<td>');
	tr.append(td);
	var input = $('<input>');
	input.attr({'type': 'button',
		'value': 'Login'
	});
	input.val('Login');
	td.append(input);
	input.click(function(event) {submitLogin();});
	td.append(input);
}

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
	var uiDiv = $('#ui');
	$('#errorDiv').remove();
	if (res['status'] == 'success') {
		loginRegistry();
	} else {
		var errorDiv = $('<div>');
		uiDiv.append(errorDiv);
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

function renderSelectTable() {
	var div = $('#ui');
	var h1 = $('<h1>');
	div.append(h1);
	h1.html('Query Scope');
	var tableDiv = $('<div>');
	div.append(tableDiv);
	var table = $('<table>');
	tableDiv.append(table);
	table.css({'border': '1px solid black',
		'border-collapse': 'collapse'
	});
	var thead = $('<thead>');
	table.append(thead);
	var tr = $('<tr>');
	thead.append(tr);
	var th = $('<th>');
	th.css({'border': '1px solid black'
	});
	tr.append(th);
	th.html('Studies');
	var th = $('<th>');
	th.css({'border': '1px solid black'
	});
	tr.append(th);
	th.html('Datasets');
	var th = $('<th>');
	th.css({'border': '1px solid black'
	});
	tr.append(th);
	th.html('Libraries');
	var th = $('<th>');
	th.css({'border': '1px solid black'
	});
	tr.append(th);
	th.html('Functions');
	var th = $('<th>');
	th.css({'border': '1px solid black'
	});
	tr.append(th);
	th.html('Sites');
	var tbody = $('<tbody>');
	table.append(tbody);
	var tr = $('<tr>');
	tbody.append(tr);
	var td = $('<td>');
	td.css({'border': '1px solid black'
	});
	tr.append(td);
	var studyDiv = $('<div>');
	studyDiv.attr({'id': 'studyDiv'});
	td.append(studyDiv);
	var td = $('<td>');
	td.css({'border': '1px solid black'
	});
	tr.append(td);
	var datasetDiv = $('<div>');
	datasetDiv.attr({'id': 'datasetDiv'});
	td.append(datasetDiv);
	var td = $('<td>');
	td.css({'border': '1px solid black'
	});
	tr.append(td);
	var libDiv = $('<div>');
	libDiv.attr({'id': 'libDiv'});
	td.append(libDiv);
	var td = $('<td>');
	td.css({'border': '1px solid black'
	});
	tr.append(td);
	var funcDiv = $('<div>');
	funcDiv.attr({'id': 'funcDiv'});
	td.append(funcDiv);
	var td = $('<td>');
	td.css({'border': '1px solid black'
	});
	tr.append(td);
	var siteDiv = $('<div>');
	siteDiv.attr({'id': 'siteDiv'});
	td.append(siteDiv);
	tableDiv.append($('<br>'));
	tableDiv.append($('<br>'));
}

function expandAll() {
	$('#navigation').find('div.hitarea.expandable-hitarea').click();
	//$('#navigation').find('div.hitarea.tree-hitarea.expandable-hitarea').click();
}

function buildTreeResult(res) {
	$('#resultDiv').remove();
	var div = $('#ui');
	var resultDiv = $('<div>');
	resultDiv.attr({'id': 'resultDiv'});
	div.append(resultDiv);
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

