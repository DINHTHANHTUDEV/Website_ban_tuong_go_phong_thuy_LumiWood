<template>
  <div class="admin-promotion-list-view">

    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Quản lý Khuyến mãi</h1>
      <router-link :to="{ name: 'adminPromotionNew' }" class="btn btn-primary">
        <i class="bi bi-plus-lg"></i> Thêm Khuyến mãi
      </router-link>
    </div>


    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <form @submit.prevent="applyTableFilters">
          <div class="row g-2 align-items-end">

            <div class="col-md-4">
              <label for="filterStatus" class="form-label">Trạng thái</label>
              <select class="form-select form-select-sm" id="filterStatus" v-model="filters.statusFilter">
                <option :value="null">-- Tất cả --</option>
                <option value="ACTIVE">Đang diễn ra</option>
                <option value="UPCOMING">Sắp diễn ra</option>
                <option value="EXPIRED">Đã hết hạn</option>
                <option value="INACTIVE">Ngừng hoạt động</option>
              </select>
            </div>



            <div class="col-md-auto">
              <button type="submit" class="btn btn-primary btn-sm w-100">
                <i class="bi bi-funnel"></i> Lọc
              </button>
            </div>
            <div class="col-md-auto">
              <button type="button" class="btn btn-outline-secondary btn-sm w-100" @click="resetFilters">
                <i class="bi bi-arrow-repeat"></i> Xóa lọc
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>


    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>


    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>


    <div v-else-if="promotions.length > 0" class="table-responsive card shadow-sm">
      <table class="table table-hover table-striped mb-0 align-middle">
        <thead class="table-light">
        <tr>
          <th scope="col">Mã Code</th>
          <th scope="col">Tên KM</th>
          <th scope="col">Loại</th>
          <th scope="col" class="text-end">Giá trị</th>
          <th scope="col">Ngày hiệu lực</th>
          <th scope="col">Ngày hết hạn</th>
          <th scope="col" class="text-center">Lượt dùng</th>
          <th scope="col">Bậc KH</th>
          <th scope="col" class="text-center">Trạng thái</th>
          <th scope="col" class="text-center" style="min-width: 100px;">Hành Động</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="promo in promotions" :key="promo.id">

          <td class="fw-medium">{{ promo.code }}</td>

          <td>{{ promo.name || 'N/A' }}</td>

          <td>{{ formatDiscountType(promo.discountType) }}</td>

          <td class="text-end">{{ formatDiscountValue(promo.discountValue, promo.discountType) }}</td>

          <td>{{ formatDate(promo.startDate) }}</td>

          <td>{{ formatDate(promo.endDate) }}</td>

          <td class="text-center">{{ promo.currentUsage ?? 0 }} / {{ promo.maxUsage || '∞' }}</td>

          <td>
            <span v-if="!promo.targetTiers" class="badge bg-light text-dark border">Tất cả</span>
            <span v-else>
                <span v-for="tier in promo.targetTiers.split(',').map(t => t.trim()).filter(t => t)" :key="tier"
                      class="badge me-1" :class="getTierClass(tier)">
                  {{ formatTier(tier) }}
                </span>
              </span>
          </td>

          <td class="text-center">
              <span class="badge rounded-pill" :class="getPromoStatusClass(promo)">
                {{ getPromoStatusText(promo) }}
              </span>
          </td>

          <td class="text-center">

            <router-link
              :to="{ name: 'adminPromotionEdit', params: { id: promo.id } }"
              class="btn btn-sm btn-outline-secondary me-1" title="Sửa">
              <i class="bi bi-pencil-square"></i>
            </router-link>

            <button
              class="btn btn-sm"
              :class="promo.isActive ? 'btn-outline-danger' : 'btn-outline-success'"
              :title="promo.isActive ? 'Ngừng hoạt động' : 'Kích hoạt lại'"
              @click="confirmToggleStatus(promo)"
              :disabled="togglingStatusId === promo.id">

              <span v-if="togglingStatusId === promo.id" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>

              <i v-else :class="promo.isActive ? 'bi bi-pause-circle' : 'bi bi-play-circle-fill'"></i>
            </button>

          </td>
        </tr>
        </tbody>
      </table>


      <div class="card-footer bg-light border-top-0 py-2" v-if="totalPages > 1">
        <BasePagination
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="handlePageChange"
          class="mt-0 d-flex justify-content-center mb-0"
        />
      </div>
    </div>


    <div v-else class="alert alert-info text-center">
      <i class="bi bi-info-circle me-2"></i> Không tìm thấy chương trình khuyến mãi nào phù hợp với bộ lọc hiện tại.
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { getAdminPromotions, toggleAdminPromotionStatus } from '@/http/modules/admin/adminPromotionService.js';
import BasePagination from '@/components/common/BasePagination.vue';

import { formatCurrency, formatDate, formatTier, getTierClass } from '@/utils/formatters';

const router = useRouter();
const route = useRoute();


const promotions = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const itemsPerPage = ref(10);


const togglingStatusId = ref(null);


const filters = reactive({
  statusFilter: route.query.statusFilter || null,
});


const fetchPromotions = async (page = 0) => {
  loading.value = true;
  error.value = null;
  promotions.value = [];
  try {

    const params = {
      page,
      size: itemsPerPage.value,
      sort: 'createdAt,desc',
      ...(filters.statusFilter && { statusFilter: filters.statusFilter })
    };
    console.log("Fetching promotions with params:", params);
    const response = await getAdminPromotions(params);
    promotions.value = response.data.content || [];
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (err) {
    console.error("Error fetching admin promotions:", err);
    error.value = `Không thể tải danh sách khuyến mãi. Lỗi: ${err.response?.data?.message || err.message || 'Unknown error'}`;
  } finally {
    loading.value = false;
  }
};


const applyTableFilters = () => {


  const query = { ...route.query, page: 0 };
  if (filters.statusFilter) {
    query.statusFilter = filters.statusFilter;
  } else {
    delete query.statusFilter;
  }

  router.push({ query });

};

const resetFilters = () => {

  filters.statusFilter = null;

  const query = { ...route.query };
  delete query.statusFilter;

  query.page = 0;
  router.push({ query });

};



const confirmToggleStatus = async (promo) => {

  if (togglingStatusId.value) return;


  const newStatus = !promo.isActive;
  const actionText = newStatus ? 'kích hoạt lại' : 'ngừng hoạt động';


  if (!confirm(`Bạn có chắc muốn ${actionText} khuyến mãi "${promo.code}"?`)) {
    return;
  }


  togglingStatusId.value = promo.id;

  try {

    const response = await toggleAdminPromotionStatus(promo.id, newStatus);


    const updatedPromo = response.data;
    const promoIndex = promotions.value.findIndex(p => p.id === updatedPromo.id);
    if (promoIndex !== -1) {

      promotions.value[promoIndex] = updatedPromo;
      console.log(`Promotion ${promo.id} status toggled successfully on UI.`);
    } else {

      console.warn(`Could not find promotion ${promo.id} in local list after toggle. Refetching.`);
      await fetchPromotions(currentPage.value);
    }



  } catch (err) {

    console.error(`Error toggling status for promotion ${promo.id}:`, err);

    const errorMsg = err.response?.data?.message || err.message || `Lỗi khi ${actionText} khuyến mãi.`;
    alert(errorMsg);

  } finally {
    togglingStatusId.value = null;
  }
};



const formatDiscountType = (type) => {
  if (type === 'PERCENTAGE') return 'Phần trăm (%)';
  if (type === 'FIXED_AMOUNT') return 'Số tiền cố định (VND)';
  return type;
};

const formatDiscountValue = (value, type) => {
  if (value === null || value === undefined) return 'N/A';
  if (type === 'PERCENTAGE') return `${value}%`;

  return formatCurrency(value);
};


const getPromoStatusText = (promo) => {
  const now = new Date();
  const start = new Date(promo.startDate);
  const end = new Date(promo.endDate);

  if (!promo.isActive) return 'Ngừng';

  const nowDateOnly = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const startDateOnly = new Date(start.getFullYear(), start.getMonth(), start.getDate());
  const endDateOnly = new Date(end.getFullYear(), end.getMonth(), end.getDate());

  if (nowDateOnly < startDateOnly) return 'Sắp diễn ra';

  if (nowDateOnly > endDateOnly) return 'Đã hết hạn';

  return 'Đang diễn ra';
};


const getPromoStatusClass = (promo) => {
  const statusText = getPromoStatusText(promo);
  switch (statusText) {
    case 'Ngừng': return 'text-bg-secondary';
    case 'Sắp diễn ra': return 'text-bg-info';
    case 'Đã hết hạn': return 'text-bg-warning text-dark';
    case 'Đang diễn ra': return 'text-bg-success';
    default: return 'text-bg-light text-dark border';
  }
};






watch(
  () => route.query,
  (newQuery, oldQuery) => {
    console.log('Route query changed:', newQuery);

    filters.statusFilter = newQuery.statusFilter || null;




    const page = parseInt(newQuery.page || '0', 10);
    fetchPromotions(page);
  },
  {
    immediate: true,
    deep: true
  }
);

const handlePageChange = (newPage) => {
  router.push({ query: { ...route.query, page: newPage } });
};


</script>

<style scoped>
.table th, .table td {
  vertical-align: middle;
}
.badge {
  font-size: 0.8em;
  padding: 0.3em 0.5em;
}
.table-hover tbody tr:hover {
  background-color: rgba(0, 0, 0, 0.03);
}


</style>

