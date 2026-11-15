import http from '@/services/http'

const debitNoteService = {}

debitNoteService.find = async (startDate, endDate, status) => {
  return await http.get(
    `/roqui/v1/debit/note/report/dates/${startDate}/${endDate}/status/${status}`,
  )
}

debitNoteService.authorizeAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/debit/note/authorize/dates/${startDate}/${endDate}`, {})
}

debitNoteService.checkAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/debit/note/check/dates/${startDate}/${endDate}`, {})
}

debitNoteService.authorize = async (code, number) => {
  return await http.post(`/roqui/v1/debit/note/authorize`, { code, number })
}

export default debitNoteService
