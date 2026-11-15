<template>
  <div class="table-container" v-if="rows.length > 0">
    <table class="table is-fullwidth">
      <thead>
        <tr>
          <th>Fecha</th>
          <th>Tipo</th>
          <th>Detalle</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in rows" :key="index">
          <td class="fecha">{{ row.date }}</td>
          <td>
            <span v-if="row.tag" :class="['tag', row.tag]">{{ row.type }}</span>
          </td>
          <td class="detalle">{{ row.detail }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
// RoQui guarda el historial en un solo texto (ElectronicDocument.kt). Cada
// respuesta del SRI empieza con "fecha |" y lleva sus mensajes, y al autorizar
// se pone delante "numero fechaISO". Va de la mas nueva a la mas vieja.
const EVENT_DATE = /(\d{4}-\d{2}-\d{2} \d{2}:\d{2}) \|/g
const AUTHORIZATION = /(\d{49}|null) \d{4}-\d{2}-\d{2}T\S+\s*$/
const MESSAGE = /(ERROR|ADVERTENCIA|INFORMATIVO) (\d+|null): /g
const TAGS = {
  AUTORIZADO: 'is-success',
  ERROR: 'is-danger',
  ADVERTENCIA: 'is-warning',
  INFORMATIVO: 'is-info',
}

function clean(text) {
  return text.replaceAll('|', ' ').replace(/\s+/g, ' ').trim()
}

// Las filas de una respuesta: su autorizacion si la tuvo, y sus mensajes.
function eventRows(date, authorization, content) {
  const rows = []
  if (authorization && authorization[1] !== 'null') {
    rows.push({ date, type: 'AUTORIZADO', tag: TAGS.AUTORIZADO, detail: authorization[1] })
  }

  const messages = [...content.matchAll(MESSAGE)]
  const before = clean(messages.length > 0 ? content.slice(0, messages[0].index) : content)
  if (before !== '' || (messages.length === 0 && rows.length === 0)) {
    rows.push({ date, type: '', tag: null, detail: before || 'Sin mensajes del SRI' })
  }

  messages.forEach((message, index) => {
    const end = index + 1 < messages.length ? messages[index + 1].index : content.length
    // la informacion adicional vacia llega como "- null"
    const text = clean(content.slice(message.index + message[0].length, end)).replace(
      / - null$/,
      '',
    )
    const code = message[2]
    rows.push({
      date,
      type: message[1],
      tag: TAGS[message[1]],
      detail: code === 'null' ? text : `${code}: ${text}`,
    })
  })
  return rows
}

export default {
  name: 'AppObservation',
  props: {
    observation: {
      type: String,
      default: '',
    },
  },
  computed: {
    rows() {
      const text = this.observation || ''
      const dates = [...text.matchAll(EVENT_DATE)]

      // si no tiene el formato de RoQui se muestra tal cual, no se pierde nada
      if (dates.length === 0) {
        const detail = clean(text)
        return detail ? [{ date: '', type: '', tag: null, detail }] : []
      }

      const rows = []
      const start = text.slice(0, dates[0].index)
      let authorization = start.match(AUTHORIZATION)
      const leftover = clean(authorization ? start.slice(0, authorization.index) : start)
      if (leftover !== '') {
        rows.push({ date: '', type: '', tag: null, detail: leftover })
      }

      dates.forEach((date, index) => {
        const end = index + 1 < dates.length ? dates[index + 1].index : text.length
        let content = text.slice(date.index + date[0].length, end)
        // la autorizacion de la respuesta siguiente queda al final de esta
        const next = content.match(AUTHORIZATION)
        if (next) {
          content = content.slice(0, next.index)
        }
        rows.push(...eventRows(date[1], authorization, content))
        authorization = next
      })
      return rows
    },
  },
}
</script>

<style lang="scss" scoped>
.fecha {
  white-space: nowrap;
}

.detalle {
  // el numero de autorizacion son 49 digitos seguidos
  overflow-wrap: anywhere;
}
</style>
