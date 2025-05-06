import client from '@/services/client'

const parameterService = {}

parameterService.getBaseDirectory = async (token) => {
  return await client.get('/parameter/base/directory', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

parameterService.setBaseDirectory = async (token, baseDirectory) => {
  return await client.post(
    '/parameter/base/directory',
    {
      baseDirectory,
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )
}

export default parameterService
