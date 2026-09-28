import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const establishmentService = {}

establishmentService.getEstablishments = async (token) => {
  return await client.get('/establishments', headerAuthorization(token))
}

export default establishmentService
