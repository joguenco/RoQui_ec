import client from '@/services/client'

const certificateService = {}

certificateService.getCertificate = async (token) => {
  return await client.get('/certificate', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

certificateService.loadCertificate = async (token, formData) => {
  return await client.post('/certificate/load', formData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

export default certificateService
