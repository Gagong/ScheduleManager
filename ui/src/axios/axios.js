import axios from 'axios'
import store from '../store/store'

export const LOGIN_API = axios.create({
	baseURL: `http://localhost:8081/api/login`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})

LOGIN_API.interceptors.request.use(
	successConfig(),
	errorConfig()
);

export const DICTIONARY_API = axios.create({
	baseURL: `http://localhost:8081/api/dictionary`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})

DICTIONARY_API.interceptors.request.use(
	successConfig(),
	errorConfig()
);

export const SCHEDULE_API = axios.create({
	baseURL: `http://localhost:8081/api/schedule`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})

SCHEDULE_API.interceptors.request.use(
	successConfig(),
	errorConfig()
);

export const PROFESSOR_DISCIPLINE_API = axios.create({
	baseURL: `http://localhost:8081/api/professorDiscipline`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})

PROFESSOR_DISCIPLINE_API.interceptors.request.use(
	successConfig(),
	errorConfig()
);

function successConfig() {
	return (config) => {
		const token = store.getters.getAuthHeader;
		if (token) {
			config.headers.Authorization = token;
		}
		return config;
	};
}

function errorConfig() {
	return (error) => {
		return Promise.reject(error);
	};
}