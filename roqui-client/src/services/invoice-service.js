import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const invoiceService = {}

invoiceService.find = async (token, startDate, endDate) => {
  return await client.get(
    `/invoice/report/dates/${startDate}/${endDate}/status/Unauthorized`,
    headerAuthorization(token),
  )
}

export default invoiceService
