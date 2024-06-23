<template>
  <div class="new-todo-container">
    <h2 class="header">Create New Todo</h2>
    <form @submit.prevent="createTodo" class="form">

      <label for="completed">Completed:</label>
      <input type="checkbox" id="completed" v-model="todo.completed">

      <label for="title">Title:</label>
      <input type="text" id="title" v-model="todo.title" required class="input">

      <label for="description">Description:</label>
      <textarea id="description" v-model="todo.description" required class="textarea"></textarea>

      <label for="dueDate">Due Date:</label>
      <input type="date" id="dueDate" v-model="todo.dueDate" class="input">

      <div class="button-container">
        <button type="submit" class="save-button">Create new TODO </button>
        <button @click="cancelNew" class="cancel-button">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  components: {
  },
  data() {
    return {
      todo: {
        title: '',
        description: '',
        dueDate: '',
        completed: false,
      }
    };
  },
  methods: {
    cancelNew() {
      this.$router.go(-1);
    },
    async createTodo() {
      try {
        if (this.isLoggedIn) return;
        const token = localStorage.getItem('token');
        const response = await axios.post('http://localhost:8080/todos', this.todo, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        console.log('Todo created:', response.data);
        this.$router.push('/home');
      } catch (error) {
        console.error('Error creating todo:', error);
      }
    },
    computed: {
      isLoggedIn() {
        return !localStorage.getItem('token');
      },
    },
  }
};
</script>

<style scoped>
.new-todo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: 2%;
  margin-right: 2%;
  padding: 20px;
}

.header {
  padding: 20px;
  width: 100%;
  text-align: center;
  color: #154360;
  margin-bottom: 40px;
}

.form {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 400px;
  gap: 15px;
}

.todo-content input[type="text"],
.todo-content textarea,
.todo-content input[type="date"],
.todo-content input[type="checkbox"] {
  width: calc(100% - 20px);
  padding: 8px;
  margin-bottom: 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.todo-content input[type="checkbox"] {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.button-container {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cancel-button,
.save-button {
  padding: 10px 20px;
  border: none;
  padding: 10px 20px;
  margin-right: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-button {
  background-color: #555555;
  color: white;
}

.save-button {
  background-color: #5dade2;
  color: white;
}

.cancel-button:hover {
  background-color: #333333;
}

.save-button:hover {
  background-color: #2e86c1;
}
</style>
