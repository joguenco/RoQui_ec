import http from '@/services/http'

const emailClientHttpService = {}
const emailClientSmtpService = {}
const emailService = {}

emailClientHttpService.getConfiguration = async () => {
  return await client.get('/email/client/http')
}

emailClientHttpService.update = async (token, url, securityToken) => {
  return await client.post(
    '/email/client/http/update',
    {
      url: url,
      token: securityToken,
    },
    headerAuthorization(),
  )
}

emailClientSmtpService.getConfiguration = async () => {
  return await client.get('/email/client/smtp')
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
    headerAuthorization(),
  )
}

emailService.send = async (token, code, number) => {
  return await client.post(`/email/send`, { code, number })
}

emailService.sendTest = async (token, address) => {
  return await client.post(`/email/send/test`, { address })
}

export { emailClientHttpService, emailClientSmtpService, emailService }
