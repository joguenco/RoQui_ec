import http from '@/services/http'

const emailClientHttpService = {}
const emailClientSmtpService = {}
const emailService = {}

emailClientHttpService.getConfiguration = async () => {
  return await http.get('/roqui/v1/email/client/http')
}

emailClientHttpService.update = async (url, securityToken) => {
  return await http.post('/roqui/v1/email/client/http/update', {
    url: url,
    token: securityToken,
  })
}

emailClientSmtpService.getConfiguration = async () => {
  return await http.get('/roqui/v1/email/client/smtp')
}

emailClientSmtpService.update = async (url, port, account, password, encryption) => {
  return await http.post('/roqui/v1/email/client/smtp/update', {
    url: url,
    port: port,
    account: account,
    password: password,
    encryption: encryption,
  })
}

emailService.send = async (code, number) => {
  return await http.post(`/roqui/v1/email/send`, { code, number })
}

emailService.sendTest = async (address) => {
  return await http.post(`/roqui/v1/email/send/test`, { address })
}

export { emailClientHttpService, emailClientSmtpService, emailService }
