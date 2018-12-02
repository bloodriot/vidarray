import express from 'express';
import webpackMiddleware from 'webpack-dev-middleware';
import webpack from 'webpack';
import webpackConfig from './webpack.config.js';
const app = express();
app.use(webpackMiddleware(webpack(webpackConfig)));

app.get('/api1/v1/files/', function (req, res) {
  return res.send('Hello world');
});

app.listen(4000, () => {
  console.log('Listening');
});