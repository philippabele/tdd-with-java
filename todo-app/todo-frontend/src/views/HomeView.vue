<template>
  <div class="home-container">
    <router-link to="/new-todo" class="new-todo-link">Create new TODO</router-link>
    <div v-if="todos.length > 0" class="todos-list">
      <div v-for="todo in todos" :key="todo.id" class="todo-item">
        <div class="todo-header">
          <input type="checkbox" v-model="todo.completed" :class="getCheckboxClass(todo)" disabled>
          <div class="todo-content">
            <h2>{{ todo.title }}</h2>
            <div class="todo-due-date" v-if="todo.dueDate">{{ formatDate(todo.dueDate) }}</div>
            <router-link :to="{ name: 'TodoDetail', params: { id: todo.id }, props: { todo: todo } }" class="edit-button">Edit</router-link>
            <button @click="deleteTodo(todo.id)" class="delete-button">Delete</button>
          </div>
        </div>
        <div class="todo-description">
          <p>{{ todo.description }}</p>
        </div>
      </div>
    </div>
    <div v-else class="no-todos-message">
      <p>No todos yet.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  components: {
  },
  data() {
    return {
      todos: [],
    };
  },
  methods: {
    async fetchTodos() {
      try {
        if (!this.isLoggedIn) return;
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/todos', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        this.todos = response.data;
      } catch (error) {
        console.error('Error fetching todos:', error);
      }
    },
    async deleteTodo(id) {
      try {
        if (!this.isLoggedIn) return;
        const token = localStorage.getItem('token');
        await axios.delete(`http://localhost:8080/todos/${id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        this.todos = this.todos.filter(todo => todo.id !== id);
      } catch (error) {
        console.error('Error deleting todo:', error);
      }
    },
    getCheckboxClass(todo) {
      return todo.completed ? 'completed' : '';
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'short', day: 'numeric' };
      return new Date(date).toLocaleDateString(undefined, options);
    }
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('token');
    },
  },
  created() {
    this.fetchTodos();
  }
};
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: left;
  width: 100%;
  padding: 20px;
}

.new-todo-link {
  position: relative;
  top: 0px;
  left: 0px;
  margin: 20px 0;
  padding: 10px 20px;
  background-color: #5dade2;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.new-todo-link:hover {
  background-color: #2e86c1;
}

.todos-list {
  width: 100%;
  max-width: 800px;
}

.todo-item {
  background-color: #f8f9f9;
  border: 1px solid #d5d8dc;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
}

.todo-header {
  display: flex;
  align-items: center;
}

.todo-content {
  flex: 1;
  margin-left: 10px;
  display: flex;
  align-items: center;
}

.todo-content h2 {
  flex: 1;
  margin: 0;
  padding: 0 10px;
  text-align: left;
}

.todo-due-date {
  margin-right: 10px;
  text-align: left;
}

.todo-header input[type="checkbox"] {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.edit-button {
  padding: 5px 10px;
  background-color: #5dade2;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.edit-button:hover {
  background-color: #2e86c1;
}

.delete-button {
  padding: 5px 10px;
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 5px 10px;
  margin-left: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-button:hover {
  background-color: #c0392b;
}

.todo-description {
  margin-top: 10px;
  padding-left: 55px;
  text-align: left;
}

  .no-todos-message {
  margin-top: 20px;
  color: #333333;
}
</style>
