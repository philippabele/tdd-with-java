<template>
  <div>
    <h1>Login Page</h1>
    <form @submit.prevent="login">
      <label for="username">Username:</label>
      <input type="text" id="username" v-model="loginForm.username" required>

      <label for="password">Password:</label>
      <input type="password" id="password" v-model="loginForm.password" required>

      <button type="submit">Login</button>
    </form>
    <p v-if="loginError">{{ loginError }}</p>
    <router-link to="/register">Register</router-link>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginError: ''
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('http://localhost:8080/login', this.loginForm);
        console.log('Login successful:', response.data);
        localStorage.setItem('token', response.data.token);
        this.$router.push('/home');
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.loginError = error.response.data; // Fehlermeldung vom Backend
        } else {
          this.loginError = 'An error occurred during login';
        }
      }
    }
  }
};
</script>



<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
