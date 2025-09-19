import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const creditNoteService = {}

creditNoteService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/credit/note/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(token),
  )
}

creditNoteService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/credit/note/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

creditNoteService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/credit/note/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

creditNoteService.authorize = async (token, code, number) => {
  return await client.post(`/credit/note/authorize`, { code, number }, headerAuthorization(token))
}

export default creditNoteService
