import http from '@/services/http'

const BASE_URL = import.meta.env.VITE_API_URL
const documentService = {}

documentService.find = async (code, number) => {
  return await http.get(`/roqui/v1/document/code/${code}/number/${number}`)
}

documentService.pdf = function () {
  return `${BASE_URL}/files/pdf`
}

documentService.xml = function () {
  return `${BASE_URL}/files/xml`
}

export default documentService
