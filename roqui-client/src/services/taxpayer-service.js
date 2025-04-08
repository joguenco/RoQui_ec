import client from '@/services/client'

const taxpayerService = {}

taxpayerService.getTaxpayer = async (token) => {
  return await client.get('/taxpayer', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

export default taxpayerService
