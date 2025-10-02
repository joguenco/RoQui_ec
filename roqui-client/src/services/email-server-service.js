import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const emailServerService = {}

emailServerService.getConfiguration = async (token) => {
  return await client.get('/email/server', headerAuthorization(token))
}

emailServerService.update = async (token, url, securityToken) => {
  return await client.post(
    '/email/server/update',
    {
      url: url,
      token: securityToken,
    },
    headerAuthorization(token),
  )
}

export default emailServerService
