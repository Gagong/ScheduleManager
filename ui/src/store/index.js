import Vue from 'vue'
import Vuex from 'vuex'
import {DICTIONARY_API} from "@/axios/axios";

Vue.use(Vuex)

export default new Vuex.Store({
	state: {
		username: localStorage.getItem('username') || '',
		password: localStorage.getItem('password') || '',
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
			state.isAuthenticated = value;
		},
		CLEAR_CREDENTIALS(state) {
			state.username = '';
			state.password = '';
			state.isAuthenticated = false;
			localStorage.removeItem('username');
			localStorage.removeItem('password');
		},
	},
	actions: {
		// eslint-disable-next-line no-unused-vars
		async login({ commit, state }, { username, password }) {
			try {
				commit('SET_CREDENTIALS', { username, password });

				await DICTIONARY_API.get('/getAll');

				commit('SET_AUTHENTICATED', true);
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
				commit('SET_AUTHENTICATED', false);
				return false;
			}

			try {
				await DICTIONARY_API.get('/getAll');
				commit('SET_AUTHENTICATED', true);
				return true;
			} catch (error) {
				commit('SET_AUTHENTICATED', false);
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
	},
})
