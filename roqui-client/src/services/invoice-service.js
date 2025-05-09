import client from '@/services/client'

const invoiceService = {}

invoiceService.find = async (token, startDate, endDate) => {
  return await client.get(`/invoice/report/dates/${startDate}/${endDate}/status/Unauthorized`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

export default invoiceService
