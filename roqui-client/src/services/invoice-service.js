import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const invoiceService = {}

invoiceService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/invoice/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(token),
  )
}

invoiceService.authorizeAll = async (token, startDate, endDate) => {
  return await client.get(
    `/invoice/authorize/dates/${startDate}/${endDate}`,
    headerAuthorization(token),
  )
}

invoiceService.checkAll = async (token, startDate, endDate) => {
  return await client.get(
    `/invoice/check/dates/${startDate}/${endDate}`,
    headerAuthorization(token),
  )
}

invoiceService.authorize = async (token, code, number) => {
  return await client.post(`/invoice/authorize`, { code, number }, headerAuthorization(token))
}

export default invoiceService
