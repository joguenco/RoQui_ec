<template>
  <AppHeader />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Información de la Suscripción</p>
    </div>
    <div class="message-body">
      <p><strong>Suscriptor: </strong>{{ subscription.subscriber }}</p>
      <p><strong>Nombre: </strong>{{ subscription.name }}</p>
      <p><strong>Fecha de expiración: </strong>{{ subscription.endDate }}</p>
      <p><strong>La suscripción caducará en: </strong>{{ subscription.remainingDays }} días</p>
    </div>
  </article>
</template>
<script>
import AppHeader from '@/components/layout/AppHeader.vue'
import subscriptionService from '@/services/subscription-service'

export default {
  components: {
    AppHeader,
  },

  data: () => ({
    subscription: {},
    user: {},
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getStatus(this.user.accessToken)
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    getStatus(token) {
      subscriptionService.getStatus(token).then((response) => {
        this.subscription = response.data
      })
    },
  },
}
</script>
