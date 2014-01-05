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
	if(app.url === '/alerts'){
		request('http://www.transitchicago.com/api/1.0/alerts.aspx', function (err, res, xml) {
	        if (!err && res.statusCode === 200) {
	            convert2js(xml);
	        }
	    });
	}else if(app.url === '/routes'){
		request('http://www.transitchicago.com/api/1.0/routes.aspx', function (err, res, xml) {
	        if (!err && res.statusCode === 200) {
	            convert2js(xml);
	        }
	    });
	}else{
		response.end({ 'error': 404,
					   'message': 'Route Not Found'
					  });
	}
};