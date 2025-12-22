import Vue from 'vue'
import VueRouter from 'vue-router';
import MainScreen from '../views/MainScreen.vue'
import Login from "@/views/Login.vue";
import store from "../store"

Vue.use(VueRouter)

const routes = [
	{
		path: '/login',
		name: 'login',
		component: Login,
		meta: { requiresAuth: false }
	},
	{
		path: '/',
		name: 'main',
		component: MainScreen,
		meta: { requiresAuth: true }
	}
]

const router = new VueRouter({
	routes
})

router.beforeEach(async (to, from, next) => {
	if (to.meta.requiresAuth) {
		const isAuthenticated = await store.dispatch('checkAuth');

		if (!isAuthenticated) {
			next('/login');
		} else {
			next('/');
		}
	} else {
		if (to.name === 'login') {
			const isAuthenticated = await store.dispatch('checkAuth');
			if (isAuthenticated) {
				next('/');
				return;
			}
		}
		next();
	}
});

export default router
