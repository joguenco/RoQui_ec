import moment from 'moment'

const validate = {}

validate.isDate = (date) => {
  return moment(date, 'YYYY-MM-DD', true).isValid()
}

export default validate
