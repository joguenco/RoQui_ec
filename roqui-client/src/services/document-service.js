import client from '@/services/client'
import headerAuthorization from '@/services/header-authorization'

const documentService = {}

documentService.find = async (token, code, number) => {
  return await client.get(`/document/code/${code}/number/${number}`, headerAuthorization(token))
}

export default documentService
