<template>
  <div class="search-bar">
    <form @submit.prevent="performSearch">
      <input
        type="text"
        v-model.trim="searchKeyword"
        placeholder="Tìm kiếm sản phẩm..."
        class="search-input"
        aria-label="Tìm kiếm sản phẩm"
      />
      <button type="submit" class="search-button" aria-label="Tìm kiếm">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
          <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
        </svg>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const searchKeyword = ref('');
const router = useRouter();

const performSearch = () => {
  if (!searchKeyword.value) {


    return;
  }

  router.push({
    name: 'productList',
    query: { keyword: searchKeyword.value }
  });


};
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
}

.search-bar form {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 20px; 
  overflow: hidden; 
  background-color: #fff;
}

.search-input {
  padding: 8px 15px;
  border: none;
  outline: none;
  font-size: 0.9em;
  flex-grow: 1; 
  min-width: 150px; 
  border-radius: 20px 0 0 20px; 
}

.search-button {
  padding: 8px 12px;
  border: none;
  background-color: #eee; 
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 20px 20px 0; 
  transition: background-color 0.3s ease;
}
.search-button:hover {
  background-color: #ddd;
}
.search-button svg {
  color: #555;
}
</style>

