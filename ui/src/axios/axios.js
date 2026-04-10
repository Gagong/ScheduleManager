import axios from 'axios'
import store from '../store/store'
import mitt from 'mitt'

export const emitter = mitt()

const baseConfig = {
	baseURL: process.env.VUE_APP_API_URL || 'http://localhost:8081/api',
	headers: {
		'Content-Type': 'application/json',
		'Accept': 'application/json'
	}
}

function createApiInstance(endpoint) {
	const instance = axios.create({
		...baseConfig,
		baseURL: baseConfig.baseURL + endpoint
	})

	instance.interceptors.request.use(
		(config) => {
			const token = store.getters.getAuthHeader
			if (token) {
				config.headers.Authorization = token
			}
			return config
		},
		(error) => {
			emitter.emit('api-error', {
				message: error.message,
				type: 'request'
			})
			return Promise.reject(error)
		}
	)

	instance.interceptors.response.use(
		(response) => response,
		(error) => {
			const errorInfo = {
				status: error.response?.status,
				statusText: error.response?.statusText,
				message: error.response?.data?.message || error.message,
				url: error.config?.url,
				method: error.config?.method?.toUpperCase(),
				timestamp: new Date().toISOString(),
				data: error.response?.data
			}

			emitter.emit('api-error', errorInfo)
			return Promise.reject(error)
		}
	)

	return instance
}

export function makeDownloadAction(response, defaultFileName) {
	let filename = defaultFileName
	const contentDisposition = response.headers.get('content-disposition');
	if (contentDisposition) {
		const match = contentDisposition.match(/filename\*=UTF-8''(.+?)(?:;|$)/)
		if (match) {
			filename = decodeURIComponent(match[1])
		} else {
			const match2 = contentDisposition.match(/filename="?(.+?)"?$/i)
			if (match2) {
				filename = match2[1]
			}
		}
	}
	const url = window.URL.createObjectURL(response.data)
	const link = document.createElement('a')
	link.href = url
	link.setAttribute('download', filename)
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
	window.URL.revokeObjectURL(url)
}

export const EMPLOYEE_API = createApiInstance('/employee')
export const DICTIONARY_API = createApiInstance('/dictionary')
export const SCHEDULE_API = createApiInstance('/schedule')
export const PROFESSOR_API = createApiInstance('/professor')
export const PROFILE_API = createApiInstance('/profile')