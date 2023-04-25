const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack');
module.exports ={
  transpileDependencies: true,
  lintOnSave: false,
  
  transpileDependencies: [
    'vuetify'
  ],
  configureWebpack: {
    plugins: [
      new webpack.optimize.LimitChunkCountPlugin({
        maxChunks: 1,
      }),
    ],
    resolve: {
      fallback: {
        "zlib": require.resolve("browserify-zlib"),
        "querystring": require.resolve("querystring-es3"),
        "path": require.resolve("path-browserify"),
        "crypto": require.resolve("crypto-browserify"),
        "stream": require.resolve("stream-browserify"),
        assert: require.resolve('assert/'),
        util: require.resolve('util/'),
        http: require.resolve('stream-http'),
        url: require.resolve('url/'),
        fs: false
      }
    }
  },
  devServer: {

    proxy:{
      "/v8/":{
        target: "https://query1.finance.yahoo.com",
        ws: true,
        secure: false 
      }
    }
    
     },
     
}


