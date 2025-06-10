<template>
  <div class="order-history-view container mt-4 mb-5">
    <h1 class="text-center mb-4">Lịch Sử Đơn Hàng</h1>

    <!-- Loading/Error/No Orders States -->
    <div v-if="loading" class="text-center my-5">Đang tải đơn hàng...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else-if="orders.length === 0" class="alert alert-info text-center">Bạn chưa có đơn hàng nào.</div>

    <!-- Order List -->
    <div v-else>
      <div class="list-group shadow-sm">
        <div
          v-for="order in orders"
          :key="order.id"
          class="list-group-item list-group-item-action flex-column align-items-start mb-3 rounded border"
        >
          <!-- Thông tin đơn hàng -->
          <div class="d-flex w-100 justify-content-between mb-2 flex-wrap">
            <h5 class="mb-1 h6">
              Mã đơn hàng: <strong class="text-primary">#{{ order.id }}</strong>
            </h5>
            <small class="text-muted" :title="formatFullDateTime(order.orderDate)">
              {{ formatDateRelative(order.orderDate) }}
            </small>
          </div>

          <!-- Ảnh sản phẩm -->
          <div
            v-if="order.orderItems && order.orderItems.length > 0"
            class="mb-2 order-items-preview d-flex flex-wrap gap-2"
          >
            <img
              v-for="item in order.orderItems.slice(0, 5)"
              :key="item.productId"
              :src="item.productImage || defaultImage"
              :alt="item.productName || 'Sản phẩm'"
              class="rounded border"
              width="50"
              height="50"
              style="object-fit: cover"
              :title="`${item.productName || 'Sản phẩm'} (x${item.quantity})`"
              @error="onImageError"
            />
            <span v-if="order.orderItems.length > 5" class="align-self-center text-muted small ms-1">
              + {{ order.orderItems.length - 5 }} SP khác
            </span>
          </div>

          <!-- Tổng tiền + trạng thái -->
          <div class="d-flex justify-content-between align-items-center flex-wrap">
            <p class="mb-1">
              Tổng tiền:
              <strong class="text-danger">{{ formatCurrency(order.totalAmount) }}</strong>
            </p>
            <span class="badge rounded-pill" :class="getStatusClass(order.status)">
              {{ formatStatus(order.status) }}
            </span>
          </div>
          <small class="text-muted">Thanh toán: {{ order.paymentMethod || 'Chưa rõ' }}</small>

          <!-- Nút xem chi tiết -->
          <div class="mt-2 text-end">
            <router-link
              :to="{ name: 'userOrderDetail', params: { orderId: order.id } }"
              class="btn btn-sm btn-outline-primary"
            >
              <i class="bi bi-eye"></i> Xem chi tiết
            </router-link>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <BasePagination
        v-if="totalPages > 1"
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="handlePageChange"
        class="mt-4 d-flex justify-content-center"
      />
    </div>
  </div>
</template>


<script setup>

import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute, RouterLink } from "vue-router";
import { getUserOrders } from "@/http/modules/public/orderService.js";
import BasePagination from "@/components/common/BasePagination.vue";
import defaultImage from "@/assets/images/placeholder.png";



const formatCurrency = (value) =>
  value != null
    ? new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value)
    : "0 đ";
const formatFullDateTime = (dateString) =>
  dateString ? new Date(dateString).toLocaleString("vi-VN") : "";
const formatDateRelative = (dateString) => {
  /* ... */
  return dateString ? new Date(dateString).toLocaleDateString("vi-VN") : "";
};
const formatStatus = (status) => {
  /* ... */
  return status || "N/A";
};
const getStatusClass = (status) => {
  /* ... */
  return "text-bg-light";
};

const router = useRouter();
const route = useRoute();
const orders = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const itemsPerPage = ref(10);

const fetchOrders = async (page = 0) => {
  /* ... logic fetch giữ nguyên ... */
  loading.value = true;
  error.value = null;
  try {
    const params = { page, size: itemsPerPage.value };
    const response = await getUserOrders(params);
    orders.value = response.data.content || [];
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (err) {
    error.value = "Không thể tải danh sách đơn hàng.";
    orders.value = [];
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};


const handlePageChange = (newPage) => {
  router.push({ query: { ...route.query, page: newPage } });
};
watch(
  () => route.query.page,
  (newPageQuery) => {
    fetchOrders(parseInt(newPageQuery, 10) || 0);
  },
  { immediate: true }
);
const onImageError = (event) => {
  event.target.src = defaultImage;
};
</script>




















<style scoped>
/* Style không đổi */
.order-history-view {
  min-height: 70vh;
}

.list-group-item {
  transition: background-color 0.15s ease-in-out;
}

.order-items-preview img {
  opacity: 0.9;
}

.badge {
  font-size: 0.8em;
}
</style>
