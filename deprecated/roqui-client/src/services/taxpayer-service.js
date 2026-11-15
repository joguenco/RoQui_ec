import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const taxpayerService = {}

taxpayerService.getTaxpayer = async (token) => {
  return await client.get('/taxpayer', headerAuthorization(token))
}

export default taxpayerService
