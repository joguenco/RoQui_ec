import http from '@/services/http'

const withholdService = {}

withholdService.find = async (token, startDate, endDate, status) => {
  return await client.get(
    `/withhold/report/dates/${startDate}/${endDate}/status/${status}`,
    headerAuthorization(),
  )
}

withholdService.authorizeAll = async (token, startDate, endDate) => {
  return await client.post(
    `/withhold/authorize/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

withholdService.checkAll = async (token, startDate, endDate) => {
  return await client.post(
    `/withhold/check/dates/${startDate}/${endDate}`,
    {},
    headerAuthorization(),
  )
}

withholdService.authorize = async (token, code, number) => {
  return await client.post(`/withhold/authorize`, { code, number })
}

export default withholdService
