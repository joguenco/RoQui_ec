import http from '@/services/http'

const withholdService = {}

withholdService.find = async (startDate, endDate, status) => {
  return await http.get(`/withhold/report/dates/${startDate}/${endDate}/status/${status}`)
}

withholdService.authorizeAll = async (startDate, endDate) => {
  return await http.post(`/withhold/authorize/dates/${startDate}/${endDate}`, {})
}

withholdService.checkAll = async (startDate, endDate) => {
  return await http.post(`/withhold/check/dates/${startDate}/${endDate}`, {})
}

withholdService.authorize = async (code, number) => {
  return await http.post(`/withhold/authorize`, { code, number })
}

export default withholdService
