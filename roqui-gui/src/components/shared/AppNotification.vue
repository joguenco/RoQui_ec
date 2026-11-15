<template>
  <div class="container is-max-tablet">
    <div class="columns">
      <div class="column is-half is-offset-one-quarter">
        <div :class="['notification', type + ' is-light']">
          <p class="has-text-centered">{{ message }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppNotification',
  props: {
    type: {
      type: String,
      default: 'is-link', //is-danger, is-warning, is-success, is-info, is-primary
    },
    message: {
      type: String,
      default: 'Notification message',
    },
  },
}
</script>

<style lang="scss" scoped>
.notification {
  margin: 10px;
}
</style>
