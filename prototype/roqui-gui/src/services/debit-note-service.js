import http from '@/services/http'

const debitNoteService = {}

debitNoteService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/debit/note/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(),
  )
}

debitNoteService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/debit/note/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

debitNoteService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/debit/note/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

debitNoteService.authorize = async (token, code, number) => {
  return await client.post(`/debit/note/authorize`, { code, number })
}

export default debitNoteService
