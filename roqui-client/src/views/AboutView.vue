<template>
  <AppHeader />
  <br />
  <article class="message is-link m-6">
    <div class="message-header">
      <p>Aplicación para firmar, enviar y autorizar comprobantes electrónicos</p>
    </div>
    <div class="message-body">
      <p class="has-text-centered">
        <strong>
          <a href="https://resolvedor.dev" target="_blank">https://resolvedor.dev</a>
        </strong>
      </p>
    </div>
  </article>
  <AppFooter class="m-6" />
</template>
<script>
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

export default {
  components: {
    AppHeader,
    AppFooter,
  },
}
</script>
