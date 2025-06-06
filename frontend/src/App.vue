<template>
  <div id="app">
    <AppHeader :class="{ 'shifted-by-sidebar': isSidebarOpen && isLargeScreen }" />
    <AppSidebar />
    <main class="main-content" :class="{ 'shifted-by-sidebar': isSidebarOpen && isLargeScreen }">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <!-- Giả sử AppFooter có class="app-footer" bên trong nó -->
    <AppFooter class="app-footer" :class="{ 'shifted-by-sidebar': isSidebarOpen && isLargeScreen }" />
    <!-- Tích hợp Zalo Link Widget -->
    <ZaloLinkWidget />
    <!-- Chỉ hiển thị chat widget nếu không phải trang admin -->
    <ChatWidget v-if="!isAdminRoute" />
    <!-- ============================== -->
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRoute } from 'vue-router'; // import để lấy thông tin route hiện tại
import AppHeader from "./components/layout/AppHeader.vue";
import AppSidebar from "./components/layout/AppSidebar.vue";
import AppFooter from "./components/layout/AppFooter.vue";
import ChatWidget from "@/components/chat/ChatWidget.vue";
import ZaloLinkWidget from '@/components/chat/ZaloLinkWidget.vue';
import { Offcanvas } from "bootstrap";

const route = useRoute();

// --- Khai báo biến isAdminRoute để kiểm tra route hiện tại có phải trang admin hay không ---
// Dùng computed vì route.path là reactive, khi route thay đổi sẽ tự động cập nhật giá trị
const isAdminRoute = computed(() => {
  return route.path.startsWith('/admin');
});

const isLargeScreen = ref(false);
const isSidebarOpen = ref(false);
const sidebarWidth = 280;
let mediaQueryList = null;
let sidebarInstance = null;

const checkScreenSize = () => {
  isLargeScreen.value = mediaQueryList.matches;
  if (!isLargeScreen.value && sidebarInstance && !sidebarInstance._isShown) {
    isSidebarOpen.value = false;
  }
  if (isLargeScreen.value && sidebarInstance && !sidebarInstance._isShown) {
    isSidebarOpen.value = false;
  }
};
const handleSidebarShow = () => { isSidebarOpen.value = true; };
const handleSidebarHide = () => { isSidebarOpen.value = false; };

onMounted(() => {
  mediaQueryList = window.matchMedia("(min-width: 992px)");
  checkScreenSize();
  mediaQueryList.addEventListener("change", checkScreenSize);
  const sidebarElement = document.getElementById("appSidebar");
  if (sidebarElement) {
    sidebarInstance = Offcanvas.getOrCreateInstance(sidebarElement);
    sidebarElement.addEventListener('show.bs.offcanvas', handleSidebarShow);
    sidebarElement.addEventListener('hide.bs.offcanvas', handleSidebarHide);
    if (sidebarInstance._isShown) { isSidebarOpen.value = true; }
  } else { console.error("AppSidebar element not found"); }
});
onUnmounted(() => {
  if (mediaQueryList) { mediaQueryList.removeEventListener("change", checkScreenSize); }
  const sidebarElement = document.getElementById("appSidebar");
  if (sidebarElement) {
    sidebarElement.removeEventListener('show.bs.offcanvas', handleSidebarShow);
    sidebarElement.removeEventListener('hide.bs.offcanvas', handleSidebarHide);
    // Không nên dispose instance ở đây nếu AppSidebar vẫn còn được mount
    // Instance sẽ được quản lý/dispose trong AppSidebar.vue onBeforeUnmount
  }
  // sidebarInstance = null; // Không reset ở đây
});
</script>

<style>
/* --- Global Styles --- */
body {
  /* ... */
}

#app {
  /* ... */
}

/* === Transitions & Shifting === */
.main-content,
.app-footer {
  /* Thêm .app-footer vào đây */
  /* Đồng bộ transition với Bootstrap Offcanvas */
  transition: margin-left 0.3s ease;
}

/* Page transition */
.fade-enter-active,
.fade-leave-active {
  /* ... */
}

.fade-enter-from,
.fade-leave-to {
  /* ... */
}


/* === Desktop Styles === */
@media (min-width: 992px) {

  /* Áp dụng margin khi sidebar mở */
  .main-content.shifted-by-sidebar,
  .app-header.shifted-by-sidebar,
  .app-footer.shifted-by-sidebar {
    margin-left: 280px;
    /* Bằng chiều rộng sidebar */
  }

  /* Ngăn backdrop của Bootstrap hiển thị trên desktop */
  .offcanvas-backdrop {
    display: none !important;
  }

  /* Đảm bảo sidebar hiển thị đúng cách (thường không cần nhưng để chắc chắn) */
  #appSidebar.offcanvas.show {
    visibility: visible !important;
  }
}

/* === Mobile Styles === */
@media (max-width: 991.98px) {

  /* Reset margin trên mobile */
  .main-content.shifted-by-sidebar,
  .app-header.shifted-by-sidebar,
  .app-footer.shifted-by-sidebar {
    margin-left: 0;
  }

  /* Backdrop sẽ tự hiển thị trên mobile theo mặc định của Bootstrap */
}
</style>
