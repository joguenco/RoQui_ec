import http from '@/services/http'

const documentService = {}

documentService.find = async (code, number) => {
  return await http.get(`/roqui/v1/document/code/${code}/number/${number}`)
}

const saveBlob = (data, filename, type) => {
  const url = URL.createObjectURL(new Blob([data], { type }))
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

documentService.downloadPdf = async (accessKey) => {
  const response = await http.get(`/files/pdf/${accessKey}`, { responseType: 'blob' })
  saveBlob(response.data, `${accessKey}.pdf`, 'application/pdf')
}

documentService.downloadXml = async (accessKey) => {
  const response = await http.get(`/files/xml/${accessKey}`, { responseType: 'blob' })
  saveBlob(response.data, `${accessKey}.xml`, 'application/xml')
}

export default documentService
