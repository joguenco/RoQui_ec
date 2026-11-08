import http from '@/services/http'

const liquidationService = {}

liquidationService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/liquidation/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(token),
  )
}

liquidationService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/liquidation/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

liquidationService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/liquidation/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

liquidationService.authorize = async (token, code, number) => {
  return await client.post(`/liquidation/authorize`, { code, number })
}

export default liquidationService
