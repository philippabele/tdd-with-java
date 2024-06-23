<template>
  <div class="todo-detail-container">
    <h2 class="header">Edit Todo</h2>
    <form @submit.prevent="updateTodo" class="form">

      <label for="completed">Completed:</label>
      <input type="checkbox" id="completed" v-model="localTodo.completed" :class="{ 'completed': localTodo.completed, 'overdue': isOverdue(localTodo.dueDate) }">

      <label for="title">Title:</label>
      <input type="text" id="title" v-model="localTodo.title" required>

      <label for="description">Description:</label>
      <textarea id="description" v-model="localTodo.description" ></textarea>

      <label for="dueDate">Due Date:</label>
      <input type="date" id="dueDate" v-model="localTodo.dueDate">

      <div class="button-container">
        <button type="submit" class="save-button">Save and Go Back</button>
        <button type="button" @click="cancelEdit" class="cancel-button">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  props: {
    todo: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      localTodo: { ...this.todo }
    };
  },
  watch: {
    todo: {
      handler(newVal) {
        this.localTodo = { ...newVal };
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    cancelEdit() {
      this.$router.go(-1);
    },
    isOverdue(dueDate) {
      if (!dueDate) return false;
      const now = new Date();
      const due = new Date(dueDate);
      return due < now;
    },
    updateTodo() {
      if (!this.isLoggedIn) return;
      this.$emit('update-todo', this.localTodo);
    },
    computed: {
      isLoggedIn() {
        return !!localStorage.getItem('token');
      },
    },
  }
}
</script>

<style scoped>
.todo-detail-container {
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
  width: 400px;
  gap: 10px;
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

