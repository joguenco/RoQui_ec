import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const emailService = {}

emailService.send = async (token, code, number) => {
  return await client.post(`/email/send`, { code, number }, headerAuthorization(token))
}

export default emailService
