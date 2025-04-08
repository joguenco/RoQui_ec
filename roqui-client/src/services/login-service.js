import client from '@/services/client'

const loginService = {}

loginService.login = async (username, password) => {
  return await client.post('/login', { username, password })
}

export default loginService
