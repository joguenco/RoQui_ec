<template>
  <nav class="navbar is-primary" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item"><strong class="has-text-grey-darker">RoQui Ec</strong></a>
    </div>
    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start">
        <router-link to="/home" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Inicio</strong></router-link
        >
        <router-link to="/taxpayer" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Empresa</strong></router-link
        >
        <router-link to="/parameter" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Parámetros</strong></router-link
        >
        <router-link to="/about" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Acerca</strong></router-link
        >
        <router-link to="/exit" class="navbar-item" active-class="is-active"
          ><strong class="has-text-grey-dark">Salir</strong></router-link
        >
      </div>
    </div>
  </nav>
</template>
