<template>
  <div class="admin-layout d-flex">
    <AdminSidebar />
    <main class="admin-main-content flex-grow-1">
      
      
      <div class="admin-page-content p-3 p-lg-4">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
      
    </main>
  </div>
</template>

<script setup>
import AdminSidebar from './AdminSidebar.vue';

import { RouterView } from 'vue-router';
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background-color: #f8f9fa; 
}

.admin-main-content {
  margin-left: 260px; 
  width: calc(100% - 260px); 
  transition: margin-left 0.3s ease, width 0.3s ease; 
}

.admin-page-content {
  
}


.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}


@media (max-width: 991.98px) {
  .admin-main-content {
    margin-left: 0;
    width: 100%;
  }
  
}
</style>

