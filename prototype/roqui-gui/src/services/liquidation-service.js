import http from '@/services/http'

const liquidationService = {}

liquidationService.find = async (startDate, endDate, status) => {
  return await client.get(`/liquidation/report/dates/${startDate}/${endDate}/status/${status}`)
}

liquidationService.authorizeAll = async (startDate, endDate) => {
  return await client.post(`/liquidation/authorize/dates/${startDate}/${endDate}`, {})
}

liquidationService.checkAll = async (startDate, endDate) => {
  return await client.post(`/liquidation/check/dates/${startDate}/${endDate}`, {})
}

liquidationService.authorize = async (code, number) => {
  return await client.post(`/liquidation/authorize`, { code, number })
}

export default liquidationService
