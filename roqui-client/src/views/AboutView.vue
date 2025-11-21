<template>
  <AppHeader />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Aplicación para firmar, enviar y autorizar documentos electrónicos con el SRI Ecuador</p>
    </div>
    <div class="message-body">
      <div class="container has-text-centered mt-4 mb-4">
        <strong>
          <a href="https://resolvedor.dev" target="_blank">https://resolvedor.dev</a>
        </strong>
      </div>
      <div class="container has-text-centered mt-4 mb-4">
        <strong>Repositorio</strong>
        <br />
        <a href="https://github.com/joguenco/RoQui_ec" target="_blank"
          >https://github.com/joguenco/RoQui_ec</a
        >
      </div>
      <div class="container has-text-centered mt-4 mb-4">
        Documentación del SRI para Facturación Electrónica:
        <br />
        <a href="https://www.sri.gob.ec/facturacion-electronica" target="_blank">
          https://www.sri.gob.ec/facturacion-electronica
        </a>
      </div>
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
