<template>
  <div class="admin-order-list-view container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Quản lý Đơn hàng</h1>
    </div>

    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <form @submit.prevent="applyTableFilters">
          <div class="row g-3 align-items-end">

            <div class="col-md-4 col-lg-3">
              <label for="filterKeyword" class="form-label">Tìm kiếm</label>
              <input type="text" class="form-control form-control-sm" id="filterKeyword" v-model="filters.keyword" placeholder="Mã ĐH, tên, SĐT, Gmail,....">
            </div>

            <div class="col-md-3 col-lg-2">
              <label for="filterStatus" class="form-label">Trạng thái</label>
              <select class="form-select form-select-sm" id="filterStatus" v-model="filters.status">
                <option :value="null">-- Tất cả --</option>
                <option v-for="statusOpt in statusOptions" :key="statusOpt.value" :value="statusOpt.value">
                  {{ statusOpt.text }}
                </option>
              </select>
            </div>

            <div class="col-md-5 col-lg-4">
              <label class="form-label">Ngày đặt hàng</label>
              <div class="input-group input-group-sm">
                <input type="date" class="form-control" title="Từ ngày" v-model="filters.startDate">
                <span class="input-group-text">đến</span>
                <input type="date" class="form-control" title="Đến ngày" v-model="filters.endDate">
              </div>
            </div>

            <div class="col-md-12 col-lg-3 d-flex gap-2 mt-3 mt-lg-0">
              <button type="submit" class="btn btn-primary btn-sm flex-grow-1">
                <i class="bi bi-search"></i> Tìm kiếm / Lọc
              </button>
              <button type="button" class="btn btn-outline-secondary btn-sm flex-grow-1" @click="resetFilters" title="Xóa bộ lọc">
                <i class="bi bi-x-lg"></i> Xóa lọc
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div v-if="loading" class="text-center my-5 py-5">
      <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
        <span class="visually-hidden">Đang tải đơn hàng...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      Lỗi tải đơn hàng: {{ error }}
    </div>

    <div v-else-if="orders.length > 0" class="table-responsive card shadow-sm">
      <table class="table table-hover table-striped mb-0 align-middle">
        <thead class="table-light">
        <tr>
          <th scope="col">Mã ĐH</th>
          <th scope="col">Ngày Đặt</th>
          <th scope="col">Khách Hàng</th>
          <th scope="col" class="text-end">Tổng Tiền</th>
          <th scope="col" class="text-center">Trạng Thái</th>
          <th scope="col">Thanh Toán</th>
          <th scope="col" class="text-center">Hành Động</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="order in orders" :key="order.id">
          <td class="fw-medium">#{{ order.id }}</td>
          <td>{{ formatDateTime(order.orderDate) }}</td>
          <td>{{ order.customerName }}</td>
          <td class="text-end">{{ formatCurrency(order.totalAmount) }}</td>
          <td class="text-center">
              <span class="badge rounded-pill" :class="getStatusClass(order.status)">
                {{ formatStatus(order.status) }}
              </span>
          </td>
          <td>{{ order.paymentMethod || 'N/A' }}</td>
          <td class="text-center">
            <router-link
              :to="{ name: 'adminOrderDetail', params: { orderId: order.id } }"
              class="btn btn-sm btn-outline-primary"
              title="Xem chi tiết"
            >
              <i class="bi bi-eye"></i>
            </router-link>
          </td>
        </tr>
        </tbody>
      </table>

      <div class="card-footer bg-light border-top-0" v-if="totalPages > 1">
        <BasePagination
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="handlePageChange"
          class="mb-0 mt-3 d-flex justify-content-center"
        />
      </div>
    </div>

    <div v-else class="alert alert-info text-center">
      Không có đơn hàng nào khớp với tiêu chí.
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getAllOrders } from '@/http/modules/admin/adminOrderService.js';
import BasePagination from '@/components/common/BasePagination.vue';

import { allStatuses, formatStatus, formatCurrency, formatDateTime, getStatusClass } from '@/utils/formatters';

const router = useRouter();
const route = useRoute();

const orders = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(1); // UI 1-based
const totalPages = ref(0);
const itemsPerPage = ref(15);

const filters = reactive({
  keyword: route.query.keyword || '',
  status: route.query.status || '',
  startDate: route.query.startDate || '',
  endDate: route.query.endDate || '',
});

const statusOptions = computed(() => {
  return allStatuses.map(s => ({ value: s, text: formatStatus(s) }));
});

const applyTableFilters = () => {
  const query = { ...route.query, page: 1 };
  if (filters.keyword) query.keyword = filters.keyword; else delete query.keyword;
  if (filters.status) query.status = filters.status; else delete query.status;
  if (filters.startDate) query.startDate = filters.startDate; else delete query.startDate;
  if (filters.endDate) query.endDate = filters.endDate; else delete query.endDate;

  if (filters.startDate && filters.endDate && filters.startDate > filters.endDate) {
    alert("Ngày bắt đầu không được sau ngày kết thúc.");
    return;
  }

  router.push({ query });
};

const resetFilters = () => {
  filters.keyword = '';
  filters.status = '';
  filters.startDate = '';
  filters.endDate = '';
  const query = { ...route.query };
  delete query.keyword;
  delete query.status;
  delete query.startDate;
  delete query.endDate;
  query.page = 1;
  router.push({ query });
};

const fetchOrders = async (page = 1) => {
  loading.value = true;
  error.value = null;

  try {
    const params = {
      page: page - 1, // 0-based backend
      size: itemsPerPage.value,
      sort: route.query.sort || 'orderDate,desc',
    };

    if (filters.keyword?.trim()) params.keyword = filters.keyword.trim();
    if (filters.status) params.status = filters.status;
    if (filters.startDate) params.startDate = filters.startDate;
    if (filters.endDate) params.endDate = filters.endDate;

    const response = await getAllOrders(params);
    orders.value = response.content || [];
    currentPage.value = response.number + 1;
    totalPages.value = response.totalPages;
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
  () => route.query,
  (newQuery) => {
    filters.keyword = newQuery.keyword || '';
    filters.status = newQuery.status || '';
    filters.startDate = newQuery.startDate || '';
    filters.endDate = newQuery.endDate || '';

    const pageNum = parseInt(newQuery.page || '1', 10);
    fetchOrders(pageNum);
  },
  { immediate: true, deep: true }
);
</script>

<style scoped>
.admin-order-list-view { min-height: 80vh; }
.table th, .table td { vertical-align: middle; }
.badge { font-size: 0.8em; padding: 0.4em 0.7em; }
.input-group-sm .form-control[type="date"] {
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
  font-size: 0.875rem;
}
</style>
