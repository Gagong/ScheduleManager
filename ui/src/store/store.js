import Vue from 'vue'
import Vuex from 'vuex'
import {LOGIN_API} from "@/axios/axios";

Vue.use(Vuex)

export default new Vuex.Store({
	state: {
		username: localStorage.getItem('username') || '',
		password: localStorage.getItem('password') || '',
		email: localStorage.getItem('email') || '',
		enabled: localStorage.getItem('enabled') || '',
		firstName: localStorage.getItem('firstName') || '',
		lastName: localStorage.getItem('lastName') || '',
		middleName: localStorage.getItem('middleName') || '',
		fullName: localStorage.getItem('fullName') || '',
		id: localStorage.getItem('id') || '',
		roles: localStorage.getItem('roles') || [],
		isAuthenticated: false,
	},
	mutations: {
		SET_CREDENTIALS(state, { username, password }) {
			state.username = username;
			state.password = password;
			localStorage.setItem('username', username);
			localStorage.setItem('password', password);
		},
		SET_AUTHENTICATED(state, value) {
			state = state && value
			localStorage.setItem('email', value.email);
			localStorage.setItem('enabled', value.enabled);
			localStorage.setItem('firstName', value.firstName);
			localStorage.setItem('lastName', value.lastName);
			localStorage.setItem('middleName', value.middleName);
			localStorage.setItem('fullName', value.fullName);
			localStorage.setItem('id', value.id);
			localStorage.setItem('roles', value.roles);
			state.isAuthenticated = true;
		},
		CLEAR_CREDENTIALS(state) {
			state.username = '';
			state.password = '';
			state.email = '';
			state.enabled = '';
			state.firstName = '';
			state.lastName = '';
			state.middleName = '';
			state.fullName = '';
			state.id = '';
			state.roles = [];
			state.isAuthenticated = false;

			localStorage.removeItem('username');
			localStorage.removeItem('password');
			localStorage.removeItem('email');
			localStorage.removeItem('enabled');
			localStorage.removeItem('firstName');
			localStorage.removeItem('lastName');
			localStorage.removeItem('middleName');
			localStorage.removeItem('fullName');
			localStorage.removeItem('id');
			localStorage.removeItem('roles');
		},
	},
	actions: {
		// eslint-disable-next-line no-unused-vars
		async login({ commit, state }, { username, password }) {
			try {
				commit('SET_CREDENTIALS', { username, password });
				const body = {
					username: username,
					password: password
				}

				const response = await LOGIN_API.post('login', body);
				commit('SET_AUTHENTICATED', response.data)

				return { success: true };
			} catch (error) {
				commit('CLEAR_CREDENTIALS');
				return {
					success: false,
					error: error.response?.data?.message || 'Данные для входа не верны'
				};
			}
		},

		logout({ commit }) {
			commit('CLEAR_CREDENTIALS');
		},

		async checkAuth({ state, commit }) {
			if (!state.username || !state.password) {
				commit('CLEAR_CREDENTIALS');
				return false;
			}

			try {
				const body = {
					username: state.username,
					password: state.password
				}
				const response = await LOGIN_API.post('/login', body);
				commit('SET_AUTHENTICATED', response.data);
				return true;
			} catch (error) {
				commit('CLEAR_CREDENTIALS');
				return false;
			}
		},
	},
	getters: {
		getAuthHeader: (state) => {
			if (state.username && state.password) {
				return 'Basic ' + btoa(`${state.username}:${state.password}`);
			}
			return null;
		},
		isAuthenticated: (state) => state.isAuthenticated,
		username: (state) => state.username,
		fullName: (state) => state.fullName,
	},
})
