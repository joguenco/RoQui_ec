import http from '@/services/http'

const parameterService = {}

parameterService.getBaseDirectory = async () => {
  return await http.get('/roqui/v1/parameter/base/directory')
}

parameterService.setBaseDirectory = async (baseDirectory) => {
  return await http.post('/roqui/v1/parameter/base/directory', {
    baseDirectory,
  })
}

parameterService.getLogoJpeg = async () => {
  const config = {
    responseType: 'blob',
  }
  return await http.get('/roqui/v1/resource/logo/jpeg', config)
}

parameterService.uploadLogoJpeg = async (formData) => {
  return await http.post('/roqui/v1/resource/logo/jpeg/load', formData)
}

parameterService.uploadLogoPng = async (formData) => {
  return await http.post('/roqui/v1/resource/logo/png/load', formData)
}

parameterService.uploadEmailTemplateHtml = async (formData) => {
  return await http.post('/roqui/v1/resource/template/html/load', formData)
}

parameterService.getResource = async (name) => {
  return await http.get(`/roqui/v1/resource/name?name=${name}`)
}

export default parameterService
