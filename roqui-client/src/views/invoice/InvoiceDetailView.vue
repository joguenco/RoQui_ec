<template>
  <div class="container">
    <table class="table is-bordered is-striped is-hoverable">
      <thead>
        <tr>
          <th class="titulo">
            <a title="Número de comprobante" class="button">Factura</a>
          </th>
          <th class="titulo">
            <a title="Fecha de comprobante ( aaaa-mm-dd )" class="button">Fecha</a>
          </th>
          <th class="titulo">Total</th>
          <th class="titulo">Documento</th>
          <th class="titulo">Razón Social</th>
          <th class="titulo">Correo</th>
          <th class="titulo">Estado</th>
          <th class="titulo">PDF</th>
          <th class="titulo">XML</th>
          <th class="titulo">
            <a title="Autorizar" class="button">A</a>
          </th>
        </tr>
      </thead>
      <tfoot>
        <tr>
          <th class="titulo">
            <a title="Número de comprobante" class="button">Factura</a>
          </th>
          <th class="titulo">
            <a title="Fecha de comprobante ( aaaa-mm-dd )" class="button">Fecha</a>
          </th>
          <th class="titulo">Total</th>
          <th class="titulo">Documento</th>
          <th class="titulo">Razón Social</th>
          <th class="titulo">Correo</th>
          <th class="titulo">Estado</th>
          <th class="titulo">PDF</th>
          <th class="titulo">XML</th>
          <th class="titulo">
            <a title="Autorizar" class="button">A</a>
          </th>
        </tr>
      </tfoot>
      <tbody>
        <tr v-for="d in details" :key="d.id">
          <td class="no_enviado">
            <p>{{ d.code }}</p>
            <p>{{ d.number }}</p>
          </td>
          <td>{{ d.date }}</td>
          <td class="numero">{{ d.total }}</td>
          <td class="numero">{{ d.identification }}</td>
          <td>{{ d.legalName }}</td>
          <td>
            <a class="button button is-text is-outlined">{{ d.email }}</a>
          </td>
          <td>
            <a
              class="button is-info is-outlined no_enviado"
              @click="showDocument(d.code, d.number)"
              >{{ d.status }}</a
            >
          </td>
          <td>
            <a href="" target="_blank" class="button is-link is-outlined no_enviado">pdf</a>
          </td>
          <td>
            <a href="" target="_blank" class="button is-success is-outlined no_enviado">xml</a>
          </td>
          <td>
            <a class="button is-info is-loading" style="display: none">A</a
            ><a class="button is-info" @click="authorize(d.code, d.number)">A</a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import invoiceService from '@/services/invoice-service'
import documentService from '@/services/document-service'

export default {
  data: () => ({
    user: {},
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
    } else {
      this.$router.push('/')
    }
  },

  props: {
    details: { type: Array, required: true },
  },

  methods: {
    authorize(code, number) {
      invoiceService
        .authorize(this.user.accessToken, code, number)
        .then((response) => {
          console.log('update', response.data)
        })
        .catch((error) => {
          console.error('Error authorizing invoice:', error)
        })
    },

    showDocument(code, number) {
      documentService
        .find(this.user.accessToken, code, number)
        .then((response) => {
          console.log('document', response.data)
        })
        .catch((error) => {
          console.error('Error fetching document:', error)
        })
    },
  },
}
</script>
