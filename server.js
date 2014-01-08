'use strict';

var url = require('url'),
	request = require('request'),
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

var Schema = mongoose.Schema;

var railRouteSchema = Schema({
	'Route': String,
	'RouteColorCode': String,
	'RouteTextColor': String,
	'ServiceId': String,
	'RouteURL': String,
	'RouteStatus': String,
	'RouteStatusColor': String,
	'RouteIcon': { type: String, default: 'images/cta_train.svg' }
});

var busRouteSchema = Schema({
	'Route': String,
	'RouteColorCode': String,
	'RouteTextColor': String,
	'ServiceId': String,
	'RouteURL': String,
	'RouteStatus': String,
	'RouteStatusColor': String,
	'RouteIcon': { type: String, default: 'images/cta_bus.svg' }
});

var RailRoute = mongoose.model('RailRoute', railRouteSchema, 'RailRoutes');

var BusRoute = mongoose.model('BusRoute', busRouteSchema, 'BusRoutes');

var Station = mongoose.model('Station', railRouteSchema, 'Stations');

var pushRoutesToServer = function(data, type){
	_.each(data.CTARoutes.RouteInfo, function(el){
		var RouteType = type === 'rail' ? RailRoute : (type === 'bus') ? BusRoute : Station;
		var route = type === 'rail' ? new RailRoute(el) : (type === 'bus') ? new BusRoute(el) : new Station(el);
		RouteType.findOneAndUpdate({ Route: route.Route }, el, { upsert: true }, function (err) {
			if(err){ console.log(err); }
		});
	});
};


var convert2js = function(xml, response, type){
	parser.parseString(xml, function (err, json) {
		pushRoutesToServer(json, type);
		response.writeHead(200, {'Content-Type': 'application/json'});
		response.end(JSON.stringify(json));
	});
};


module.exports = function (app, response) {
	var urlParts = url.parse(app.url, true);
	var path = urlParts.pathname;
	var query = urlParts.query;

	if(path.indexOf('routes') !== -1){
		request('http://www.transitchicago.com/api/1.0/routes.aspx?type=' + query.type,
			function (err, res, xml) {
				if (!err && res.statusCode === 200) {
					convert2js(xml, response, query.type);
				}
			}
		);
	}else if(path.indexOf('search') !== -1){
		var results = [];
		var regex = query.query;
		Station.find( { $or: [ 
			{ ServiceId: new RegExp(regex, 'i') }, 
			{ Route: new RegExp(regex, 'i') }
		]}, function (err, docs){
			results.push.apply(results, docs); 
			RailRoute.find( { $or: [ 
				{ ServiceId: new RegExp(regex, 'i') }, 
				{ Route: new RegExp(regex, 'i') }
			]}, function (err, docs){
				results.push.apply(results, docs); 
				BusRoute.find( { $or: [ 
					{ ServiceId: new RegExp(regex, 'i') }, 
					{ Route: new RegExp(regex, 'i') }
				]}, function (err, docs){
					results.push.apply(results, docs); 
					response.writeHead(200, {'Content-Type': 'application/json'});
					response.end(JSON.stringify(results));
				});
			});
		});
	}

};