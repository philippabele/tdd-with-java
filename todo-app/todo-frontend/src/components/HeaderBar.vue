<template>
  <div class="header">
    <h1>Your own TODO-List</h1>
    <div class="header-links">
      <a v-if="isLoggedIn" @click.prevent="handleLogout" class="logout-link">Logout</a>
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
  align-items: center;
  justify-content: space-between;
  background-color: #154360;
  color: white;
  padding: 15px;
}

.header h1 {
  margin: 0;
  flex: 1;
  text-align: center;
}

.header-links {
  display: flex;
  gap: 10px;
}

.logout-link {
  cursor: pointer;
  color: #9bc8e7;
}
</style>
