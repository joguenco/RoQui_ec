import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const subscriptionService = {}

subscriptionService.getStatus = async (token) => {
  return await client.get('/subscription', headerAuthorization(token))
}

export default subscriptionService
