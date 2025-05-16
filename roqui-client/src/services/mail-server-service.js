import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const mailServerService = {}

mailServerService.getInformation = async (token) => {
  return await client.get('/mail/server', headerAuthorization(token))
}

mailServerService.uploadCertificate = async (token, formData) => {
  return await client.post('/certificate/load', formData, headerAuthorization(token))
}

export default mailServerService
