import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const withholdService = {}

withholdService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/withhold/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(token),
  )
}

withholdService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/withhold/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

withholdService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/withhold/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(token),
  )
}

withholdService.authorize = async (token, code, number) => {
  return await client.post(`/withhold/authorize`, { code, number }, headerAuthorization(token))
}

export default withholdService
