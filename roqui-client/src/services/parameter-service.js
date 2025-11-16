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

parameterService.getLogoJpeg = async (token) => {
  const config = {
    headers: { Authorization: `Bearer ${token}` },
    responseType: 'blob',
  }
  return await client.get('/resource/logo/jpeg', config)
}

parameterService.uploadLogoJpeg = async (token, formData) => {
  return await client.post('/resource/logo/jpeg/load', formData, headerAuthorization(token))
}

parameterService.uploadLogoPng = async (token, formData) => {
  return await client.post('/resource/logo/png/load', formData, headerAuthorization(token))
}

parameterService.uploadEmailTemplateHtml = async (token, formData) => {
  return await client.post('/resource/template/html/load', formData, headerAuthorization(token))
}

parameterService.getResource = async (token, name) => {
  return await client.get(`/resource/name?name=${name}`, headerAuthorization(token))
}

export default parameterService
