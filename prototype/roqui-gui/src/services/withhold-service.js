import http from '@/services/http'

const withholdService = {}

withholdService.find = async (startDate, endDate, status) => {
  return await client.get(`/withhold/report/dates/${startDate}/${endDate}/status/${status}`)
}

withholdService.authorizeAll = async (startDate, endDate) => {
  return await client.post(`/withhold/authorize/dates/${startDate}/${endDate}`, {})
}

withholdService.checkAll = async (startDate, endDate) => {
  return await client.post(`/withhold/check/dates/${startDate}/${endDate}`, {})
}

withholdService.authorize = async (code, number) => {
  return await client.post(`/withhold/authorize`, { code, number })
}

export default withholdService
