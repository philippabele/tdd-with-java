<template>
  <div>
    <div class="index-container">
      <router-link to="/login">Log in</router-link>
      <router-link to="/register">Sign up</router-link>
    </div>
  </div>
</template>

<script>

export default {}
</script>

<style scoped>
.index-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
