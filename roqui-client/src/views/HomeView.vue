<template>
  <AppHeader />

  <article class="panel is-info m-6">
    <p class="panel-heading">Menú</p>

    <a class="panel-block is-active">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      Facturas
    </a>
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      Notas de Crédito
    </a>
  </article>
</template>
<script>
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
  },
}
</script>
