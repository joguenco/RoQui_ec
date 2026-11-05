import http from '@/services/http'

const taxpayerService = {}

taxpayerService.getTaxpayer = async function () {
  const { data } = await http.get('/roqui/v1/taxpayer')
  return data
}

export default taxpayerService
