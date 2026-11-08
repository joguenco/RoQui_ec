import http from '@/services/http'

const certificateService = {}

certificateService.getCertificate = async () => {
  return await http.get('/roqui/v1/certificate')
}

certificateService.getDaysToExpire = async () => {
  return await http.get('/roqui/v1/certificate/days-to-expiry')
}

certificateService.uploadCertificate = async (formData) => {
  return await http.post('/roqui/v1/certificate/load', formData)
}

export default certificateService
