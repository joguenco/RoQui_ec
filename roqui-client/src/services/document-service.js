import { BASE_URL } from '@/services/config'
import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const documentService = {}

documentService.find = async (token, code, number) => {
  return await client.get(`/document/code/${code}/number/${number}`, headerAuthorization(token))
}

documentService.pdf = function () {
  return `${BASE_URL}/files/pdf`
}

documentService.xml = function () {
  return `${BASE_URL}/files/xml`
}

export default documentService
