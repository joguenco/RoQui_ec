import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const emailClientHttpService = {}
const emailClientSmtpService = {}
const emailService = {}

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

emailClientSmtpService.update = async (token, url, port, account, password, encryption) => {
  return await client.post(
    '/email/client/smtp/update',
    {
      url: url,
      port: port,
      account: account,
      password: password,
      encryption: encryption,
    },
    headerAuthorization(token),
  )
}

emailService.send = async (token, code, number) => {
  return await client.post(`/email/send`, { code, number }, headerAuthorization(token))
}

emailService.sendTest = async (token, address) => {
  return await client.post(`/email/send/test`, { address }, headerAuthorization(token))
}

export { emailClientHttpService, emailClientSmtpService, emailService }
