"use strict";
var xml2js = require('xml2js'),
    request = require('request');

exports.alerts = function(req, res){
    request('http://www.transitchicago.com/api/1.0/alerts.aspx', function (error, response, body) {
        if (!error && response.statusCode === 200) {
            var parseString = xml2js.parseString;
            var xml = body;
            parseString(xml, function (err, result) {
                res.send(result);
            });
        }
    });
};

exports.routes = function(req, res){
    request('http://www.transitchicago.com/api/1.0/routes.aspx', function (error, response, body) {
        if (!error && response.statusCode === 200) {
            var parseString = xml2js.parseString;
            var xml = body;
            parseString(xml, function (err, result) {
                res.send(result);
            });
        }
    });
};