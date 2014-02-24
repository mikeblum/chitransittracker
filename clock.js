#! /app/bin/node

'use strict';

var url = require('url'),
	request = require('request'),
	xml2js = require('xml2js'),
	mongoose = require('mongoose'),
	moment = require('moment'),
	_ = require('underscore'),
	promise = require('q');

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
	console.log('update-db: getting CTA data');
	console.log('update-db: getting CTA alerts');
});

// If the connection throws an error
mongoose.connection.on('error',function (err) {
  console.log('Mongoose default connection error: ' + err);
  process.exit(1);
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

var RailRoute = mongoose.model('RailRoute', routeSchema, 'RailRoutes');

var BusRoute = mongoose.model('BusRoute', routeSchema, 'BusRoutes');

var Station = mongoose.model('Station', routeSchema, 'Stations');

var Alert = mongoose.model('Alert', alertSchema, 'Alerts');

var ctaDate = 'YYYYMMDD hh:mm:ss';

var pushRouteToDatabase = function(routeType, route){
	var deferred = promise.defer();
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
			deferred.resolve(true);
	});
	return deferred.promise;
};

var prepareRoutesForDatabase = function(data, type){
	var promises = [];
	var prepared = promise.defer();
	_.each(data.CTARoutes.RouteInfo, function(route){
		var routeType = type === 'rail' ? RailRoute : (type === 'bus') ? BusRoute : Station;

		route.Route =  route.Route.split('|'); //split route name from physical address
		route.type = type === 'bus' ? 'bus' : 'rail';
		//get directions of bus routes
		if(type === 'bus'){
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
											promises.push(pushRouteToDatabase(routeType, route));
										});
									}else{
										console.log(err);
										process.exit(1);
									}
								});
							});
						}else{
							promises.push(pushRouteToDatabase(routeType, route));
						}
					});
				}else{
					console.log(err);
					process.exit(1);
				}
			});
		}else{
			promises.push(pushRouteToDatabase(routeType, route));
		}
	});
	promise.allSettled(promises).then(function(){
		console.log('routes done');
		prepared.resolve(true);
	}).done();
	return prepared.promise;
};

var processRoutes = function(xml, type){
	var parsed = promise.defer();
	parser.parseString(xml, function (err, json) {
		parsed.resolve(prepareRoutesForDatabase(json));
	});
	return parsed.promise;
};

var pushAlertToDatabase = function(alert){
	var deferred = promise.defer();
	Alert.findOneAndUpdate({ serviceId: alert.AlertId }, {
		alertId: alert.AlertId,
		alertURL: alert.AlertURL,
		eventEnd: alert.EventEnd,
		eventStart: alert.EventStart,
		fullDescription: alert.FullDescription,
		guid: alert.GUID,
		headline: alert.Headline,
		impact: alert.Impact,
		impactedService: alert.ImpactedService,
		majorAlert: alert.MajorAlert,
		severityCSS: alert.SeverityCSS,
		severityColor: alert.SeverityColor,
		severityScore: alert.SeverityScore,
		shortDescription: alert.ShortDescription,
		tbd: alert.TBD,
		ttim: alert.ttim
	}, { upsert: true }, function (err) {
		if(err){ 
			console.log(err);
		}
		deferred.resolve(true);
	});
	return deferred.promise;
};

var prepareAlertsForDatabase = function(data){
	var promises = [];
	var prepared = promise.defer();
	var now = moment();
	Alert.remove().exec(function(){
		_.each(data.CTAAlerts.Alert, function(alert){
			//only display an alert if we are padt the start date and before the end date
			var start = moment(alert.EventStart, ctaDate);
			var end = moment(alert.EventEnd, ctaDate);
			if((start.diff(now) > 0 && end.diff(now) > 0) || (start.diff(now) > 0 && !alert.EventEnd)){
				promises.push(pushAlertToDatabase(alert));
			}
		});
		promise.allSettled(promises).then(function(){
			console.log('alerts done');
			prepared.resolve(true);
		}).done();
	}); //clear alerts from db
	return prepared.promise;
};

var processAlerts = function(xml){
	var parsed = promise.defer();
	parser.parseString(xml, function (err, json) {
		parsed.resolve(prepareAlertsForDatabase(json));
	});
	return parsed.promise;
};

var getDataFromCTA = function(type){
	var done = promise.defer();
	request('http://www.transitchicago.com/api/1.0/routes.aspx?type=' + type,
		function (err, res, xml) {
			if (!err && res.statusCode === 200) {
				processRoutes(xml, type).then(function(){
					done.resolve(true);
				});
			}else{
				console.log(err);
				process.exit(1);
			}
		}
	);	
	return done.promise;
};

var getAlertsFromCTA = function(){
	var done = promise.defer();
	request('http://www.transitchicago.com/api/1.0/alerts.aspx', function (err, res, xml) {
		if (!err && res.statusCode === 200) {
			processAlerts(xml).then(function(){
				done.resolve(true);
			});
		}else{
			console.log(err);
			process.exit(1);
		}
	});
	return done.promise;
};

promise.allSettled([
	getDataFromCTA('station'),
	getDataFromCTA('bus'),
	getDataFromCTA('rail'),
	getAlertsFromCTA()
]).then(function(results){
	console.log('update-db: settled');
	mongoose.connection.close(function () {
		console.log('update-db: complete');
		process.exit(0);
	});
}).done();


