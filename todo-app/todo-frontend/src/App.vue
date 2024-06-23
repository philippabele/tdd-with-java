<template>
  <div id="app">
    <HeaderBar :isLoggedIn="isLoggedIn" @logout="logout" />
    <router-view :isLoggedIn="isLoggedIn" @login="login" @register="register" />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderBar.vue';

export default {
  name: 'App',
  components: {
    HeaderBar,
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('token');
    },
  },
  methods: {
    login(token) {
      localStorage.setItem('token', token);
      this.$router.push('/home');
    },
    register() {
      this.$router.push('/register');
    },
    logout() {
      localStorage.removeItem('token');
      this.$router.push('/home');
    },
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin: 0px;
}
</style>
