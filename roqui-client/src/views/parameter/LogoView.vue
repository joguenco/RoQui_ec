<template>
  <AppHeader />
  <br />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Logotipo</p>
    </div>
    <div class="message-body"></div>
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
