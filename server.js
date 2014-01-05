module.exports = function (app, response) {
	var request = require('request'),
		xml2js = require('xml2js');

	var convert2js = function(xml){
		var parseString = xml2js.parseString;
        parseString(xml, function (err, result) {
            sendToApi = JSON.stringify(result);
            response.writeHead(200, {'Content-Type': 'application/json'});
			response.end(sendToApi);
        });
	}

	var url = app.url;
	var parts = url.split('?'); //grab parameters
	if(parts.length == 1){
		parts.push("");
	}
	if(parts[0].indexOf('/alerts') !== -1){
		request('http://www.transitchicago.com/api/1.0/alerts.aspx?' + parts[1], function (err, res, xml) {
	        if (!err && res.statusCode === 200) {
	            convert2js(xml);
	        }
	    });
	}else if(parts[0].indexOf('/routes') !== -1){
		request('http://www.transitchicago.com/api/1.0/routes.aspx?' + parts[1], function (err, res, xml) {
	        if (!err && res.statusCode === 200) {
	            convert2js(xml);
	        }
	    });
	}else{
		response.writeHead(404, {'Content-Type': 'application/json'});
		response.end("Failed to serve " + url);
	}

};