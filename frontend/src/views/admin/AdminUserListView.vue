<template>
  <div class="admin-user-list-view">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Quản lý Khách hàng</h1>

    </div>


    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <form @submit.prevent="applyTableFilters">
          <div class="row g-3 align-items-end">
            <div class="col-md-4">
              <label for="filterKeyword" class="form-label">Tìm kiếm</label>
              <input type="text" class="form-control form-control-sm" id="filterKeyword" v-model="filters.search"
                placeholder="Tên đăng nhập, họ tên...">
            </div>
            <div class="col-md-3">
              <label for="filterTier" class="form-label">Bậc KH</label>
              <select class="form-select form-select-sm" id="filterTier" v-model="filters.tier">
                <option value="all">-- Tất cả --</option>
                <option value="BRONZE">Đồng</option>
                <option value="SILVER">Bạc</option>
                <option value="GOLD">Vàng</option>
                <option value="DIAMOND">Kim cương</option>
              </select>
            </div>
            <div class="col-md-3">
              <label for="filterStatus" class="form-label">Trạng thái TK</label>
              <select v-model="filters.status" class="form-select form-select-sm" id="filterStatus">
                <option value="all">-- Tất cả --</option>
                <option value="active">Đang hoạt động</option>
                <option value="locked">Đã khóa</option>
              </select>
            </div>
            <div class="col-md-2">
              <button type="submit" class="btn btn-primary btn-sm w-100">Lọc</button>
            </div>
          </div>
        </form>
      </div>
    </div>



    <div v-if="loading" class="text-center my-5"> ... </div>


    <div v-else-if="error" class="alert alert-danger"> ... </div>


    <div v-else-if="users.length > 0" class="table-responsive card shadow-sm">
      <table class="table table-hover table-striped mb-0 align-middle">
        <thead class="table-light">
          <tr>

            <th scope="col" @click="setSort('username')" class="sortable">
              Tên đăng nhập <i :class="getSortIcon('username')"></i>
            </th>
            <th scope="col" @click="setSort('fullName')" class="sortable">
              Họ và Tên <i :class="getSortIcon('fullName')"></i>
            </th>
            <th scope="col" @click="setSort('tier')" class="sortable">
              Bậc KH <i :class="getSortIcon('tier')"></i>
            </th>
            <th scope="col" @click="setSort('totalSpent')" class="sortable text-end">
              Tổng chi tiêu <i :class="getSortIcon('totalSpent')"></i>
            </th>
            <th scope="col" @click="setSort('createdAt')" class="sortable">
              Ngày tham gia <i :class="getSortIcon('createdAt')"></i>
            </th>
            <th scope="col" class="text-center">Trạng thái</th>
            <th scope="col" class="text-center">Hành Động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td class="fw-medium">{{ user.username }}</td>
            <td>{{ user.fullName || 'N/A' }}</td>
            <td>
              <span class="badge" :class="getTierClass(user.tier)">{{ formatTier(user.tier) }}</span>
            </td>
            <td class="text-end">{{ formatCurrency(user.totalSpent) }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td class="text-center">
              <span class="badge rounded-pill" :class="user.isActive ? 'text-bg-success' : 'text-bg-secondary'">
                {{ user.isActive ? 'Hoạt động' : 'Đã khóa' }}
              </span>
            </td>
            <td class="text-center">
              <router-link :to="{ name: 'adminUserDetail', params: { userId: user.id } }"
                class="btn btn-sm btn-outline-primary me-1" title="Xem chi tiết">
                <i class="bi bi-eye"></i>
              </router-link>

              <button class="btn btn-sm" :class="user.isActive ? 'btn-outline-warning' : 'btn-outline-success'"
                :title="user.isActive ? 'Khóa tài khoản' : 'Mở khóa tài khoản'" @click="toggleUserStatus(user)"
                :disabled="updatingStatusId === user.id">
                <span v-if="updatingStatusId === user.id" class="spinner-border spinner-border-sm" role="status"
                  aria-hidden="true"></span>
                <i v-else :class="user.isActive ? 'bi bi-lock' : 'bi bi-unlock'"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>


      <div class="card-footer bg-light border-top-0" v-if="totalPages >= 1">
        <BasePagination :current-page="currentPage" :total-pages="totalPages" @page-change="handlePageChange"
          class="mt-3 d-flex justify-content-center mb-0" />
      </div>


      <div v-else class="alert alert-info text-center"> ... </div>




    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getAdminCustomers, updateAdminCustomerStatus } from '@/http/modules/admin/adminUserService.js';
import BasePagination from '@/components/common/BasePagination.vue';
import { formatCurrency, formatDate } from '@/utils/formatters';

const router = useRouter();
const route = useRoute();

const users = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const itemsPerPage = ref(10);
const currentSort = ref({ field: 'createdAt', direction: 'desc' });
const updatingStatusId = ref(null);

const filters = reactive({
  search: route.query.search || '',
  tier: route.query.tier || 'all',
  status: route.query.status || 'all', // 'all', 'active', 'locked'
});


// Hàm fetch dữ liệu theo filter
const fetchUsers = async (page = 0) => {
  loading.value = true;
  error.value = null;

  try {
    const params = {
      page,
      tier: filters.tier === 'all' ? undefined : filters.tier,
      status: filters.status === 'all' ? undefined : filters.status,
      search: filters.search || '',
    };

    const response = await getAdminCustomers(params);
    console.log('totalPages:', response.data.totalPages);
    console.log('currentPage:', response.data.number);
    users.value = (response.data.content || []).map(user => ({
      ...user,
      id: user.userId,
    }));

    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;

    console.log('Response page:', response.data.number, 'Response total pages:', response.data.totalPages); // debug
  } catch (err) {
    error.value = "Không thể tải danh sách khách hàng.";
  } finally {
    loading.value = false;
  }
};




// Đồng bộ query params URL và filters + fetchUsers
watch(
  () => route.query,
  (newQuery) => {
    currentPage.value = Number(newQuery.page) || 0;
    console.log('Current page:', currentPage.value, 'Total pages:', totalPages.value); // debug
    const [field, direction] = (newQuery.sort || 'createdAt,desc').split(',');
    currentSort.value = { field, direction };

    filters.search = newQuery.search ?? '';
    filters.tier = newQuery.tier ?? 'all';
    filters.status = newQuery.status ?? 'all';

    fetchUsers(currentPage.value);
  },
  { immediate: true }
);




// Khi submit form filter
const applyTableFilters = () => {
  const query = {
    page: 0,
    search: filters.search || undefined,
    tier: filters.tier || undefined,
    status: filters.status || undefined,
  };

  Object.keys(query).forEach(key => {
    if (query[key] === undefined) delete query[key];
  });

  router.push({ query });
};



// Hàm đổi trạng thái user
const toggleUserStatus = async (user) => {
  if (updatingStatusId.value) return;
  const newStatus = !user.isActive;
  const actionText = newStatus ? 'mở khóa' : 'khóa';
  if (!confirm(`Bạn có chắc muốn ${actionText} tài khoản "${user.username}"?`)) return;

  updatingStatusId.value = user.id;
  try {
    await updateAdminCustomerStatus(user.id, newStatus);

    const userIndex = users.value.findIndex(u => u.id === user.id);
    if (userIndex !== -1) {
      users.value[userIndex].isActive = newStatus;
    }
  } catch (err) {
    console.error(`Error updating status for user ${user.id}:`, err);
    alert(`Lỗi khi ${actionText} tài khoản: ${err.response?.data?.message || err.message}`);
  } finally {
    updatingStatusId.value = null;
  }
};

const formatTier = (tier) => {
  const map = { BRONZE: 'Đồng', SILVER: 'Bạc', GOLD: 'Vàng', DIAMOND: 'Kim cương' };
  return map[tier] || tier;
};
const getTierClass = (tier) => {
  const map = { BRONZE: 'bg-opacity-75 bg-secondary text-dark', SILVER: 'bg-opacity-75 bg-info text-dark', GOLD: 'bg-warning text-dark', DIAMOND: 'bg-primary' };
  return map[tier] || 'bg-light text-dark';
};

const setSort = (field) => {
  let direction = 'asc';
  if (currentSort.value.field === field && currentSort.value.direction === 'asc') {
    direction = 'desc';
  }
  router.push({ query: { ...route.query, sort: `${field},${direction}`, page: 0 } });
};

const getSortIcon = (field) => {
  if (currentSort.value.field !== field) return 'bi bi-filter';
  return currentSort.value.direction === 'asc' ? 'bi bi-sort-up' : 'bi bi-sort-down';
};

// Xử lý khi đổi trang
const handlePageChange = (newPage) => {
  router.push({ query: { ...route.query, page: newPage } });
};
</script>

<style scoped>
.sortable {
  cursor: pointer;
}

.sortable:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.sortable i {
  margin-left: 5px;
  color: #999;
}

.sortable:hover i {
  color: #333;
}

th i.bi-sort-up,
th i.bi-sort-down {
  color: var(--bs-primary) !important;
}

.badge.bg-opacity-75 {
  --bs-bg-opacity: 0.75;
}
</style>
