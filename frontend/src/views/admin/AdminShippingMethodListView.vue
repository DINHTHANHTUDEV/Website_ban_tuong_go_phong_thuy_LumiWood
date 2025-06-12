<template>
  <div class="admin-shipping-method-list-view">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Quản lý Vận chuyển</h1>
      <router-link :to="{ name: 'adminShippingMethodNew' }" class="btn btn-primary">
        <i class="bi bi-plus-lg"></i> Thêm Phương thức
      </router-link>
    </div>


    <div v-if="loading" class="text-center my-5"> ... </div>


    <div v-else-if="error" class="alert alert-danger"> ... </div>


    <div v-else-if="methods.length > 0" class="table-responsive card shadow-sm">
      <table class="table table-hover table-striped mb-0 align-middle">
        <thead class="table-light">
          <tr>
            <th scope="col">Tên Phương thức</th>
            <th scope="col">Mô tả</th>
            <th scope="col" class="text-end">Phí cơ bản</th>
            <th scope="col" class="text-center">Thời gian dự kiến</th>
            <th scope="col" class="text-center">Trạng thái</th>
            <th scope="col" class="text-center">Hành Động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="method in methods" :key="method.id">
            <td class="fw-medium">{{ method.name }}</td>
            <td>{{ method.description || 'N/A' }}</td>
            <td class="text-end">{{ formatCurrency(method.baseCost) }}</td>
            <td class="text-center">{{ formatEstimatedDays(method.estimatedDaysMin, method.estimatedDaysMax) }}</td>
            <td class="text-center">
              <span class="badge rounded-pill" :class="method.isActive ? 'text-bg-success' : 'text-bg-secondary'">
                {{ method.isActive ? 'Hoạt động' : 'Ngừng' }}
              </span>
            </td>
            <td class="text-center">
              <router-link :to="{ name: 'adminShippingMethodEdit', params: { id: method.id } }"
                class="btn btn-sm btn-outline-secondary me-1" title="Sửa">
                <i class="bi bi-pencil-square"></i>
              </router-link>
              <button class="btn btn-sm" :class="method.isActive ? 'btn-outline-danger' : 'btn-outline-success'"
                :title="method.isActive ? 'Ngừng hoạt động' : 'Kích hoạt lại'" @click="confirmToggleStatus(method)"
                :disabled="togglingStatusId === method.id">
                <span v-if="togglingStatusId === method.id" class="spinner-border spinner-border-sm"></span>
                <i v-else :class="method.isActive ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="card-footer bg-light border-top-0" v-if="totalPages > 1">
        <BasePagination :current-page="currentPage" :total-pages="totalPages" @page-change="handlePageChange"
          class="mt-3 d-flex justify-content-center mb-0" />
      </div>
    </div>


    <div v-else class="alert alert-info text-center"> ... </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  getAdminShippingMethods,
  updateAdminShippingMethod
} from '@/http/modules/admin/adminShippingService.js';
import BasePagination from '@/components/common/BasePagination.vue';
import { formatCurrency } from '@/utils/formatters';

const router = useRouter();
const route = useRoute();

const methods = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const itemsPerPage = ref(10);
const togglingStatusId = ref(null);

// Fetch danh sách phương thức vận chuyển
const fetchMethods = async (page = 0) => {
  loading.value = true;
  error.value = null;
  try {
    const params = { page, size: itemsPerPage.value, sort: 'name,asc' };
    const response = await getAdminShippingMethods(params);
    methods.value = response.data.content || [];
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (err) {
    console.error("Error fetching admin shipping methods:", err);
    error.value = "Không thể tải danh sách phương thức vận chuyển.";
  } finally {
    loading.value = false;
  }
};

// Toggle trạng thái hoạt động
const confirmToggleStatus = async (method) => {
  if (togglingStatusId.value) return;
  const actionText = method.isActive ? 'ngừng hoạt động' : 'kích hoạt lại';
  if (!confirm(`Bạn có chắc muốn ${actionText} phương thức "${method.name}"?`)) return;

  togglingStatusId.value = method.id;
  try {
    const updatedData = { ...method, isActive: !method.isActive };
    await updateAdminShippingMethod(method.id, updatedData);
    await fetchMethods(currentPage.value);
  } catch (err) {
    console.error(`Error toggling status for method ${method.id}:`, err);
    alert(`Lỗi khi ${actionText} phương thức: ${err.response?.data?.message || err.message}`);
  } finally {
    togglingStatusId.value = null;
  }
};

// Format hiển thị thời gian ước tính
const formatEstimatedDays = (min, max) => {
  if (min === null && max === null) return 'N/A';
  if (min !== null && max !== null) {
    return min === max ? `${min} ngày` : `${min}-${max} ngày`;
  }
  if (min !== null) return `Từ ${min} ngày`;
  if (max !== null) return `Tối đa ${max} ngày`;
  return 'N/A';
};

// Theo dõi thay đổi query page để gọi API
watch(() => route.query.page, (newPageQuery) => {
  fetchMethods(parseInt(newPageQuery || '0', 10));
}, { immediate: true });

// Thay đổi trang phân trang
const handlePageChange = (newPage) => {
  router.push({ query: { ...route.query, page: newPage } });
};
</script>

<style scoped></style>
