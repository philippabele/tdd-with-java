<template>
  <div>
    <div v-if="!editing">
      <span>{{ todo.title }}</span>
      <button @click="editing = true">Edit</button>
      <button @click="deleteTodo">Delete</button>
    </div>
    <div v-else>
      <input type="text" v-model="editedTitle" />
      <button @click="saveEdit">Save</button>
      <button @click="cancelEdit">Cancel</button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    todo: Object,
  },
  data() {
    return {
      editing: false,
      editedTitle: this.todo.title,
    };
  },
  methods: {
    saveEdit() {
      // Update todo with edited title
      this.$emit("editTodo", { ...this.todo, title: this.editedTitle });
      this.editing = false;
    },
    cancelEdit() {
      this.editing = false;
      this.editedTitle = this.todo.title;
    },
    deleteTodo() {
      this.$emit("deleteTodo", this.todo);
    },
  },
};
</script>

<style scoped>
/* Add your styling here */
</style>


<!--
<template>
  <div class="todo-item">
    <input type="checkbox" v-model="todo.completed" @change="updateTodo" />
    <span :class="{ completed: todo.completed }">{{ todo.title }}</span>
    <router-link :to="`/todo/${todo.id}`">Edit</router-link>
    <button @click="deleteTodo">Delete</button>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  props: {
    todo: Object  // Assume that todo is passed as a prop from parent component
  },
  methods: {
    ...mapActions(['updateTodo', 'deleteTodo']),
    updateTodo() {
      this.$store.dispatch('updateTodo', this.todo);
    },
    deleteTodo() {
      this.$store.dispatch('deleteTodo', this.todo.id);
    }
  }
}
</script>

<style scoped>
.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  border-bottom: 1px solid #ccc;
}

.completed {
  text-decoration: line-through;
}
</style>

-->
