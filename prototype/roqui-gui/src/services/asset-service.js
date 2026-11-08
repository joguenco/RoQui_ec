import http from '@/services/http'

const assetService = {}

assetService.getEnvironment = async () => {
  return await http.get('/roqui/v1/asset/environment')
}

export default assetService
