<template>
  <div class="home-container">
    <div class="global-header">
    </div>
    <router-link to="/new-todo" class="new-todo-link">New Todo</router-link>

    <div v-if="todos.length > 0" class="todos-list">
      <div v-for="todo in todos" :key="todo.id" class="todo-item">
        <div class="todo-header">
          <input type="checkbox"
                 v-model="todo.completed"
                 :class="getCheckboxClass(todo)"
                 disabled>
          <div class="todo-content">
            <h2>{{ todo.title }}</h2>
            <div class="todo-due-date" v-if="todo.dueDate">{{ formatDate(todo.dueDate) }}</div>
            <router-link :to="{ name: 'TodoDetail', params: { id: todo.id }, props: { todo: todo } }" class="edit-button">Edit</router-link>
            <!--
            <button @click="editTodo(todo.id)" class="edit-button">Edit</button>
            -->
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
  data() {
    return {
      todos: [],
    };
  },
  methods: {
    async fetchTodos() {
      try {
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
        const token = localStorage.getItem('token');
        await axios.delete(`http://localhost:8080/todos/${id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        this.fetchTodos();
      } catch (error) {
        console.error('Error deleting todo:', error);
      }
    },
    editTodo(todoId) {
      this.$router.push({ name: 'TodoDetail', params: { id: todoId }, props: { todo: this.findTodoById(todoId) } });
    },
    findTodoById(id) {
      return this.todos.find(todo => todo.id === id);
    },
    isOverdue(dueDate) {
      const today = new Date().setHours(0, 0, 0, 0); // Set to the start of the day for accurate comparison
      return new Date(dueDate) < today;
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    getCheckboxClass(todo) {
      if (!todo.dueDate) {
        return '';
      }
      return {
        completed: todo.completed,
        overdue: this.isOverdue(todo.dueDate)
      };
    },
  },
  mounted() {
    this.fetchTodos();
  },
};
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.global-header {
  padding: 0px;
  width: 100%;
  text-align: left;
}

.new-todo-link {
  position: sticky;
  top: 20px;
  left: 20px;
  padding: 10px;
  margin: 10px;
  background-color: #555555;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  cursor: pointer;
}

.new-todo-link:hover {
  background-color: #333333;
}

.no-todos-message {
  margin-top: 20px;
  color: #7f8c8d;
}

.todos-list {
  width: 100%;
  margin-left: 2%;
  margin-right: 2%;
}

.todo-item {
  background-color: #f2f2f2;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 8px;
}

.todo-header {
  display: flex;
  align-items: center;
}

.todo-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.todo-header h2 {
  flex: 1;
  margin: 0;
  padding: 0 10px;
  text-align: left; /* Linksbündig */
}

.todo-header .todo-due-date {
  margin-right: 10px;
}

.todo-header input[type="checkbox"] {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.todo-header input[type="checkbox"].completed {
  background-color: green;
  border: 2px solid green;
}

.todo-header input[type="checkbox"].overdue {
  border: 2px solid red;
}

.todo-header .edit-button,
.todo-header .delete-button {
  background-color: #555555; /* Dunkelgrau */
  color: white;
  border: none;
  padding: 5px 10px;
  margin-left: 10px;
  border-radius: 4px; /* Abgerundete Ecken */
  cursor: pointer;
}

.todo-header .edit-button:hover,
.todo-header .delete-button:hover {
  background-color: #333333;
}

.todo-description {
  margin-top: 10px;
  padding-left: 45px;
  text-align: left; /* Linksbündig */
}
</style>
