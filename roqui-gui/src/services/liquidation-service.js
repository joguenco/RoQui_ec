import http from '@/services/http'

const liquidationService = {}

liquidationService.find = async (startDate, endDate, status) => {
  return await http.get(
    `/roqui/v1/liquidation/report/dates/${startDate}/${endDate}/status/${status}`,
  )
}

liquidationService.authorizeAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/liquidation/authorize/dates/${startDate}/${endDate}`, {})
}

liquidationService.checkAll = async (startDate, endDate) => {
  return await http.post(`/roqui/v1/liquidation/check/dates/${startDate}/${endDate}`, {})
}

liquidationService.authorize = async (code, number) => {
  return await http.post(`/roqui/v1/liquidation/authorize`, { code, number })
}

export default liquidationService
