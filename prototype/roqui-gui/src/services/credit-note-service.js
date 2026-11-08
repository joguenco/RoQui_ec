import http from '@/services/http'

const creditNoteService = {}

creditNoteService.find = async (startDate, endDate, status) => {
  return await client.get(`/credit/note/report/dates/${startDate}/${endDate}/status/${status}`)
}

creditNoteService.authorizeAll = async (startDate, endDate) => {
  return await client.post(`/credit/note/authorize/dates/${startDate}/${endDate}`, {})
}

creditNoteService.checkAll = async (startDate, endDate) => {
  return await client.post(`/credit/note/check/dates/${startDate}/${endDate}`, {})
}

creditNoteService.authorize = async (code, number) => {
  return await client.post(`/credit/note/authorize`, { code, number })
}

export default creditNoteService
