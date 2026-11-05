<template>
  <div class="about">
    <div>
      <h1>About</h1>
      <p>Identification: {{ taxpayer.identification }}</p>
      <p>Name: {{ taxpayer.legalName }}</p>
    </div>
  </div>
</template>

<script>
import taxpayerService from '@/services/taxpayer-service'

export default {
  data: () => ({
    taxpayer: {},
  }),

  mounted() {
    taxpayerService.getTaxpayer().then((data) => {
      this.taxpayer = data
    })
  },
}
</script>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>
