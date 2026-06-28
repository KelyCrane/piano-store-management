module.exports = {
  lintOnSave: false,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8088',
        changeOrigin: true
      },
      '/ws': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        ws: true
      }
    }
  }
}
