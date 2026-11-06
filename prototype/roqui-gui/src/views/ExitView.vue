<template>
  <div></div>
</template>
<script>
import { useAuthStore } from '@/stores/auth'

export default {
  mounted() {
    const authStore = useAuthStore()
    authStore.logout()
  },
}
</script>
