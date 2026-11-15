import http from '@/services/http'

const invoiceService = {}

invoiceService.find = async (startDate, endDate, status) => {
  return await http.get(`/roqui/v1/invoice/report/dates/${startDate}/${endDate}/status/${status}`)
}

invoiceService.authorizeAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/invoice/authorize/dates/${startDate}/${endDate}`, {})
}

invoiceService.checkAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/invoice/check/dates/${startDate}/${endDate}`, {})
}

invoiceService.authorize = async (code, number) => {
  return await http.post(`/roqui/v1/invoice/authorize`, { code, number })
}

export default invoiceService
