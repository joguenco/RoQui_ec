import { BASE_URL } from '@/services/config'
import http from '@/services/http'

const documentService = {}

documentService.find = async (token, code, number) => {
  return await client.get(`/document/code/${code}/number/${number}`)
}

documentService.pdf = function () {
  return `${BASE_URL}/files/pdf`
}

documentService.xml = function () {
  return `${BASE_URL}/files/xml`
}

export default documentService
