import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

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
  return await client.post(`/liquidation/authorize`, { code, number }, headerAuthorization(token))
}

export default liquidationService
