import client from '@/services/client'

const parameterService = {}

parameterService.getBaseDirectory = async (token) => {
  return await client.get('/parameter/base/directory', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

export default parameterService
