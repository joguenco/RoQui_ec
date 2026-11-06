import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const debitNoteService = {}

debitNoteService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/debit/note/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(token),
  )
}

debitNoteService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/debit/note/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

debitNoteService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/debit/note/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

debitNoteService.authorize = async (token, code, number) => {
  return await client.post(`/debit/note/authorize`, { code, number }, headerAuthorization(token))
}

export default debitNoteService
