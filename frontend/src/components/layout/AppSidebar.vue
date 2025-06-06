<template>
  <div
    class="offcanvas offcanvas-start text-bg-primary sidebar-container"
    tabindex="-1"
    id="appSidebar"
    aria-labelledby="appSidebarLabel"
    data-bs-scroll="true"
    data-bs-backdrop="true"
    data-bs-touch="false"
  >
    <div class="offcanvas-header">
      <h5 class="offcanvas-title" id="appSidebarLabel">
        <router-link
          :to="{ name: 'home' }"
          class="text-white text-decoration-none fw-bold sidebar-brand"
        >
          <img
            class="img-logosidebar"
            src="https://doanhnhantredaklak.org/userfiles/users/107/1609494305008.png"
            alt=""
          />
          Tượng Gỗ Phong Thủy
        </router-link>
      </h5>
      <button
        type="button"
        class="btn-close btn-close-white"
        data-bs-dismiss="offcanvas"
        aria-label="Đóng menu"
      ></button>
    </div>
    <div class="offcanvas-body d-flex flex-column">
      
      <ul class="nav nav-pills flex-column mb-auto sidebar-nav">
        <li class="nav-item">
          <router-link class="nav-link" :to="{ name: 'home' }" active-class="active">
            <i class="bi bi-house-door nav-icon"></i>
            <span>Trang chủ</span>
          </router-link>
        </li>

        
        <li class="nav-item">
          <a
            class="nav-link has-submenu"
            href="#"
            @click.prevent="toggleProductSubmenu"
            :class="{ active: isProductSubmenuRouteActive, open: isProductSubmenuOpen }"
            role="button"
            :aria-expanded="isProductSubmenuOpen"
            aria-controls="productSubmenuVue"
          >
            <i class="bi bi-tags nav-icon"></i>
            <span class="product-label">Sản phẩm</span>
            <i class="bi bi-chevron-right submenu-arrow"></i>
          </a>
          
          <div
            class="collapse-transition"
            :style="{ '--max-height': isProductSubmenuOpen ? productSubmenuHeight : '0px' }"
          >
            <div ref="productSubmenuContent" class="submenu-wrapper" id="productSubmenuVue">
              <ul class="nav flex-column small">
                <li class="nav-item-for-product">
                  <router-link
                    class="nav-link submenu-link"
                    :to="{ name: 'productList' }"
                    active-class="active"
                  >
                    Tất cả sản phẩm
                  </router-link>
                </li>
                
                <li
                  v-for="category in categories.slice(0, categoriesToShow)"
                  :key="'side-' + category.id"
                  class="nav-item-for-product"
                >
                  <router-link
                    class="nav-link submenu-link"
                    :to="{ name: 'productListByCategory', params: { categorySlug: category.slug } }"
                    active-class="active"
                  >
                    {{ category.name }}
                  </router-link>
                </li>
                
                <li v-if="categories.length > 5" class="nav-item">
                  <button
                    class="nav-link submenu-link text-start w-100 bg-transparent border-0"
                    @click="toggleShowMoreCategories"
                  >
                    <em>{{ categoriesToShow === 5 ? "Xem thêm..." : "Thu gọn..." }}</em>
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" :to="{ name: 'articleList' }" active-class="active">
            <i class="bi bi-newspaper nav-icon"></i>
            <span>Tin Tức</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :to="{ name: 'contact' }" active-class="active">
            <i class="bi bi-telephone nav-icon"></i>
            <span>Liên hệ</span>
          </router-link>
        </li>
      </ul>

      
      <hr class="sidebar-divider" />

      
      <div class="user-section">
        
        <div v-if="authStore.isAuthenticated" class="user-dropdown-wrapper">
          <a
            href="#"
            class="d-flex align-items-center text-white text-decoration-none user-dropdown-toggle"
            @click.prevent="toggleUserMenu"
            role="button"
            :aria-expanded="isUserMenuOpen"
            aria-controls="userMenuVue"
          >
            <i class="bi bi-person-circle user-avatar me-2"></i>
            <strong class="user-name">{{ authStore.user?.username || "Tài khoản" }}</strong>
            <i class="bi bi-chevron-down user-arrow ms-auto"></i>
          </a>
          
          <div
            class="collapse-transition"
            :style="{ '--max-height': isUserMenuOpen ? userMenuHeight : '0px' }"
          >
            <ul ref="userMenuContent" id="userMenuVue" class="list-unstyled user-dropdown-menu">
              <template v-if="authStore.isAdmin">
                <li>
                  <router-link class="user-dropdown-item" :to="{ name: 'adminDashboard' }">
                    <i class="bi bi-speedometer2 item-icon"></i><span>Thống kê</span>
                  </router-link>
                </li>
                <li>
                  <router-link class="user-dropdown-item" :to="{ name: 'adminOrderList' }">
                    <i class="bi bi-box-seam item-icon"></i><span>Đơn hàng</span>
                  </router-link>
                </li>
                <li>
                  <router-link class="user-dropdown-item" :to="{ name: 'adminProductList' }">
                    <i class="bi bi-tags item-icon"></i><span>Sản phẩm</span>
                  </router-link>
                </li>
                <li>
                  <hr class="user-dropdown-divider" />
                </li>
              </template>
              <template v-else>
                
                <li>
                  <router-link class="user-dropdown-item" :to="{ name: 'orderHistory' }">
                    <i class="bi bi-receipt item-icon"></i><span>Đơn hàng của tôi</span>
                  </router-link>
                </li>
              </template>
              
              <li>
                <router-link class="user-dropdown-item" :to="{ name: 'userProfile' }">
                  <i class="bi bi-person-lines-fill item-icon"></i><span>Thông tin tài khoản</span>
                </router-link>
              </li>
              <li>
                <hr class="user-dropdown-divider" />
              </li>
              <li>
                <button
                  class="user-dropdown-item text-danger w-100"
                  type="button"
                  @click="handleLogoutAndCloseSidebar"
                >
                  <i class="bi bi-box-arrow-right item-icon"></i><span>Đăng xuất</span>
                </button>
              </li>
            </ul>
          </div>
        </div>
        
        <div v-else class="guest-actions">
          <router-link :to="{ name: 'login' }" class="btn btn-outline-light btn-sm me-2"
            >Đăng nhập
          </router-link>
          <router-link :to="{ name: 'register' }" class="btn btn-warning btn-sm"
            >Đăng ký
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed, nextTick, onBeforeUnmount } from "vue";
import { RouterLink, useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { getAllCategories } from "@/http/modules/public/categoryService.js";
import { Offcanvas } from "bootstrap";

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();
const categories = ref([]);
const sidebarInstance = ref(null);


const isProductSubmenuOpen = ref(false);
const isUserMenuOpen = ref(false);


const productSubmenuContent = ref(null);
const userMenuContent = ref(null);
const productSubmenuHeight = ref("0px");
const userMenuHeight = ref("0px");


const categoriesToShow = ref(5);

const toggleShowMoreCategories = () => {
  categoriesToShow.value = categoriesToShow.value === 5 ? categories.value.length : 5;

  nextTick(() => {
    calculateProductSubmenuHeight();
  });
};


const fetchCategories = async () => {
  try {
    const response = await getAllCategories();
    categories.value = response.data || [];
  } catch (err) {
    console.error("Sidebar: Error fetching categories:", err);
  }
};



const getSidebarInstance = () => {
  if (!sidebarInstance.value) {
    const sidebarElement = document.getElementById("appSidebar");
    if (sidebarElement) {

      sidebarInstance.value = Offcanvas.getOrCreateInstance(sidebarElement);
    }
  }
  return sidebarInstance.value;
};


const closeSidebar = () => {
  isProductSubmenuOpen.value = false;
  isUserMenuOpen.value = false;

  const instance = getSidebarInstance();



  if (instance) {
    const sidebarElement = document.getElementById("appSidebar");

    if (sidebarElement && sidebarElement.classList.contains("show")) {
      instance.hide();
    }
  } else {
    console.warn("Sidebar instance not found, couldn't close programmatically.");
  }
};


const calculateProductSubmenuHeight = () => {
  if (productSubmenuContent.value) {
    productSubmenuHeight.value = `${productSubmenuContent.value.scrollHeight}px`;
  }
};

const calculateUserMenuHeight = () => {
  if (userMenuContent.value) {
    userMenuHeight.value = `${userMenuContent.value.scrollHeight}px`;
  }
};


const toggleProductSubmenu = () => {
  isProductSubmenuOpen.value = !isProductSubmenuOpen.value;
  if (isProductSubmenuOpen.value) {
    nextTick(calculateProductSubmenuHeight);
  }
};

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value;
  if (isUserMenuOpen.value) {
    nextTick(calculateUserMenuHeight);
  }
};


const handleLogoutAndCloseSidebar = () => {
  closeSidebar();
  authStore.logout();
  router.push({ name: "home" }).catch(() => {});
};


const isProductSubmenuRouteActive = computed(() => {
  return route.name === "productList" || route.name === "productListByCategory";
});


watch(route, () => {





});


watch(
  () => authStore.isAuthenticated,
  (newVal, oldVal) => {
    if (newVal !== oldVal) {

      if (isUserMenuOpen.value) {
        nextTick(calculateUserMenuHeight);
      }
    }
  }
);


const handleResize = () => {
  if (isProductSubmenuOpen.value) calculateProductSubmenuHeight();
  if (isUserMenuOpen.value) calculateUserMenuHeight();
};

onMounted(async () => {
  await fetchCategories();
  getSidebarInstance();
  calculateProductSubmenuHeight();
  calculateUserMenuHeight();


  const sidebarElement = document.getElementById("appSidebar");
  if (sidebarElement) {
    sidebarElement.addEventListener("hidden.bs.offcanvas", () => {
      isProductSubmenuOpen.value = false;
      isUserMenuOpen.value = false;
    });
  }


  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {

  window.removeEventListener("resize", handleResize);


  const instance = sidebarInstance.value;
  if (instance) {
    instance.dispose();
    console.log("Sidebar instance disposed.");
  }
  sidebarInstance.value = null;


  const sidebarElement = document.getElementById("appSidebar");
  if (sidebarElement) {






  }
});
</script>

<style scoped>

.sidebar-container {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1045; 
  background-color: var(--color-primary-dark, #4b2c20) !important; 
  color: rgba(255, 255, 255, 0.85) !important; 
  width: 280px !important;
  border-right: 1px solid rgba(255, 255, 255, 0.1); 
  
  
}

.offcanvas-header {
  padding: 1rem 1.25rem;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1); 
  align-items: center;
}


.offcanvas-header .btn-close {
  padding: 0.5rem 0.5rem;
  margin: -0.5rem -0.5rem -0.5rem auto; 
  filter: invert(1) grayscale(100%) brightness(200%); 
  opacity: 0.7; 
  transition: opacity 0.2s ease-in-out;
}
.offcanvas-header .btn-close:hover {
  opacity: 1; 
}

.sidebar-brand {
  font-family: var(--font-family-heading, "Segoe UI", Tahoma, Geneva, Verdana, sans-serif);
  font-size: 1.25rem; 
  font-weight: 600; 
}

.sidebar-brand i {
  vertical-align: -0.1em;
  color: var(--color-accent, #d4af37); 
}

.offcanvas-body {
  padding: 0; 
  display: flex;
  flex-direction: column;
  
  overflow-y: auto;
  overflow-x: hidden; 
}


.sidebar-nav {
  padding: 0.5rem 0; 
}

.sidebar-nav .nav-item {
  
  margin-left: -10px;
  margin-bottom: 2px;
  border-bottom: 1px solid gray;
}

.sidebar-nav .nav-link {
  color: rgba(255, 255, 255, 0.8);
  padding: 0.8rem 1.25rem; 
  
  
  transition: background-color 0.2s ease, color 0.2s ease, padding-left 0.3s ease;
  font-weight: 500;
  display: flex;
  align-items: center;
  position: relative; 
  text-decoration: none;
  white-space: nowrap; 
}

.sidebar-nav .nav-link .nav-icon {
  width: 1.25em; 
  font-size: 1.1rem;
  margin-right: 0.9rem;
  text-align: center;
  flex-shrink: 0; 
  transition: color 0.3s ease;
}

.sidebar-nav .nav-link span {
  flex-grow: 1; 
}

.sidebar-nav .nav-link:hover {
  background-color: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.sidebar-nav .nav-link.active {
  background-color: var(--color-accent, #d4af37);
  color: var(--color-primary-dark, #40241a);
  font-weight: 600;
}

.sidebar-nav .nav-link.active .nav-icon {
  color: var(--color-primary-dark, #40241a);
}


.nav-link.has-submenu .submenu-arrow {
  margin-left: auto;
  transition: transform 0.3s ease;
  font-size: 0.75em;
  opacity: 0.7;
}

.nav-link.has-submenu.open .submenu-arrow {
  transform: rotate(90deg);
}


.nav-link.has-submenu.open,
.nav-link.has-submenu.active {
  
  background-color: rgba(255, 255, 255, 0.06);
  color: #fff;
}


.sidebar-nav .nav-link.has-submenu.active {
  background-color: var(--color-accent, #d4af37);
  color: var(--color-primary-dark, #40241a);
}

.sidebar-nav .nav-link.has-submenu.active .submenu-arrow {
  opacity: 1;
}


.collapse-transition {
  overflow: hidden;
  transition: max-height 0.35s ease-in-out; 
  max-height: var(--max-height, 0px); 
}

.submenu-wrapper {
  padding: 0.3rem 0 0.5rem 0; 
  
}

.submenu-wrapper .nav-link.submenu-link {
  padding: 0.4rem 1.25rem 0.4rem 3rem; 
  font-size: 0.88rem;
  color: rgba(255, 255, 255, 0.7);
  position: relative;
}


.submenu-wrapper .nav-link.submenu-link.active {
  color: var(--color-accent-light, #f0d882); 
  background-color: transparent;
  font-weight: 500;
}

.submenu-wrapper .nav-link.submenu-link.active::before {
  content: "";
  position: absolute;
  left: 1.25rem; 
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px; 
  background-color: var(--color-accent-light, #f0d882);
  border-radius: 3px;
}

.submenu-wrapper .nav-link.submenu-link:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.05);
}

.submenu-wrapper .nav-item button.submenu-link {
  font-style: italic;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.6);
}

.submenu-wrapper .nav-item button.submenu-link:hover {
  color: rgba(255, 255, 255, 0.9);
}


.sidebar-divider {
  margin: 1rem 1.25rem; 
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}


.user-section {
  padding: 0 1.25rem 1rem 1.25rem; 
  margin-top: auto; 
}

.user-dropdown-wrapper {
  position: relative;
}

.user-dropdown-toggle {
  cursor: pointer;
  padding: 0.75rem 0; 
  display: flex !important;
  align-items: center !important;
  border-radius: var(--border-radius-sm, 0.25rem);
  transition: background-color 0.2s ease;
}

.user-dropdown-toggle:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

.user-dropdown-toggle .user-avatar {
  font-size: 1.8rem; 
  opacity: 0.9;
}

.user-dropdown-toggle .user-name {
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 150px; 
}

.user-dropdown-toggle .user-arrow {
  transition: transform 0.3s ease;
  font-size: 0.8em;
  opacity: 0.7;
}


.user-dropdown-toggle[aria-expanded="true"] .user-arrow {
  transform: rotate(180deg);
}

.user-dropdown-toggle[aria-expanded="true"] {
  background-color: rgba(255, 255, 255, 0.08);
}

.user-dropdown-menu {
  
  border-radius: var(--border-radius-md, 0.375rem);
  padding: 0.5rem 0;
  margin: 0; 
  list-style: none;
}

.user-dropdown-item {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.6rem 1rem; 
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  background-color: transparent;
  border-top: 0px;
  border-right: 0px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  border-left: 0px;
  text-align: left;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease;
  cursor: pointer;
  border-radius: var(--border-radius-sm, 0.25rem);
  white-space: nowrap;
}

.user-dropdown-item .item-icon {
  width: 1.25em; 
  text-align: center;
  margin-right: 0.8rem;
  opacity: 0.8;
  flex-shrink: 0;
}

.user-dropdown-item span {
  flex-grow: 1;
}

.user-dropdown-item:hover,
.user-dropdown-item:focus {
  background-color: rgba(255, 255, 255, 0.1);
  color: #fff;
  outline: none;
}

.user-dropdown-item.text-danger {
  color: #f8a5a5 !important; 
}

.user-dropdown-item.text-danger .item-icon {
  color: #f8a5a5 !important;
  opacity: 1;
}

.user-dropdown-item.text-danger:hover {
  background-color: rgba(220, 53, 69, 0.2);
  color: #ffcdcd !important;
}

.user-dropdown-item.text-danger:hover .item-icon {
  color: #ffcdcd !important;
}

.user-dropdown-divider {
  height: 1px;
  margin: 0.5rem 1rem; 
  overflow: hidden;
  background-color: rgba(255, 255, 255, 0.1);
  border: 0;
}


.guest-actions {
  padding: 0.75rem 0; 
  display: flex;
  justify-content: center; 
}

.guest-actions .btn-sm {
  font-size: 0.85rem;
  padding: 0.4rem 0.8rem;
}

.guest-actions .btn-outline-light {
  border-color: rgba(255, 255, 255, 0.5);
  color: rgba(255, 255, 255, 0.8);
}

.guest-actions .btn-outline-light:hover {
  border-color: #fff;
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
}

.guest-actions .btn-warning {
  color: #333; 
}


.offcanvas-body::-webkit-scrollbar {
  width: 6px;
}

.offcanvas-body::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.offcanvas-body::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  transition: background-color 0.2s ease;
}

.offcanvas-body::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.nav-item-for-product {
  margin-left: 20px;
}

.img-logosidebar {
  width: 20px; 
  height: 20px;
  object-fit: contain; 
  display: inline-block; 
  vertical-align: middle; 
  margin-right: 0.5rem; 
}
</style>

