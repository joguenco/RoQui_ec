import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const emailClientHttpService = {}
const emailClientSmtpService = {}

emailClientHttpService.getConfiguration = async (token) => {
  return await client.get('/email/client/http', headerAuthorization(token))
}

emailClientHttpService.update = async (token, url, securityToken) => {
  return await client.post(
    '/email/client/http/update',
    {
      url: url,
      token: securityToken,
    },
    headerAuthorization(token),
  )
}

emailClientSmtpService.getConfiguration = async (token) => {
  return await client.get('/email/client/smtp', headerAuthorization(token))
}

emailClientSmtpService.update = async (token, url, port, account, password) => {
  return await client.post(
    '/email/client/smtp/update',
    {
      url: url,
      port: port,
      account: account,
      password: password,
    },
    headerAuthorization(token),
  )
}

export { emailClientHttpService, emailClientSmtpService }
