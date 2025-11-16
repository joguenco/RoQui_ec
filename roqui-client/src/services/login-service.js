import client from '@/services/client'
import configService from '@/services/config'
import axios from 'axios'

const loginService = {}

loginService.login = async (username, password) => {
  return await client.post('/login', { username, password })
}

// Use a separate axios instance for refresh to avoid interceptor adding access token
const refreshClient = axios.create({
  baseURL: configService.API_URL,
})

loginService.refreshToken = async (refreshToken) => {
  return await refreshClient.post('/refresh', { refreshToken })
}

export default loginService
