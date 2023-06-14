const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
	transpileDependencies: [
		'vuetify'
	],

	devServer: {
		proxy: {
			'/api': {
				target: 'http://localhost:8081',
				ws: true,
				changeOrigin: true,
			}
		}
	},

	assetsDir: 'static',
	publicPath: '/schedule',
	outputDir: 'build'
})
