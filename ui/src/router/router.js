import Vue from 'vue'
import VueRouter from 'vue-router';
import store from "../store/store"
import Professor from "@/views/components/Professor.vue";
import GeneralSchedule from "@/views/components/GeneralSchedule.vue";
import CreateSchedule from "@/views/components/CreateSchedule.vue";
import Dictionaries from "@/views/components/Dictionaries.vue";
import Login from "@/views/components/Login.vue";

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
		name: 'generalSchedule',
		component: GeneralSchedule,
		meta: { requiresAuth: false }
	},
	{
		path: '/dictionary',
		name: 'dictionary',
		component: Dictionaries,
		meta: { requiresAuth: true }
	},
	{
		path: '/professor',
		name: 'professor',
		component: Professor,
		meta: { requiresAuth: true }
	},
	{
		path: '/create-schedule',
		name: 'createSchedule',
		component: CreateSchedule,
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
			next('/');
		} else {
			next();
		}
	} else {
		if (to.name === 'login') {
			const isAuthenticated = await store.dispatch('checkAuth');
			if (isAuthenticated) {
				next();
				return;
			}
		}
		next();
	}
});

export default router
