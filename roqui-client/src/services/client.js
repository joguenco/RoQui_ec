import configService from '@/services/config'
import axios from 'axios'

const client = axios.create({
  baseURL: configService.API_URL,
})

export default client
