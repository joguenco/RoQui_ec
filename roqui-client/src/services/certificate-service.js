import client from '@/services/client'

const certificateService = {}

certificateService.getCertificate = async (token) => {
  return await client.get('/certificate', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

export default certificateService
