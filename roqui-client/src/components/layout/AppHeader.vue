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
        <router-link to="/home" class="navbar-item" active-class="is-active">
          <img src="@/assets/home.svg" alt="home" />
          <strong class="has-text-grey-dark"> Inicio</strong></router-link
        >
        <router-link to="/taxpayer" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Empresa</strong></router-link
        >
        <router-link
          to="/parameter"
          class="navbar-item"
          active-class="is-active"
          v-if="showParameterOption"
          ><strong class="has-text-grey-dark">Parámetros</strong></router-link
        >
        <!-- Suscripcion oculta para todos los usuarios
        <router-link to="/subscription" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Suscripción</strong></router-link
        >
        -->
        <router-link to="/about" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Acerca</strong></router-link
        >
        <router-link to="/exit" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Salir</strong></router-link
        >
      </div>
      <div class="navbar-end">
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
import sun from '@/assets/sun.svg'
import moon from '@/assets/moon.svg'

export default {
  data: () => ({
    showParameterOption: true,
    showThemes: false,
    // el index.html ya lo puso al cargar la pagina
    theme: document.documentElement.dataset.theme === 'dark' ? 'dark' : 'light',
  }),

  computed: {
    themeIcon() {
      return this.theme === 'dark' ? moon : sun
    },
  },

  beforeMount() {
    let role = localStorage.getItem('role')
    if (role !== 'Administrator' && role !== 'Manager') {
      console.log('hide parameter menu')
      this.showParameterOption = false
    }
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
