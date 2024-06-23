import { createStore } from 'vuex';

export default createStore({
    state: {
        isLoggedIn: false,
        token: null,
    },
    mutations: {
        login(state, token) {
            state.isLoggedIn = true;
            state.token = token;
        },
        logout(state) {
            state.isLoggedIn = false;
            state.token = null;
        },
    },
    actions: {
        login({ commit }, token) {
            localStorage.setItem('token', token);
            commit('login', token);
        },
        logout({ commit }) {
            localStorage.removeItem('token');
            commit('logout');
        },
    },
    getters: {
        isLoggedIn: (state) => state.isLoggedIn,
        token: (state) => state.token,
    },
});
