<template>
  <div class="register-container">
    <h2 class="subheader">Sign up</h2>
    <form @submit.prevent="register" class="form">

      <label for="username">Username:</label>
      <input type="text" id="username" v-model="registerForm.username" required>

      <label for="password">Password:</label>
      <input type="password" id="password" v-model="registerForm.password" required>

      <label for="confirmPassword">Confirm Password:</label>
      <input type="password" id="confirmPassword" v-model="registerForm.confirmPassword" required>

      <button type="submit" class="button">Sign up</button>
    </form>
    <p v-if="registerError" class="error">{{ registerError }}</p>

    <p class="login"> You already have an account?</p>
    <router-link to="/login" class="button">Log in</router-link>
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
.register-container {
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
  background-color: #5dade2;
}

.error {
  color: red;
  margin-top: 10px;
}

.login {
  color:  #333;
  margin-top: 60px;
}

</style>
