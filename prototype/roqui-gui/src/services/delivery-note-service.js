import http from '@/services/http'

const deliveryNoteService = {}

deliveryNoteService.find = async (startDate, endDate, status) => {
  return await http.get(`/delivery-note/report/dates/${startDate}/${endDate}/status/${status}`)
}

deliveryNoteService.authorizeAll = async (startDate, endDate) => {
  return await http.post(`/delivery-note/authorize/dates/${startDate}/${endDate}`, {})
}

deliveryNoteService.checkAll = async (startDate, endDate) => {
  return await http.post(`/delivery-note/check/dates/${startDate}/${endDate}`, {})
}

deliveryNoteService.authorize = async (code, number) => {
  return await http.post(`/delivery-note/authorize`, { code, number })
}

export default deliveryNoteService
