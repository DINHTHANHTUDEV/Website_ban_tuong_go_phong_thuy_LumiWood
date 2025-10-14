<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top app-header">
    <div class="container">
      <button class="btn btn-white fixed-sidebar-toggle d-none d-lg-flex align-items-center" type="button"
        data-bs-toggle="offcanvas" data-bs-target="#appSidebar" aria-controls="appSidebar"
        aria-label="Mở menu điều hướng">
        <i class="bi bi-list me-2"></i>


      </button>


      <router-link class="navbar-brand fw-bold app-logo" :to="{ name: 'home' }">
        <img src="https://doanhnhantredaklak.org/userfiles/users/107/1609494305008.png" alt="Logo"
          class="logo-img me-2" />
        Tượng Gỗ Phong Thủy
      </router-link>


      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbarContent"
        aria-controls="mainNavbarContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>


      <div class="collapse navbar-collapse" id="mainNavbarContent">

        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link px-lg-3" :to="{ name: 'home' }" active-class="active">
              Trang chủ
            </router-link>
          </li>


          <li class="nav-item dropdown" @mouseenter="openProductDropdown" @mouseleave="startCloseProductDropdownTimer">
            <a ref="productDropdownTrigger" class="nav-link dropdown-toggle px-lg-3" href="#" role="button"
              :class="{ active: isProductRouteActive || isProductDropdownOpen }" :aria-expanded="isProductDropdownOpen"
              @click.prevent="handleDropdownTriggerClick($event, 'product')" aria-haspopup="true">
              Sản phẩm
            </a>
            <ul ref="productDropdownMenu" class="dropdown-menu shadow-sm fade" :class="{ show: isProductDropdownOpen }"
              @mouseenter="cancelCloseProductDropdownTimer" @mouseleave="startCloseProductDropdownTimer"
              @click="closeProductDropdownOnClick">
              <li>
                <router-link class="dropdown-item" :to="{ name: 'productList' }" active-class="active">
                  Tất cả sản phẩm
                </router-link>
              </li>
              <li v-if="!loadingCategories && categories.length > 0">
                <hr class="dropdown-divider" />
              </li>
              <li v-if="loadingCategories" class="dropdown-item disabled text-center small py-2">
                <div class="spinner-border spinner-border-sm text-secondary" role="status">
                  <span class="visually-hidden">Đang tải...</span>
                </div>
              </li>
              <li v-else-if="categoryError" class="dropdown-item disabled">
                <span class="text-danger small">{{ categoryError }}</span>
              </li>
              <li v-else v-for="category in categories" :key="category.id">
                <router-link class="dropdown-item"
                  :to="{ name: 'productListByCategory', params: { categorySlug: category.slug } }"
                  active-class="active">
                  {{ category.name }}
                </router-link>
              </li>
              <li v-if="!loadingCategories && !categoryError && categories.length === 0" class="dropdown-item disabled">
                <span class="text-muted small">Không có danh mục.</span>
              </li>
            </ul>
          </li>


          <li class="nav-item">
            <router-link class="nav-link px-lg-3" :to="{ name: 'articleList' }" active-class="active">
              Tin Tức
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link px-lg-3" :to="{ name: 'contact' }" active-class="active">
              Liên hệ
            </router-link>
          </li>
        </ul>


        <div class="d-none d-lg-flex mx-lg-3 header-search-desktop">
          <SearchBar />
        </div>


        <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center header-actions">

          <template v-if="!authStore.isAuthenticated">
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'login' }" active-class="active">
                <i class="bi bi-box-arrow-in-right me-1"></i> Đăng nhập
              </router-link>
            </li>
            <li class="nav-item ms-lg-2">
              <router-link class="btn btn-outline-primary btn-sm" :to="{ name: 'register' }">
                <i class="bi bi-person-plus-fill me-1"></i> Đăng ký
              </router-link>
            </li>
          </template>
          <template v-else>

            <li class="nav-item dropdown" @mouseenter="openUserDropdown" @mouseleave="startCloseUserDropdownTimer">
              <a ref="userDropdownTrigger" class="nav-link dropdown-toggle d-flex align-items-center" href="#"
                role="button" :class="{ active: isUserDropdownOpen }" :aria-expanded="isUserDropdownOpen"
                @click.prevent="handleDropdownTriggerClick($event, 'user')" aria-haspopup="true">
                <i class="bi bi-person-circle fs-5 me-1"></i>
                <span class="d-none d-lg-inline">Chào, {{ authStore.user?.username || "bạn" }}</span>
                <span class="d-inline d-lg-none">Tài khoản</span>
              </a>
              <ul ref="userDropdownMenu" class="dropdown-menu dropdown-menu-end shadow-sm fade"
                :class="{ show: isUserDropdownOpen }" @mouseenter="cancelCloseUserDropdownTimer"
                @mouseleave="startCloseUserDropdownTimer" @click="closeUserDropdownOnClick">

                <template v-if="authStore.isAdmin">
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminDashboard' }">
                      <i class="bi bi-speedometer2"></i> Thống kê
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminOrderList' }">
                      <i class="bi bi-box-seam"></i> Quản lý Đơn hàng
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminProductList' }">
                      <i class="bi bi-tags"></i> Quản lý Sản phẩm
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminUserList' }">
                      <i class="bi bi-people nav-icon"></i> Quản lý khách hàng
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminShippingMethodList' }">
                      <i class="bi bi-truck nav-icon"></i> Quản lý vận chuyển
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminPromotionList' }">
                      <i class="bi bi-gift nav-icon"></i> Quản lý khuyến mãi
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminArticleList' }">
                      <i class="bi bi-journal-text nav-icon"></i> Quản lý bài viết
                    </router-link>
                  </li>
                  <!-- Thêm mục POS (bán hàng tại cửa hàng) -->
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'adminPOS' }">
                      <i class="bi bi-cash nav-icon"></i> POS (bán hàng tại cửa hàng)
                    </router-link>
                  </li>
                  <li>
                    <hr class="dropdown-divider" />
                  </li>
                </template>


                <template v-if="!authStore.isAdmin">
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'userProfile' }">
                      <i class="bi bi-person-lines-fill"></i> Tài khoản của tôi
                    </router-link>
                  </li>
                  <li>
                    <router-link class="dropdown-item" :to="{ name: 'orderHistory' }">
                      <i class="bi bi-receipt"></i> Đơn hàng của tôi
                    </router-link>
                  </li>
                </template>


                <li>
                  <hr class="dropdown-divider" />
                </li>
                <li>
                  <button class="dropdown-item text-danger d-flex align-items-center" type="button"
                    @click="handleLogout">
                    <i class="bi bi-box-arrow-right"></i> Đăng xuất
                  </button>
                </li>
              </ul>
            </li>

          </template>



          <li class="nav-item ms-lg-3" v-if="!authStore.isAdmin">
            <router-link class="nav-link position-relative cart-icon-link" :to="{ name: 'shoppingCart' }"
              title="Giỏ hàng">
              <i class="bi bi-cart-fill fs-5"></i>
              <span v-if="cartStore.totalItemsCount > 0"
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill cart-badge">
                {{ cartStore.totalItemsCount > 99 ? "99+" : cartStore.totalItemsCount }}
                <span class="visually-hidden">sản phẩm</span>
              </span>
            </router-link>
          </li>

          <!-- 🔔 Nút thông báo chung cho cả admin và user -->
          <li class="nav-item dropdown ms-lg-3">
            <a class="nav-link position-relative" href="#" role="button" @click.prevent="toggleNotificationDropdown">
              <i class="bi bi-bell-fill fs-5"></i>

              <!-- 🔴 Số lượng chưa đọc -->
              <span v-if="unreadNotifications.length > 0"
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                {{ unreadNotifications.length > 99 ? '99+' : unreadNotifications.length }}
                <span class="visually-hidden">thông báo chưa đọc</span>
              </span>
            </a>

            <!-- 📩 Danh sách thông báo -->
            <ul class="dropdown-menu dropdown-menu-end shadow fade" :class="{ show: isNotificationDropdownOpen }"
              style="width: 380px; max-height: 460px; overflow-y: auto;">

              <!-- 💤 Không có thông báo -->
              <li v-if="notifications.length === 0" class="dropdown-item text-muted text-center">
                Không có thông báo.
              </li>

              <!-- 🔔 Thông báo -->
              <li v-for="(notification, index) in notifications" :key="notification.id || index"
                class="dropdown-item notification-item"
                :class="[getNotificationClass(notification.content), { 'fw-bold': !notification.read }]"
                @click="viewNotificationDetail(notification)">
                <div class="title">{{ notification.title }}</div>
                <div class="content">{{ notification.content }}</div>
                <div class="time">{{ formatDate(notification.createdTime) }}</div>
              </li>

              <!-- 🧹 Xóa tất cả -->
              <li v-if="notifications.length > 0" class="dropdown-item text-end">
                <button class="btn btn-sm btn-outline-danger" @click="notifications = []">
                  🗑️ Xóa tất cả
                </button>
              </li>
            </ul>
          </li>







        </ul>


        <div class="d-lg-none my-2 header-search-mobile">
          <SearchBar />
        </div>
      </div>

    </div>

  </nav>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed, watch } from "vue";
import { RouterLink, useRouter, useRoute } from "vue-router";
import SearchBar from "./SearchBar.vue";
import { useAuthStore } from "@/store/auth";
import { useCartStore } from "@/store/cart";
import { getAllCategories } from "@/http/modules/public/categoryService.js";
import { connectNotificationSocket, disconnectNotificationSocket } from "@/utils/notificationSocket";

const authStore = useAuthStore();
const cartStore = useCartStore();
const router = useRouter();
const route = useRoute();

const categories = ref([]);
const loadingCategories = ref(false);
const categoryError = ref(null);

const isProductDropdownOpen = ref(false);
const isUserDropdownOpen = ref(false);
const productCloseTimer = ref(null);
const userCloseTimer = ref(null);
const closeDelay = 200;

const productDropdownTrigger = ref(null);
const productDropdownMenu = ref(null);
const userDropdownTrigger = ref(null);
const userDropdownMenu = ref(null);

// ✅ Danh sách thông báo từ WebSocket
const notifications = ref([]);

// ✅ Trạng thái hiển thị dropdown thông báo
const isNotificationDropdownOpen = ref(false);

// ✅ Kiểm tra đã khởi tạo WebSocket hay chưa
const websocketInitialized = ref(false);

// ✅ Danh sách thông báo chưa đọc
const unreadNotifications = computed(() =>
  notifications.value.filter((n) => !n.read)
);

// ========================== Dropdown xử lý ==========================
const openProductDropdown = () => {
  cancelCloseProductDropdownTimer();
  if (isUserDropdownOpen.value) closeUserDropdown();
  isProductDropdownOpen.value = true;
};
const closeProductDropdown = () => {
  isProductDropdownOpen.value = false;
};
const startCloseProductDropdownTimer = () => {
  cancelCloseProductDropdownTimer();
  productCloseTimer.value = setTimeout(closeProductDropdown, closeDelay);
};
const cancelCloseProductDropdownTimer = () => {
  if (productCloseTimer.value) {
    clearTimeout(productCloseTimer.value);
    productCloseTimer.value = null;
  }
};

const openUserDropdown = () => {
  cancelCloseUserDropdownTimer();
  if (isProductDropdownOpen.value) closeProductDropdown();
  isUserDropdownOpen.value = true;
};
const closeUserDropdown = () => {
  isUserDropdownOpen.value = false;
};
const startCloseUserDropdownTimer = () => {
  cancelCloseUserDropdownTimer();
  userCloseTimer.value = setTimeout(closeUserDropdown, closeDelay);
};
const cancelCloseUserDropdownTimer = () => {
  if (userCloseTimer.value) {
    clearTimeout(userCloseTimer.value);
    userCloseTimer.value = null;
  }
};
const closeProductDropdownOnClick = (event) => {
  if (event.target.closest(".dropdown-item")) {
    closeProductDropdown();
  }
};
const closeUserDropdownOnClick = (event) => {
  if (event.target.closest(".dropdown-item")) {
    closeUserDropdown();
  }
};
const handleDropdownTriggerClick = (event, type) => {
  event.preventDefault();
};

// ========================== Xác định route sản phẩm ==========================
const isProductRouteActive = computed(() => {
  return route.name === "productList" || route.name === "productListByCategory";
});

// ========================== Lấy danh mục ==========================
const fetchCategories = async () => {
  if (loadingCategories.value) return;
  loadingCategories.value = true;
  categoryError.value = null;
  try {
    const response = await getAllCategories();
    if (Array.isArray(response?.data)) {
      categories.value = response.data;
    } else {
      console.warn("⚠️ API trả về danh mục không hợp lệ:", response?.data);
      categoryError.value = "Dữ liệu danh mục không hợp lệ.";
    }
  } catch (err) {
    console.error("❌ Lỗi tải danh mục:", err);
    categoryError.value = "Lỗi tải danh mục. Vui lòng thử lại.";
    categories.value = [];
  } finally {
    loadingCategories.value = false;
  }
};

// ========================== Đăng xuất ==========================
function handleLogout() {
  console.log("🚪 Đang xử lý đăng xuất...");
  closeUserDropdown();

  authStore.logout(); // Xoá user + token
  router.push({ name: "home" }).catch((err) => {
    if (
      err.name !== "NavigationDuplicated" &&
      !err.message.includes("Avoided redundant navigation")
    ) {
      console.error("❌ Lỗi điều hướng khi logout:", err);
    }
  });

  if (websocketInitialized.value) {
    disconnectNotificationSocket();
    websocketInitialized.value = false;
    console.log("❌ Đã ngắt WebSocket sau khi logout.");
  }
}

// ========================== Khi Mounted ==========================
onMounted(() => {
  fetchCategories();

  if (cartStore.items.length === 0 && !cartStore.isLoading && !cartStore.error) {
    cartStore.fetchCart().catch((err) => {
      console.error("🛒 Lỗi tải giỏ hàng ban đầu:", err);
    });
  }

  if (authStore.isAuthenticated && authStore.user?.username && !websocketInitialized.value) {
    websocketInitialized.value = true;
    const username = authStore.user.username;
    console.log("🔌 (onMounted) Kết nối WebSocket với username:", username);

    connectNotificationSocket((notification) => {
      console.log("📥 (onMounted) Nhận thông báo:", notification);
      notifications.value.unshift({
        ...notification,
        read: false,
        createdTime: notification.createdTime || new Date(),
      });
    });
  } else {
    console.log("ℹ️ Không kết nối WebSocket (chưa login hoặc đã kết nối).");
  }
});

// ========================== Khi login sau này ==========================
watch(
  () => authStore.user?.username,
  (username) => {
    if (authStore.isAuthenticated && username && !websocketInitialized.value) {
      websocketInitialized.value = true;
      console.log("🔌 (watch) Kết nối WebSocket sau login với username:", username);

      connectNotificationSocket((notification) => {
        console.log("📥 (watch) Nhận thông báo:", notification);
        notifications.value.unshift({
          ...notification,
          read: false,
          createdTime: notification.createdTime || new Date(),
        });
      });
    }
  },
  { immediate: true }
);

// ========================== Ngắt kết nối WebSocket khi component bị huỷ ==========================
onBeforeUnmount(() => {
  cancelCloseProductDropdownTimer();
  cancelCloseUserDropdownTimer();

  if (websocketInitialized.value) {
    disconnectNotificationSocket();
    websocketInitialized.value = false;
    console.log("❌ Đã ngắt kết nối WebSocket (beforeUnmount).");
  }
});

// ========================== Xem chi tiết thông báo ==========================
function viewNotificationDetail(notification) {
  notification.read = true;
  isNotificationDropdownOpen.value = false;
  console.log("👁️ Đã xem thông báo:", notification);

  if (notification.orderId) {
    router.push({ name: "orderDetail", params: { id: notification.orderId } }).catch((err) => {
      console.warn("⚠️ Lỗi chuyển hướng đơn hàng:", err);
    });
  }
}

// ========================== Toggle dropdown thông báo ==========================
function toggleNotificationDropdown() {
  isNotificationDropdownOpen.value = !isNotificationDropdownOpen.value;
}

// ========================== Format ngày giờ ==========================
function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("vi-VN", {
    dateStyle: "long",
    timeStyle: "long",
  });
}

// ========================== hàm thay đổi màu thông báo ==========================

function getNotificationClass(content) {
  if (!content) return "";

  const msg = content.toLowerCase();

  if (msg.includes("processing")) return "status-processing";
  if (msg.includes("shipping")) return "status-shipping";
  if (msg.includes("delivery_failed")) return "status-failed";
  if (msg.includes("completed")) return "status-success";
  if (msg.includes("cancelled")) return "status-cancelled";
  if (msg.includes("payment_pending")) return "status-payment";
  if (msg.includes("pending")) return "status-pending";

  return "status-default";
}


</script>






<style scoped>
.nav-link.dropdown-toggle {
  cursor: pointer;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 1030;


  transition: margin-left 0.3s ease, background-color 0.3s ease, box-shadow 0.3s ease;
}

.logo-img {
  height: 40px;
  width: auto;
}

.header-search-desktop {
  flex-grow: 1;
  max-width: 450px;
}

.header-search-mobile {}

.header-actions .nav-link,
.header-actions .btn {}


.navbar-nav .nav-link.active,
.navbar-nav .nav-link.router-link-exact-active {
  font-weight: 600;
  color: var(--color-primary, #6d4c41);
  background-color: rgba(var(--color-primary-rgb, 109, 76, 65), 0.05);
}

.dropdown-item.active,
.dropdown-item.router-link-active {
  font-weight: 600;
  background-color: rgba(var(--color-primary-rgb, 109, 76, 65), 0.1);
  color: var(--color-primary, #6d4c41);
}


.dropdown-menu {
  transition: opacity 0.15s linear;
  opacity: 0;
  visibility: hidden;
  position: absolute;
  z-index: 1000;
  margin-top: 0.5rem !important;
}

.dropdown-menu.show {
  opacity: 1;
  visibility: visible;
}

.dropdown-menu .dropdown-item {}

.dropdown-menu .dropdown-item.disabled {
  cursor: default;
  opacity: 0.7;
  background-color: transparent;
}

.navbar-nav .btn-sm {
  padding: 0.25rem 0.6rem;
  font-size: 0.875rem;
}

.nav-link i,
.dropdown-item i {
  vertical-align: -0.125em;
  margin-right: 0.4rem;
}

.dropdown-item i {
  width: 1.3em;
  text-align: center;
  margin-right: 0.5rem;
}

.dropdown-item.text-danger i {
  color: var(--bs-danger);
}

.dropdown-menu-end {
  right: 0;
  left: auto;
}

.cart-icon-link {}

.cart-badge {
  font-size: 0.65em;
  padding: 0.3em 0.55em;
  background-color: var(--bs-danger);
  color: white;
  border: 1px solid white;
}



.navbar {

  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
}

.navbar-brand {
  color: var(--color-primary-dark, #4b2c20);
  font-weight: 700 !important;
  font-family: var(--font-family-heading, sans-serif);
  font-size: 1.5rem;
  transition: color 0.2s ease;
}

.navbar-brand:hover {
  color: var(--color-primary, #6d4c41);
}

.navbar-nav .nav-link {
  color: var(--color-text-muted, #6c757d);
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: var(--border-radius-md, 0.375rem);
  transition: color 0.2s ease, background-color 0.2s ease;
  position: relative;
}

.navbar-nav .nav-link:hover {

  color: var(--color-primary, #6d4c41);
  background-color: rgba(var(--color-primary-rgb, 109, 76, 65), 0.05);
}

.dropdown-menu {
  border-radius: var(--border-radius-lg, 0.5rem);
  border: var(--border-width, 1px) solid var(--color-border, #dee2e6);
  box-shadow: var(--box-shadow-md, 0 0.5rem 1rem rgba(0, 0, 0, 0.15));
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.dropdown-item {
  color: var(--color-text, #212529);
  padding: 0.5rem 1.25rem;
  transition: background-color 0.2s ease, color 0.2s ease;
  display: block;
  width: 100%;
  clear: both;
  font-weight: 400;
  text-align: inherit;
  white-space: nowrap;
  background-color: transparent;
  border: 0;
}

.dropdown-item:hover,
.dropdown-item:focus {
  background-color: var(--color-secondary, #e9ecef);
  color: var(--color-primary, #6d4c41);
}

.dropdown-divider {
  border-top-color: var(--color-border, #dee2e6);
  margin: 0.5rem 0;
}


.navbar-nav .nav-link .bi {
  font-size: 1.3rem;
  vertical-align: middle;
}

.navbar-nav .nav-link.position-relative .badge {
  border: 2px solid var(--bs-light, #f8f9fa);
  font-size: 0.65em;
  padding: 0.3em 0.55em;
  background-color: var(--bs-danger);
  color: white;
}

a.dropdown-toggle {
  font-weight: 500;
}

a.dropdown-toggle .bi-person-circle {
  margin-right: 0.5rem !important;
}

.navbar-nav .btn-outline-primary {
  border-color: var(--color-primary, #6d4c41);
  color: var(--color-primary, #6d4c41);
  transition: all 0.2s ease;
}

.navbar-nav .btn-outline-primary:hover {
  background-color: var(--color-primary, #6d4c41);
  color: white;
}


@media (max-width: 991.98px) {
  .dropdown-menu {
    position: static !important;
    float: none !important;
    width: auto !important;
    margin-top: 0 !important;
    background-color: transparent !important;
    border: 0 !important;
    box-shadow: none !important;
    padding-left: 0.5rem;
    opacity: 1;
    visibility: visible;
    display: none;
  }

  .dropdown-menu.show {
    display: block;
  }

  .dropdown-item {
    padding-left: 1.5rem;
  }

  .dropdown-divider {
    margin: 0.5rem 1rem;
    border-top-color: var(--color-border-subtle, #ced4da);
  }

  .navbar-nav .btn-sm {
    margin-left: 0;
    margin-top: 0.5rem;
    display: inline-block;
    width: auto;
  }

  .header-actions .nav-item {
    margin-top: 0.5rem;
  }

  .header-actions .nav-item:first-child {
    margin-top: 0.75rem;
  }

  .header-actions .ms-lg-3 {
    margin-left: 0 !important;
  }

  .navbar-nav>.nav-item.ms-lg-2,
  .navbar-nav>.nav-item.ms-lg-3 {
    margin-left: 0 !important;
  }
}


.fixed-sidebar-toggle {
  position: fixed;
  top: 15px;
  left: 25px;
  z-index: 1035;
  font-weight: 500;
  border: 0px;
  --bs-btn-hover-bg: #f0eeed;
  padding: 0.3rem 0.8rem;
  border-radius: var(--border-radius-sm, 0.25rem);


  transition: background-color 0.2s ease;
}

.fixed-sidebar-toggle .bi-list {
  font-size: 1.5rem;
  vertical-align: middle;
}


.fixed-sidebar-toggle span {
  font-size: 0.95rem;
  vertical-align: middle;
}

/* màu động thông báo */
.notification-item {
  border-radius: 6px;
  padding: 10px 12px;
  margin-bottom: 6px;
  font-size: 14px;
  white-space: normal;
  overflow-wrap: break-word;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
}

/* Hover nổi bật */
.notification-item:hover {
  filter: brightness(0.92);
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.15);
}

/* Kiểu chữ */
.notification-item .title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
  color: #222;
}

.notification-item .content {
  font-size: 14px;
  color: #444;
}

.notification-item .time {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}

/* Màu nền theo trạng thái */
.status-pending {
  background-color: #fff7e6;
}

.status-payment {
  background-color: #fffbe6;
}

.status-processing {
  background-color: #e6f4ff;
}

.status-shipping {
  background-color: #f0e6ff;
}

.status-failed {
  background-color: #ffe6e6;
}

.status-success {
  background-color: #e6ffe6;
}

.status-cancelled {
  background-color: #f4f4f4;
}

.status-default {
  background-color: #f8f9fa;
}
</style>
