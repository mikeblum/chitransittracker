var express = require('express'),
    path = require('path'),
    http = require('http'),
    api = require('./routes');

var app = express();

app.configure(function () {
    app.set('port', process.env.PORT || 3000);
    app.use(express.logger('dev'));  /* 'default', 'short', 'tiny', 'dev' */
    app.use(express.bodyParser()),
    app.use(express.static(path.join(__dirname, 'public')));
});

app.get('/routes', api.routes);
app.get('/alerts', api.alerts);