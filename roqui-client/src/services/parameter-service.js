import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const parameterService = {}

parameterService.getBaseDirectory = async (token) => {
  return await client.get('/parameter/base/directory', headerAuthorization(token))
}

parameterService.setBaseDirectory = async (token, baseDirectory) => {
  return await client.post(
    '/parameter/base/directory',
    {
      baseDirectory,
    },
    headerAuthorization(token),
  )
}

export default parameterService
