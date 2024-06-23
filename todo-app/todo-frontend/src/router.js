import { createRouter, createWebHistory } from 'vue-router';
import IndexView from '@/views/IndexView.vue';
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import HomeView from '@/views/HomeView.vue';
import NewTodoForm from '@/views/NewTodoForm.vue';
import TodoDetailView from '@/views/TodoDetailView.vue';

const routes = [
    {
        path: '/',
        name: 'Index',
        component: IndexView,
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterView,
    },
    {
        path: '/home',
        name: 'Home',
        component: HomeView,
        meta: { requiresAuth: true },
    },
    {
        path: '/new-todo',
        name: 'NewTodoForm',
        component: NewTodoForm,
        meta: { requiresAuth: true },
    },
    {
        path: '/todo/:id',
        name: 'TodoDetail',
        component: TodoDetailView,
        meta: { requiresAuth: true },
        props: true
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const isLoggedIn = localStorage.getItem('token');
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isLoggedIn) {
            next('/login'); // Redirect to login page if not authenticated
        } else {
            next(); // Proceed to the requested route
        }
    } else {
        next(); // Allow access to non-protected routes
    }
});

export default router;
