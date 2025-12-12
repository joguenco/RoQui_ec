import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const assetService = {}

assetService.getEnvironment = async (token) => {
  return await client.get('/asset/environment', headerAuthorization(token))
}

export default assetService
