<template>
  <div>
    <h2>Create new Todo</h2>
    <form @submit.prevent="saveTodo" class="todo-form">
      <label for="completed">Completed:</label>
      <input type="checkbox" id="completed" v-model="newTodo.completed">

      <label for="title">Title:</label>
      <input type="text" id="title" v-model="newTodo.title" required>

      <label for="description">Description:</label>
      <textarea id="description" v-moacdel="newTodo.description"></textarea>

      <label for="dueDate">Due Date:</label>
      <input type="date" id="dueDate" v-model="newTodo.dueDate">

      <div class="button-container">
        <button type="submit" class="save-button">Create</button>
        <button @click="cancelNew" class="cancel-button">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      newTodo: {
        title: '',
        description: '',
        dueDate: '',
        completed: false,
      },
    };
  },
  methods: {
    cancelNew() {
      this.$router.go(-1);
    },
    async saveTodo() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.post('http://localhost:8080/todos', this.newTodo, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        console.log('Todo added successfully:', response.data);

        // Reset form fields
        this.newTodo = {
          title: '',
          description: '',
          dueDate: '',
          completed: false,
        };
        this.$router.push('/home');
      } catch (error) {
        console.error(error.response.data);
      }
    },
  },
};
</script>

<style scoped>
.todo-form {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 2%;
  margin-right: 2%;
  padding: 20px;
  background-color: #f2f2f2;
  border-radius: 8px;
}

.todo-form label {
  margin-bottom: 8px;
}

.todo-form input[type="text"],
.todo-form textarea,
.todo-form input[type="date"],
.todo-form input[type="checkbox"] {
  width: calc(100% - 20px);
  padding: 8px;
  margin-bottom: 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.todo-form input[type="checkbox"] {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.button-container {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.todo-form .save-button,
.todo-form .cancel-button {
  background-color: #555555;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  margin-right: 10px;
  cursor: pointer;
}

.todo-form .save-button:hover,
.todo-form .cancel-button:hover {
  background-color: #333333;
}

.todo-form h2 {
  margin-top: 0;
  margin-bottom: 20px;
  text-align: left;
  color: #333;
}
</style>
