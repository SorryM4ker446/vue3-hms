import axios, { type InternalAxiosRequestConfig } from 'axios'
import {
  clearAuthSession,
  getAccessToken,
  getRefreshToken,
  setAccessToken,
  setRefreshToken
} from '@/utils/auth'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

interface RetryableRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean
}

const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000
})

let refreshPromise: Promise<string | null> | null = null

function redirectToLogin() {
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    return null
  }

  if (!refreshPromise) {
    refreshPromise = axios
      .post(
        `${BASE_URL}/refresh`,
        {},
        {
          headers: {
            'X-Refresh-Token': refreshToken
          }
        }
      )
      .then((res) => {
        if (!res.data?.success || !res.data?.accessToken) {
          clearAuthSession()
          return null
        }

        setAccessToken(res.data.accessToken)
        if (res.data.refreshToken) {
          setRefreshToken(res.data.refreshToken)
        }
        return res.data.accessToken as string
      })
      .catch(() => {
        clearAuthSession()
        redirectToLogin()
        return null
      })
      .finally(() => {
        refreshPromise = null
      })
  }

  return refreshPromise
}

apiClient.interceptors.request.use((config) => {
  const token = getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as RetryableRequestConfig | undefined
    const status = error.response?.status
    const requestUrl = originalRequest?.url || ''

    if (
      status !== 401 ||
      !originalRequest ||
      originalRequest._retry ||
      requestUrl.includes('/login') ||
      requestUrl.includes('/refresh')
    ) {
      return Promise.reject(error)
    }

    originalRequest._retry = true
    const newAccessToken = await refreshAccessToken()

    if (!newAccessToken) {
      clearAuthSession()
      redirectToLogin()
      return Promise.reject(error)
    }

    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
    return apiClient(originalRequest)
  }
)

export default apiClient
