<template>
  <div>
    <h1>Register Page</h1>
    <form @submit.prevent="register">
      <label for="username">Username:</label>
      <input type="text" id="username" v-model="registerForm.username" required>

      <label for="password">Password:</label>
      <input type="password" id="password" v-model="registerForm.password" required>

      <label for="confirmPassword">Confirm Password:</label>
      <input type="password" id="confirmPassword" v-model="registerForm.confirmPassword" required>

      <button type="submit">Register</button>
    </form>
    <p v-if="registerError">{{ registerError }}</p>
    <router-link to="/login">Login</router-link>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      registerError: ''
    };
  },
  methods: {
    async register() {
      try {
        const response = await axios.post('http://localhost:8080/register', this.registerForm);
        console.log('Registration successful:', response.data);
        // Assuming backend returns a token upon successful registration
        localStorage.setItem('token', response.data.token);
        this.$router.push('/home');
      } catch (error) {
        if (error.response && error.response.status === 409) {
          this.registerError = error.response.data;
        } else {
          this.registerError = 'An error occurred during registration';
        }
      }
    }
  }
};
</script>

<style scoped>
.signup-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
