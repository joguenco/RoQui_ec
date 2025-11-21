import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const certificateService = {}

certificateService.getCertificate = async (token) => {
  return await client.get('/certificate', headerAuthorization(token))
}

certificateService.getDaysToExpire = async (token) => {
  return await client.get('/certificate/days-to-expiry', headerAuthorization(token))
}

certificateService.uploadCertificate = async (token, formData) => {
  return await client.post('/certificate/load', formData, headerAuthorization(token))
}

export default certificateService
