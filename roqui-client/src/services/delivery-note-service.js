import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const deliveryNoteService = {}

deliveryNoteService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/delivery-note/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(token),
  )
}

deliveryNoteService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/delivery-note/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

deliveryNoteService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/delivery-note/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

deliveryNoteService.authorize = async (token, code, number) => {
  return await client.post(`/delivery-note/authorize`, { code, number }, headerAuthorization(token))
}

export default deliveryNoteService
