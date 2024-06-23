
<template>
  <div>
    <ul>
      <li v-for="todo in todos" :key="todo.id">
        <TodoItem :todo="todo" @editTodo="editTodo" @deleteTodo="deleteTodo" />
      </li>
    </ul>
    <router-link to="/todo/new">New Todo</router-link>
  </div>
</template>

<script>
import TodoItem from "@/components/TodoItem.vue";

export default {
  components: {
    TodoItem,
  },
  props: {
    todos: Array,
  },
  methods: {
    editTodo(todo) {
      this.$emit("editTodo", todo);
    },
    deleteTodo(todo) {
      this.$emit("deleteTodo", todo);
    },
  },
};
</script>

<style scoped>
/* Add your styling here */
</style>






<!--
<template>
  <div class="todo-list">
    <div v-for="todo in todos" :key="todo.id" class="todo-item">
      <input type="checkbox" v-model="todo.completed" @change="updateTodo(todo)" />
      <span :class="{ completed: todo.completed }">{{ todo.title }}</span>
      <router-link :to="`/todo/${todo.id}`">Edit</router-link>
      <button @click="deleteTodo(todo.id)">Delete</button>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['todos'])
  },
  methods: {
    ...mapActions(['updateTodo', 'deleteTodo']),
  },
  mounted() {
    this.$store.dispatch('fetchTodos');
  }
}
</script>

<style scoped>
.todo-list {
  width: 100%;
  max-width: 600px;
  margin: auto;
}

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