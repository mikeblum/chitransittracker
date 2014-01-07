'use strict';

var request = require('request'),
		xml2js = require('xml2js'),
		mongoose = require('mongoose'),
		_ = require('underscore');

var parser = xml2js.Parser({ explicitArray: false });

// Build the connection string
var dbURI = 'mongodb://localhost/test';

// Create the database connection
mongoose.connect(dbURI);
// CONNECTION EVENTS
// When successfully connected
mongoose.connection.on('connected', function () {
  console.log('Mongoose default connection open to ' + dbURI);
});

// If the connection throws an error
mongoose.connection.on('error',function (err) {
  console.log('Mongoose default connection error: ' + err);
});

// When the connection is disconnected
mongoose.connection.on('disconnected', function () {
  console.log('Mongoose default connection disconnected');
});

// If the Node process ends, close the Mongoose connection
process.on('SIGINT', function() {
	mongoose.connection.close(function () {
		console.log('Mongoose default connection disconnected through app termination');
		process.exit(0);
	});
});

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));

var railRouteSchema = mongoose.Schema({
	Route: String,
	RouteColorCode: String,
	RouteTextColor: String,
	ServiceId: String,
	RouteURL: String,
	RouteStatus: String,
	RouteStatusColor: String
}, {collection: 'RailRoutes'});

var busRouteSchema = mongoose.Schema({
	Route: String,
	RouteColorCode: String,
	RouteTextColor: String,
	ServiceId: String,
	RouteURL: String,
	RouteStatus: String,
	RouteStatusColor: String
}, {collection: 'BusRoutes'});

var stationSchema = mongoose.Schema({
	Route: String,
	RouteColorCode: String,
	RouteTextColor: String,
	ServiceId: String,
	RouteURL: String,
	RouteStatus: String,
	RouteStatusColor: String
}, {collection: 'Stations'});

var RailRoute = mongoose.model('RailRoute', railRouteSchema);

var BusRoute = mongoose.model('BusRoute', busRouteSchema);

var Station = mongoose.model('Station', stationSchema);

var pushRoutesToServer = function(data, type){
	_.each(JSON.parse(data), function(el){
		var RouteType = type === 'rail' ? new RailRoute(el) : (type === 'bus') ? new BusRoute(el) : new Station(el);
		RouteType.save(function(err, num, n){
			if(err){
				console.log(err);
			}
			console.log(n + ' written');
		});
	});
};


var convert2js = function(xml, response, type){
    parser.parseString(xml, function (err, result) {
        var json = JSON.stringify(result);
        pushRoutesToServer(json, type);
        response.writeHead(200, {'Content-Type': 'application/json'});
		response.end(json);
    });
};

module.exports = function (app, response) {
	var url = app.url;
	var parts = url.split('?'); //grab parameters
	if(parts.length == 1){
		parts.push("");
	}
	if(parts[0].indexOf('/alerts') !== -1){
		request('http://www.transitchicago.com/api/1.0/alerts.aspx?' + parts[1],
			function (err, res, xml) {
	        	if (!err && res.statusCode === 200) {
	            	convert2js(xml, response);
	        	}
	    	}
	    );
	}else if(parts[0].indexOf('/routes') !== -1){
		request('http://www.transitchicago.com/api/1.0/routes.aspx?' + parts[1],
			function (err, res, xml) {
	        	if (!err && res.statusCode === 200) {
	        		if(parts[1].indexOf('rail') !== -1){
	        			convert2js(xml, response, 'rail');
	        		}else if(parts[1].indexOf('station') !== -1){
	        			convert2js(xml, response, 'station');
	        		}else{
	        			convert2js(xml, response, 'bus');
	        		}
	        	}
	    	}
	    );
	}else{
		response.writeHead(404, {'Content-Type': 'application/json'});
		response.end("Failed to serve " + url);
	}
};