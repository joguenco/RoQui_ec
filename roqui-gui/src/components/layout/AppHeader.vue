<template>
  <nav class="navbar is-primary" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item">
        <img src="@/assets/star.svg" alt="star" />
        <strong class="has-text-grey-darker">RoQui Ec</strong></a
      >
    </div>
    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start">
        <router-link to="/" class="navbar-item" active-class="is-active">
          <img src="@/assets/home.svg" alt="home" />
          <strong class="has-text-grey-dark"> Inicio</strong></router-link
        >
        <router-link to="/taxpayer" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Empresa</strong></router-link
        >
        <router-link to="/parameter" class="navbar-item" active-class="is-active" v-if="isAdmin"
          ><strong class="has-text-grey-dark">Parámetros</strong></router-link
        >

        <router-link to="/about" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Acerca</strong></router-link
        >
        <router-link @click="logout" to="/" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Salir</strong></router-link
        >
      </div>
      <div class="navbar-end">
        <div class="navbar-item has-text-grey-dark">Usuario: {{ userName }}</div>
        <div class="navbar-item has-dropdown" :class="{ 'is-active': showThemes }">
          <a class="navbar-link is-arrowless" title="Tema" @click.stop="showThemes = !showThemes">
            <img :src="themeIcon" alt="tema" />
          </a>
          <div class="navbar-dropdown is-right">
            <a
              class="navbar-item"
              :class="{ 'is-selected': theme === 'light' }"
              @click="setTheme('light')"
            >
              <img src="@/assets/sun.svg" alt="" /><span class="ml-2">Claro</span>
            </a>
            <a
              class="navbar-item"
              :class="{ 'is-selected': theme === 'dark' }"
              @click="setTheme('dark')"
            >
              <img src="@/assets/moon.svg" alt="" /><span class="ml-2">Oscuro</span>
            </a>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>
<script>
import { useAuth } from '@/composables/useAuth'
import sun from '@/assets/sun.svg'
import moon from '@/assets/moon.svg'

export default {
  setup() {
    const { isAuthenticated, userName, isAdmin, login, logout } = useAuth()
    return { isAuthenticated, userName, isAdmin, login, logout }
  },

  data: () => ({
    showThemes: false,
    // el index.html ya lo puso al cargar la pagina
    theme: document.documentElement.dataset.theme === 'dark' ? 'dark' : 'light',
  }),

  computed: {
    themeIcon() {
      return this.theme === 'dark' ? moon : sun
    },
  },

  mounted() {
    // el menu se cierra al hacer clic en cualquier otro sitio
    document.addEventListener('click', this.closeThemes)
  },

  beforeUnmount() {
    document.removeEventListener('click', this.closeThemes)
  },

  methods: {
    // claro u oscuro, y se guarda para la proxima vez que entre
    setTheme(theme) {
      this.theme = theme
      this.showThemes = false
      document.documentElement.dataset.theme = theme
      localStorage.setItem('theme', theme)
    },

    closeThemes() {
      this.showThemes = false
    },
  },
}
</script>
