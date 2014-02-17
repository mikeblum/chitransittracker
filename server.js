'use strict';

var url = require('url'),
	request = require('request'),
	xml2js = require('xml2js'),
	mongoose = require('mongoose'),
	moment = require('moment'),
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

var alertSchema = Schema({
	'alertId': String,
	'alertURL': String,
	'eventEnd': String,
	'eventStart': String,
	'fullDescription': String,
	'guid': String,
	'headline': String,
	'impact': String,
	'impactedService': {},
	'majorAlert': String,
	'severityCSS': String,
	'severityColor': String,
	'severityScore': String,
	'shortDescription': String,
	'tbd': String,
	'ttim': String
});

var stopSchema = Schema({
	'stop_id': Number,
	'stop_code': Number,
	'stop_name': String,
	'stop_desc': String,
	'loc':{
		'stop_lat': Number,
		'stop_lon': Number
	},
	'location_type':String,
	'parent_station': String,
	'wheelchair_boarding': String
});

var RailRoute = mongoose.model('RailRoute', routeSchema, 'RailRoutes');

var BusRoute = mongoose.model('BusRoute', routeSchema, 'BusRoutes');

var Station = mongoose.model('Station', routeSchema, 'Stations');

var Alert = mongoose.model('Alert', alertSchema, 'Alerts');

var Stop = mongoose.model('Stop', stopSchema, 'Stops');

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
		var routeType = params.type === 'rail' ? RailRoute : (params.type === 'bus') ? BusRoute : Station;
		routeType.find({}, function (err, docs){
			results = docs; 
			response.writeHead(200, {'Content-Type': 'application/json'});
			response.end(JSON.stringify(results));
		});
	}else if(path.indexOf('search') !== -1){
		var results = {};
		var regex = params.query;
		regex = regex.replace(" and "," & ");
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
		Alert.find({}, function (err, docs){
			results = docs; 
			response.writeHead(200, {'Content-Type': 'application/json'});
			response.end(JSON.stringify(results));
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
		//replace any and with &
		regex = regex.replace(" and "," & ");
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
	}else if(path.indexOf('nearby') !== -1){
		var dist = 0.20/111.12; //200 meters / degrees
		Stop.distinct('stop_id', 
		{
		    loc: {
		        $near: [params.long, params.lat], //mongo expects longitude first
		        $maxDistance: dist
		    }
		}, function(err, stopIds){
			_.each(stopIds, function(id, index){
			    stopIds[index] = id.toString();
			});
			var results = [];
			//this is expensive
			BusRoute.find({}, function(err, busRoutes){
				_.each(busRoutes, function(route){
					var routeId = route.serviceId;
					_.each(route.stops, function(direction){
						_.each(direction, function(stop){
							if(_.indexOf(stopIds, stop.stpid) > -1){
								stop.routeId = routeId;
								stop.type = 'bus';
								stop.routeName = stop.stpnm;
								results.push(stop);
							}
						});
					});
				});
				response.writeHead(200, {'Content-Type': 'application/json'});
				response.end(JSON.stringify(results));
			});
		});
	}
};