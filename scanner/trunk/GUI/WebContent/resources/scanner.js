var MAX_RETRIES = 1;
var AJAX_TIMEOUT = 300000;
var HOME = 'https://aspc.isi.edu/scanner';

function test() {
	renderLogin();
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
	var oTable = $('#example').dataTable({
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
	// check if the login page was loaded under an i-frame
	document.body.style.cursor = "default";
	var res = $.parseJSON(data);
	if (res['status'] == 'success') {
		loginRegistry();
	}
}

function loginRegistry() {
	var url = HOME + '/tagfiler';
	var obj = new Object();
	obj['action'] = 'login';
	scanner.POST(url, obj, true, postLoginRegistry, null, null, 0);
}
function postLoginRegistry(data, textStatus, jqXHR, param) {
	var res = $.parseJSON(data);
	if (res['status'] == 'success') {
		renderAvailableLibraries();
	} else {
		alert(res['status']);
	}
}
function downloadFile() {
	var url = HOME + '/tagfiler';
	var obj = new Object();
	obj['action'] = 'file';
	scanner.POST(url, obj, true, postDownloadFile, null, null, 0);
}
function postDownloadFile(data, textStatus, jqXHR, param) {
	var res = $.parseJSON(data);
	if (res['status'] == 'success') {
		tagfilerUserLogout();
	} else {
		alert(res['status']);
	}
}

function tagfilerUserLogout() {
	var url = HOME + '/tagfiler';
	var obj = new Object();
	obj['action'] = 'logout';
	scanner.POST(url, obj, true, postTagfilerUserLogout, null, null, 0);
}

function postTagfilerUserLogout(data, textStatus, jqXHR, param) {
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
	} else if ($.isEmptyObject(val)) {
		return '"EmptyObject"';
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
function renderAvailableFunction() {
	$('#funcDiv').html('');
	$('#datasetDiv').html('');
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();
	var lib = $('input:radio[name=libs]:checked').val();
	if (lib != '') {
		var url = HOME + '/tagfiler?action=getFunctions&lib=' + encodeSafeURIComponent(lib);
		scanner.GET(url, true, postRenderAvailableFunctions, null, null, 0);
	}
}

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
			'value': name
		});
		input.click(function(event) {renderAvailableDatasets();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(data[name]);
	});
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

function submitQuery() {
	var params = getSelectedParameters();
	var obj = {};
	obj['params'] = params;
	obj['action'] = 'getResults';
	obj['dataset'] = $('input:radio[name=datasets]:checked').val();
	obj['lib'] = $('input:radio[name=libs]:checked').val();
	obj['func'] = $('input:radio[name=funcs]:checked').val();
	var url = HOME + '/tagfiler';
	$('*', $('#paramsDiv')).css('cursor', 'wait');
	$('*', $('#buttonsDiv')).css('cursor', 'wait');
	document.body.style.cursor = "wait";
	scanner.POST(url, obj, true, postSubmitQuery, null, null, 0);
}

function postSubmitQuery(data, textStatus, jqXHR, param) {
	$('*', $('#paramsDiv')).css('cursor', 'default');
	$('*', $('#buttonsDiv')).css('cursor', 'default');
	document.body.style.cursor = "default";
	data = $.parseJSON(data);
	if ($('input:radio[name=libs]:checked').val() == 'Oceans') {
		buildDataTable(data);
	} else {
		buildTreeResult(data);
	}
}

function renderAvailableDatasets() {
	$('#datasetDiv').html('');
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();
	var func = $('input:radio[name=funcs]:checked').val();
	if (func != '') {
		var url = HOME + '/tagfiler?action=getDatasets&func=' + encodeSafeURIComponent(func);
		scanner.GET(url, true, postRenderAvailableDatasets, null, null, 0);
	}
}

function postRenderAvailableDatasets(data, textStatus, jqXHR, param) {
	var datasetDiv = $('#datasetDiv');
	var table = $('<table>');
	datasetDiv.append(table);
	var datasets = [];
	$.each(data[0]['datasetNodes'], function(i, dataset) {
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
			'value': name
		});
		input.click(function(event) {renderAvailableParameters();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
}

function renderAvailableLibraries() {
	var url = HOME + '/tagfiler?action=getLibraries';
	scanner.GET(url, true, postRenderAvailableLibraries, null, null, 0);
}

function renderSelectTable() {
	var div = $('#ui');
	var tableDiv = $('<div>');
	div.append(tableDiv);
	var div = $('#ui');
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
	th.html('Datasets');
	var tbody = $('<tbody>');
	table.append(tbody);
	var tr = $('<tr>');
	tbody.append(tr);
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
	var datasetDiv = $('<div>');
	datasetDiv.attr({'id': 'datasetDiv'});
	td.append(datasetDiv);
	tableDiv.append($('<br>'));
	tableDiv.append($('<br>'));
	
}

function postRenderAvailableLibraries(data, textStatus, jqXHR, param) {
	var div = $('#ui');
	div.html('');
	renderSelectTable();
	var libDiv = $('#libDiv');
	var libs = [];
	$.each(data, function(i, lib) {
		libs.push(lib['datasetName']);
	});
	libs.sort(compareIgnoreCase);
	var table = $('<table>');
	libDiv.append(table);
	$.each(libs, function(i, name) {
		var tr = $('<tr>');
		table.append(tr);
		var td = $('<td>');
		tr.append(td);
		var input = $('<input>');
		input.attr({
			'type': 'radio',
			'name': 'libs',
			'value': name
		});
		input.click(function(event) {renderAvailableFunction();});
		td.append(input);
		var td = $('<td>');
		tr.append(td);
		td.html(name);
	});
}

function renderAvailableParameters() {
	$('#paramsDiv').remove();
	$('#buttonsDiv').remove();
	$('#resultDiv').remove();
	var dataset = $('input:radio[name=datasets]:checked').val();
	if (dataset != '') {
		var url = HOME + '/tagfiler?action=getParameters&func=' + encodeSafeURIComponent($('input:radio[name=funcs]:checked').val());
		scanner.GET(url, true, postRenderAvailableParameters, null, null, 0);
	}
}

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
			h2.html(res['datasetDisplayName']);
			var value = res['parametersValues'];
			var minOccurs = res['minOccurs'];
			var maxOccurs = res['maxOccurs'];
			$.each(value, function(j, val) {
				var div = $('<div>');
				paramsDiv.append(div);
				var input = $('<input>');
				input.attr({'type': 'checkbox',
					'checked': 'checked',
					'parName': res['datasetDisplayName'],
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
	input.click(function(event) {renderAvailableLibraries();});
	buttonsDiv.append(input);
	buttonsDiv.append('<br>');
	buttonsDiv.append('<br>');
}

function expandAll() {
	$('#navigation').find('div.hitarea.expandable-hitarea').click();
	//$('#navigation').find('div.hitarea.tree-hitarea.expandable-hitarea').click();
}

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

