import http from '@/services/http'

const creditNoteService = {}

creditNoteService.find = async (startDate, endDate, status) => {
  return await http.get(
    `/roqui/v1/credit/note/report/dates/${startDate}/${endDate}/status/${status}`,
  )
}

creditNoteService.authorizeAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/credit/note/authorize/dates/${startDate}/${endDate}`, {})
}

creditNoteService.checkAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/credit/note/check/dates/${startDate}/${endDate}`, {})
}

creditNoteService.authorize = async (code, number) => {
  return await http.post(`/roqui/v1/credit/note/authorize`, { code, number })
}

export default creditNoteService
