const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
	transpileDependencies: [
		'vuetify'
	],

	devServer: {
		webSocketServer: false,

		proxy: {
			'/api': {
				target: 'http://localhost:8081',
				ws: false,
				changeOrigin: true,
			},
			'/ws': {
				target: 'http://localhost:8081',
				ws: true,
				changeOrigin: true,
			},
			'/wss': {
				target: 'http://localhost:8081',
				ws: true,
				changeOrigin: true,
			}
		},
		client: {
			webSocketURL: {
				hostname: 'localhost',
				port: 8081,
				pathname: '/ws',
				protocol: 'ws'
			}
		}
	},

	assetsDir: 'static',
	publicPath: '/schedule',
	outputDir: 'build'
})
