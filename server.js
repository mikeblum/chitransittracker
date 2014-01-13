'use strict';

var url = require('url'),
	request = require('request'),
	xml2js = require('xml2js'),
	mongoose = require('mongoose'),
	_ = require('underscore');

var ctaApiKey = '1c3467a09f364ab58ab65c2d4cf4594a';

var parser = xml2js.Parser({ explicitArray: false });

// Build the connection string
var dbURI = process.env.MONGOLAB_URI ||
  process.env.MONGOHQ_URL || 'mongodb://localhost/test';

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
	'lastUpdated': { type: Date, default: Date.now }
});

var RailRoute = mongoose.model('RailRoute', routeSchema, 'RailRoutes');

var BusRoute = mongoose.model('BusRoute', routeSchema, 'BusRoutes');

var Station = mongoose.model('Station', routeSchema, 'Stations');

var pushRoutesToServer = function(data, type){
	_.each(data.CTARoutes.RouteInfo, function(el){
		var RouteType = type === 'rail' ? RailRoute : (type === 'bus') ? BusRoute : Station;

		el.Route =  el.Route.split('|'); //split route name from physical address

		var route = type === 'rail' ? new RailRoute(el) : (type === 'bus') ? new BusRoute(el) : new Station(el);

		RouteType.findOneAndUpdate({ serviceId: el.ServiceId }, {
			route: el.Route,
			routeColorCode: el.RouteColorCode,
			routeTextColor: el.RouteTextColor,
			serviceId: el.ServiceId,
			routeURL: el.RouteURL,
			routeStatus: el.RouteStatus,
			routeStatusColor: el.RouteStatusColor,
			lastUpdated: route.lastUpdated
		}, { upsert: true }, function (err) {
			if(err){ console.log(err); }
		});
	});
};


var processRoutes = function(xml, response, type){
	parser.parseString(xml, function (err, json) {
		pushRoutesToServer(json, type);
		response.writeHead(200, {'Content-Type': 'application/json'});
		response.end(JSON.stringify(json));
	});
};

var processAlerts = function(xml, response){
	parser.parseString(xml, function (err, json) {
		response.writeHead(200, {'Content-Type': 'application/json'});
		response.end(JSON.stringify(json));
	});
};

//don't push arrivals to db - too slow
var processArrivals = function(xml, response, type){
	parser.parseString(xml, function (err, json) {
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
					processRoutes(xml, response, query.type);
				}
			}
		);
	}else if(path.indexOf('search') !== -1){
		var results = {};
		var regex = query.query;
		Station.find( {
			'route.0': new RegExp(regex, 'i')
		}, function (err, docs){
			results.stations = docs; 
			RailRoute.find({
				route: new RegExp(regex, 'i') 
			}, function (err, docs){
				results.railRoutes = docs;
				BusRoute.find( { $or: [ 
					{ serviceId: new RegExp(regex, 'i') }, 
					{ route: new RegExp(regex, 'i') }
				]}, function (err, docs){
					results.busRoutes = docs;
					response.writeHead(200, {'Content-Type': 'application/json'});
					response.end(JSON.stringify(results));
				});
			});
		});
	}else if(path.indexOf('arrivals') !== -1){
		console.log(query);
		request('http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=' + ctaApiKey + '&mapid=' + query.stop + '&max=4',
			function (err, res, xml) {
				if (!err && res.statusCode === 200) {
					processArrivals(xml, response, query.stop);
				}
			}
		);
	}else if(path.indexOf('alerts') !== -1){
        request('http://www.transitchicago.com/api/1.0/alerts.aspx', function (err, res, xml) {
            if (!err && res.statusCode === 200) {
                processAlerts(xml, response);
            }
        });
	}

};