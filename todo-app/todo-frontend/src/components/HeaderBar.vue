<template>
  <div class="header">
    <h1>Your own TODO-List</h1>
    <div class="header-links">
      <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
      <router-link v-if="!isLoggedIn" to="/register">Register</router-link>
      <button v-if="isLoggedIn" @click.prevent="handleLogout" class="logout-link">Logout</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    isLoggedIn: Boolean,
  },
  methods: {
    async handleLogout() {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          await axios.post('http://localhost:8080/logout', {}, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
        }
        localStorage.removeItem('token');
        this.$emit('logout');
        this.$router.push('/home');
      } catch (error) {
        console.error('Error during logout:', error.response || error.message);
      }
    },
  },
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #aed6f1;
  color: #154360;
  padding: 15px;
}

.header h1 {
  margin: 0;
}

.header-links {
  display: flex;
  gap: 15px;
}

.logout-link {
  cursor: pointer;
  color: #154360;
}

.logout-link:hover {
  color: #0b3c60;
}
</style>
