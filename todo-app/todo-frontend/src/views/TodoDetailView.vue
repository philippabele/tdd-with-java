<template>
  <div>
    <h2>Edit Todo</h2>
    <form @submit.prevent="save" class="todo-content">
      <label for="completed">Completed:</label>
      <input type="checkbox" id="completed" v-model="localTodo.completed" :class="{ 'completed': localTodo.completed, 'overdue': isOverdue(localTodo.dueDate) }">

      <label for="title">Title:</label>
      <input type="text" id="title" v-model="localTodo.title" required>

      <label for="description">Description:</label>
      <textarea id="description" v-model="localTodo.description"></textarea>

      <label for="dueDate">Due Date:</label>
      <input type="date" id="dueDate" v-model="localDueDate" v-if="!localTodo.dueDate">
      <input type="date" id="dueDate" v-model="localDueDate" v-else>

      <div class="button-container">
        <button type="submit" class="save-button">Save and Go Back</button>
        <button type="button" @click="cancelEdit" class="cancel-button">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: 'TodoDetailView',
  props: {
    todo: Object // Stellen Sie sicher, dass todo als Prop deklariert ist
  },
  data() {
    return {
      localTodo: {
        id: null,
        title: '',
        description: '',
        completed: false,
        dueDate: null
      },
      localDueDate: null
    };
  },
  created() {
    if (this.todo) {
      this.localTodo = { ...this.todo };
      if (this.todo.dueDate) {
        this.localDueDate = new Date(this.todo.dueDate).toISOString().substr(0, 10);
      }
    }
  },
  methods: {
    cancelEdit() {
      this.$router.go(-1);
    },
    async save() {
      try {
        const token = localStorage.getItem('token');
        if (this.localDueDate) {
          this.localTodo.dueDate = new Date(this.localDueDate).toISOString();
        } else {
          this.localTodo.dueDate = null;
        }
        await axios.put(`http://localhost:8080/todos/${this.localTodo.id}`, this.localTodo, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        this.$router.push('/home');
      } catch (error) {
        console.error('Error saving todo:', error);
      }
    },
    formatDate(dateString) {
      const options = {year: 'numeric', month: '2-digit', day: '2-digit'};
      const date = new Date(dateString);
      return date.toLocaleDateString(undefined, options);
    },
    isOverdue(dueDate) {
      if (!dueDate) return false;
      const now = new Date();
      const due = new Date(dueDate);
      return due < now;
    }
  }
};
</script>

<style scoped>
.todo-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 2%;
  margin-right: 2%;
  padding: 20px;
  background-color: #f2f2f2;
  border-radius: 8px;
}

.todo-content label {
  margin-bottom: 8px;
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

.todo-content input[type="checkbox"].completed {
  background-color: green;
}

.todo-content input[type="checkbox"].overdue {
  background-color: red;
}

.button-container {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.cancel-button,
.save-button {
  background-color: #555555; /* Dunkelgrau */
  color: white;
  border: none;
  padding: 10px 20px;
  margin-right: 10px;
  border-radius: 4px; /* Abgerundete Ecken */
  cursor: pointer;
}

.cancel-button:hover, .save-button:hover {
  background-color: #333333;
}
</style>
