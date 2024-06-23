import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';

const app = createApp(App);

app.use(router);
app.use(store);

app.mount('#app');

/*
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import axios from './axios';
import HeaderBar from './components/HeaderBar.vue'; // Passe den Pfad entsprechend an

// Set base URL for Axios
axios.defaults.baseURL = 'http://localhost:8080/api';

// Axios request interceptor for adding JWT token
axios.interceptors.request.use(config => {
    const token = store.state.userToken;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('Token added to request:', token);
    }
    return config;
}, error => {
    console.error('Error adding token to request:', error);
    return Promise.reject(error);
});


const app = createApp(App);

app.component('HeaderBar', HeaderBar);

app.use(router);
app.use(store);

app.mount('#app');
*/