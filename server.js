'use strict';

var url = require('url'),
	request = require('request'),
	xml2js = require('xml2js'),
	mongoose = require('mongoose'),
	_ = require('underscore');

var ctaTrainTrackerApiKey = process.env.CTA_TRAIN_TRACKER_API_KEY;

var ctaBusTrackerApiKey = process.env.CTA_BUS_TRACKER_API_KEY;

var parser = xml2js.Parser({ explicitArray: false });

// Build the connection string
var dbURI = process.env.MONGOHQ_URL || 'mongodb://localhost/test';

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

var routeSchema = Schema({
	'route': Array,
	'routeColorCode': String,
	'routeTextColor': String,
	'serviceId': String,
	'routeURL': String,
	'routeStatus': String,
	'routeStatusColor': String,
	'lastUpdated': { type: Date, default: Date.now },
	'directions': [],
	'stops': {},
	'type': String
});

var RailRoute = mongoose.model('RailRoute', routeSchema, 'RailRoutes');

var BusRoute = mongoose.model('BusRoute', routeSchema, 'BusRoutes');

var Station = mongoose.model('Station', routeSchema, 'Stations');

var pushRouteToDatabase = function(routeType, route){
	routeType.findOneAndUpdate({ serviceId: route.ServiceId }, {
			route: route.Route,
			routeColorCode: route.RouteColorCode,
			routeTextColor: route.RouteTextColor,
			serviceId: route.ServiceId,
			routeURL: route.RouteURL,
			routeStatus: route.RouteStatus,
			routeStatusColor: route.RouteStatusColor,
			directions: route.directions,
			stops: route.stops
		}, { upsert: true }, function (err) {
			if(err){ console.log(err); }
	});
};

var prepareRoutesForDatabase = function(data, type){
	_.each(data.CTARoutes.RouteInfo, function(route){
		var routeType = type === 'rail' ? RailRoute : (type === 'bus') ? BusRoute : Station;

		route.Route =  route.Route.split('|'); //split route name from physical address
		route.type = 'rail';
		//get directions of bus routes
		if(type === 'bus'){
			route.type = 'bus';
			request('http://www.ctabustracker.com/bustime/api/v1/getdirections?key=' + ctaBusTrackerApiKey + '&rt=' + route.ServiceId, function (err, res, xml){
				if (!err && res.statusCode === 200) {
					parser.parseString(xml, function (err, json) {
						route.directions = json['bustime-response'].dir || [];
						route.stops = {};
						if(route.directions){
							//get stops for each direction
							_.each(route.directions, function(direction){
								request('http://www.ctabustracker.com/bustime/api/v1/getstops?key=' + ctaBusTrackerApiKey + '&rt=' + route.ServiceId + '&dir=' + direction, function (err, res, xml){
									if (!err && res.statusCode === 200) {
										parser.parseString(xml, function (err, json){
											route.stops[direction] = json['bustime-response'].stop || [];
											pushRouteToDatabase(routeType, route);
										});
									}else{
										console.log(err);
									}
								});
							});
						}else{
							pushRouteToDatabase(routeType, route);
						}
					});
				}else{
					console.log(err);
				}
			});
		}else{
			pushRouteToDatabase(routeType, route);
		}
	});
};


var processRoutes = function(xml, response, type){
	parser.parseString(xml, function (err, json) {
		prepareRoutesForDatabase(json, type);
		response.writeHead(200, {'Content-Type': 'application/json'});
		response.end(JSON.stringify(json));
	});
};

//don't push these responses to db - too slow
var processResponse = function(xml, response){
	parser.parseString(xml, function (err, json) {
		response.writeHead(200, {'Content-Type': 'application/json'});
		response.end(JSON.stringify(json));
	});
};

module.exports = function (app, response) {
	var urlParts = url.parse(app.url, true);
	var path = urlParts.pathname;
	var params = urlParts.query;
	if(path.indexOf('routes') !== -1){
		request('http://www.transitchicago.com/api/1.0/routes.aspx?type=' + params.type,
			function (err, res, xml) {
				if (!err && res.statusCode === 200) {
					console.log('processing routes');
					processRoutes(xml, response, params.type);
				}else{
					console.log(err);
				}
			}
		);
	}else if(path.indexOf('search') !== -1){
		var results = {};
		var regex = params.query;
		Station.find( {
			'route.0': new RegExp(regex, 'i')
		}, function (err, docs){
			results.stations = docs; 
			BusRoute.find( { $or: [ 
					{ serviceId: new RegExp(regex, 'i') }, 
					{ route: new RegExp(regex, 'i') }
				]}, function (err, docs){
					results.busRoutes = docs;
					response.writeHead(200, {'Content-Type': 'application/json'});
					response.end(JSON.stringify(results));
			});
		});
	}else if(path.indexOf('arrivals') !== -1){
		if(params.type === 'rail'){
			request('http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=' + ctaTrainTrackerApiKey + '&mapid=' + params.stop + '&max=4',
				function (err, res, xml) {
					if (!err && res.statusCode === 200) {
						processResponse(xml, response);
					}else{
						console.log(err);
					}
				}
			);
		}else{
			console.log('bus: ' + 'http://www.ctabustracker.com/bustime/api/v1/getpredictions?key=' + ctaBusTrackerApiKey + '&stpid=' + params.stop + '&top=4');
			request('http://www.ctabustracker.com/bustime/api/v1/getpredictions?key=' + ctaBusTrackerApiKey + '&stpid=' + params.stop + '&top=4',
				function (err, res, xml){
					if (!err && res.statusCode === 200) {
						processResponse(xml, response);
					}else{
						console.log(err);
					}
			});
		}
	}else if(path.indexOf('alerts') !== -1){
		request('http://www.transitchicago.com/api/1.0/alerts.aspx', function (err, res, xml) {
			if (!err && res.statusCode === 200) {
				processResponse(xml, response);
			}else{
				console.log(err);
			}
		});
	}else if(path.indexOf('stationId') !== -1){
		var results = {};
		var serviceId = params.serviceId;
		Station.find( {
			serviceId: serviceId
		}, function (err, docs){
			if(docs.length > 0){
				results = docs[0];
				results.type = 'rail';
			}
			BusRoute.find({
				serviceId: serviceId
			}, function (err, docs){
				if(docs.length > 0){
					results = docs[0];
					results.type = 'bus';
				}
				response.writeHead(200, {'Content-Type': 'application/json'});
				response.end(JSON.stringify(results));
			});
		});
	}else if(path.indexOf('busStops') !== -1){
		var serviceId = params.serviceId;
		var regex = params.query;
		BusRoute.find({
			serviceId: serviceId
		}, function (err, docs){
			if(docs && docs.length > 0){
				results = docs[0];
				results.type = 'bus';
				var matchingStops = [];
				_.each(results.stops, function(stops, direction){
				    _.each(stops, function(stop){
				        if(new RegExp(regex, 'i').exec(stop.stpid) ||
				        	new RegExp(regex, 'i').exec(stop.stpnm)){
				            matchingStops.push(stop);
				        }
				    });
				});
				response.writeHead(200, {'Content-Type': 'application/json'});
				response.end(JSON.stringify(matchingStops));
			}else{
				response.writeHead(500, {'Content-Type': 'application/json'});
				response.end();
			}
		});
	}
};