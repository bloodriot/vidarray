const webpack = require('webpack');
const webpackMiddleware = require('webpack-dev-middleware');
const webpackConfig = require('./webpack.config.js');
const express = require('express');
const app = express();
const httpProxy = require('http-proxy');
const apiProxy = httpProxy.createProxyServer();

app.use(webpackMiddleware(webpack(webpackConfig)));

app.all("/api/*", function(req, res) {
  apiProxy.web(req, res, {target: 'http://localhost:9098/'});
});

const server = require('http').createServer(app);
server.on('upgrade', function (req, socket, head) {
  apiProxy.ws(req, socket, head, {target: frontend});
});

app.listen(3000, () => {
  console.log('Listening');
});