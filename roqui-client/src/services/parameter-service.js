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

parameterService.getLogo = async (token) => {
  const config = {
    headers: { Authorization: `Bearer ${token}` },
    responseType: 'blob',
  }
  return await client.get('/parameter/logo', config)
}

parameterService.uploadLogo = async (token, formData) => {
  return await client.post('/parameter/logo/load', formData, headerAuthorization(token))
}

export default parameterService
