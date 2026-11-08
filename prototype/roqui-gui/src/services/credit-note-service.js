import http from '@/services/http'

const creditNoteService = {}

creditNoteService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/credit/note/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(),
  )
}

creditNoteService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/credit/note/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

creditNoteService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/credit/note/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

creditNoteService.authorize = async (token, code, number) => {
  return await client.post(`/credit/note/authorize`, { code, number })
}

export default creditNoteService
