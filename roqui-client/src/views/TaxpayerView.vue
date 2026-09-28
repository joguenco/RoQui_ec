<template>
  <AppHeader />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Información Tributaria</p>
    </div>
    <div class="message-body">
      <p><strong>Razón Social: </strong>{{ taxpayer.legalName }}</p>
      <p><strong>RUC: </strong>{{ taxpayer.identification }}</p>
      <p><strong>Obligado a llevar contabilidad: </strong>{{ taxpayer.forcedAccounting }}</p>
      <p v-show="taxpayer.specialTaxpayer">
        <strong>Contribuyente especial: </strong>{{ taxpayer.specialTaxpayer }}
      </p>
      <p v-show="taxpayer.retentionAgent">
        <strong>Agente de retención resolución Nº: </strong>{{ taxpayer.retentionAgent }}
      </p>
      <p v-show="taxpayer.other"><strong>Régimen: </strong>{{ taxpayer.other }}</p>
    </div>
  </article>
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Establecimientos</p>
    </div>
    <div class="message-body">
      <table class="table is-bordered is-striped is-hoverable is-fullwidth">
        <thead>
          <tr>
            <th class="titulo">Código</th>
            <th class="titulo">Nombre Comercial</th>
            <th class="titulo">Dirección</th>
            <th class="titulo">Tipo</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="establishment in establishments" :key="establishment.code">
            <td>{{ establishment.code }}</td>
            <td>{{ establishment.businessName }}</td>
            <td>{{ establishment.address }}</td>
            <!-- La vista devuelve Principal o BranchOffice -->
            <td>{{ establishment.principal === 'Principal' ? 'Principal' : 'Sucursal' }}</td>
          </tr>
          <tr v-show="establishments.length === 0">
            <td colspan="4">No hay establecimientos registrados</td>
          </tr>
        </tbody>
      </table>
    </div>
  </article>
</template>
<script>
import taxpayerService from '@/services/taxpayer-service'
import establishmentService from '@/services/establishment-service'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
  },

  data: () => ({
    taxpayer: {},
    establishments: [],
    user: {},
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getTaxpayer(this.user.accessToken)
      this.getEstablishments(this.user.accessToken)
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    getTaxpayer(token) {
      taxpayerService.getTaxpayer(token).then((response) => {
        this.taxpayer = response.data
      })
    },

    getEstablishments(token) {
      establishmentService.getEstablishments(token).then((response) => {
        this.establishments = response.data
      })
    },
  },
}
</script>
