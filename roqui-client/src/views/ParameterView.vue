<template>
  <AppHeader />

  <article class="panel is-info m-6">
    <p class="panel-heading">Parámetros</p>

    <a class="panel-block is-active">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link class="nav-item" to="parameter/base/directory">Directorio base</router-link>
    </a>
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link class="nav-item" to="parameter/certificate">Certificado</router-link>
    </a>
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link class="nav-item" to="parameter/logo">Logotipo</router-link>
    </a>
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link class="nav-item" to="parameter/mail/smtp">Cliente de Correo SMTP</router-link>
    </a>
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link class="nav-item" to="parameter/mail/http"
        >Cliente de Correo HTTP (in develop)</router-link
      >
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
