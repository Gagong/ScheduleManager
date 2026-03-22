import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from './router/router'
import WebSocketPlugin from './plugins/websocket';
import store from './store/store'

Vue.config.productionTip = false

Vue.use(WebSocketPlugin, {
	autoConnect: true,  // автоматическое подключение при старте
	url: process.env.VUE_APP_WS_URL || 'http://localhost:8081/ws'  // URL для подключения
});

new Vue({
	vuetify,
	router,
	store,
	render: h => h(App)
}).$mount('#app')
