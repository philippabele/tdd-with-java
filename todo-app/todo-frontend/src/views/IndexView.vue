<template>
  <div class="index-container">
    <h2 class="main-title">Get started with managing your tasks</h2>
    <router-link to="/login" class="button">Login</router-link>
    <router-link to="/register" class="button">Sign up</router-link>
  </div>
</template>

<script>
export default {
  name: 'IndexView'
};
</script>

<style scoped>
.index-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 20px;
  margin-top: 60px;
}

.main-title {
  font-size: 24px;
  margin-bottom: 30px;
  color: #333;
}

.button {
  padding: 15px 30px;
  background-color: #88c8f3;
  color: #154360;
  text-decoration: none;
  border-radius: 8px;
  margin: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #5dade2;
}
</style>
