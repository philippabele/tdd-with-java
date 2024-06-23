<template>
  <div class="login-container">
    <h2 class="subheader">Log in</h2>
    <form @submit.prevent="login" class="form">

      <label for="username">Username:</label>
      <input type="text" id="username" v-model="loginForm.username" required>

      <label for="password">Password:</label>
      <input type="password" id="password" v-model="loginForm.password" required>

      <button type="submit" class="button">Log in</button>
    </form>
    <p v-if="loginError" class="error">{{ loginError }}</p>

    <p class="register"> You don't have an account yet?</p>
    <router-link to="/register" class="button">Sign up</router-link>
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
          this.loginError = error.response.data;
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
  margin-top: 60px;
}

.subheader {
  margin: 20px 0;
}

.form {
  display: flex;
  flex-direction: column;
  width: 300px;
  gap: 10px;
}

.button {
  padding: 10px 20px;
  background-color: #88c8f3;
  color: #154360;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
  text-decoration: none;
}

.button:hover {
  background-color: #5dade2;;
}

.error {
  color: red;
  margin-top: 10px;
}

.register {
  color:  #333;
  margin-top: 60px;
}

</style>
