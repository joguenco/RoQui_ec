import http from '@/services/http'

const deliveryNoteService = {}

deliveryNoteService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/delivery-note/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(),
  )
}

deliveryNoteService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/delivery-note/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

deliveryNoteService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/delivery-note/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

deliveryNoteService.authorize = async (token, code, number) => {
  return await client.post(`/delivery-note/authorize`, { code, number })
}

export default deliveryNoteService
