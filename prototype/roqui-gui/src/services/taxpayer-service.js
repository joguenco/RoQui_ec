import http from '@/services/http'

const taxpayerService = {}

taxpayerService.getTaxpayer = async () => {
  return await http.get('/roqui/v1/taxpayer')
}

taxpayerService.getEstablishments = async () => {
  return await http.get('/roqui/v1/establishments')
}

export default taxpayerService
