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
</template>
<script>
import taxpayerService from '@/services/taxpayer-service'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
  },

  data: () => ({
    taxpayer: {},
    user: {},
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getTaxpayer(this.user.accessToken)
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
  },
}
</script>
