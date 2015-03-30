var webpack = require('webpack');

module.exports = {
  context: __dirname,
  entry: [
    'webpack-dev-server/client?http://0.0.0.0:3000',
    'webpack/hot/only-dev-server',
    './js/index' // your entry point
  ],
  output: { path: __dirname + '/public', publicPath: '/js/' },
  plugins: [ new webpack.HotModuleReplacementPlugin(), new webpack.NoErrorsPlugin() ],
  module: {
    loaders: [ { test: /\.js$/, exclude: /node_modules/, loaders: [ 'react-hot', 'jsx?harmony' ] } ]
  }
};
