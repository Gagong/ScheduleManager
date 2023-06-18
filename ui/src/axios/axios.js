import axios from 'axios'

export const AXIOS = axios.create({
	baseURL: `http://localhost:8081/api`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})

export const DICTIONARY_API = axios.create({
	baseURL: `http://localhost:8081/api/dictionary`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})

export const SCHEDULE_API = axios.create({
	baseURL: `http://localhost:8081/api/schedule`,
	headers: {
		'Access-Control-Allow-Origin': 'http://localhost:8080'
	}
})