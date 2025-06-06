<template>
  <div class="admin-user-detail-view">

    <div class="mb-3">
      <router-link :to="{ name: 'adminUserList' }" class="btn btn-sm btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách KH
      </router-link>
    </div>


    <div v-if="loading" class="text-center my-5"> ... </div>


    <div v-else-if="error" class="alert alert-danger"> ... </div>


    <div v-else-if="user" class="user-detail-content">
      <h1 class="mb-4">Chi tiết Khách hàng: <strong class="text-primary">{{ user.username }}</strong></h1>

      <div class="row g-4">

        <div class="col-lg-5">
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light"><strong>Thông tin cơ bản</strong></div>
            <div class="card-body">
              <dl class="row mb-0">
                <dt class="col-sm-5">Tên đăng nhập:</dt>
                <dd class="col-sm-7">{{ user.username }}</dd>
                <dt class="col-sm-5">Họ và Tên:</dt>
                <dd class="col-sm-7">{{ user.fullName || 'N/A' }}</dd>
                <dt class="col-sm-5">Ngày tham gia:</dt>
                <dd class="col-sm-7">{{ formatDate(user.createdAt) }}</dd>
                <dt class="col-sm-5">Tổng chi tiêu:</dt>
                <dd class="col-sm-7 fw-bold text-danger">{{ formatCurrency(user.totalSpent) }}</dd>
                <dt class="col-sm-5">Trạng thái TK:</dt>
                <dd class="col-sm-7">
                  <span class="badge rounded-pill" :class="user.isActive ? 'text-bg-success' : 'text-bg-secondary'">
                    {{ user.isActive ? 'Hoạt động' : 'Đã khóa' }}
                  </span>
                </dd>
              </dl>
            </div>
          </div>


          <div class="card shadow-sm">
            <div class="card-header bg-warning"><strong>Quản lý Khách hàng</strong></div>
            <div class="card-body">

              <!-- cập nhật bậc khách hàng -->
              <div class="mb-3">
                <label for="customerTier" class="form-label">Bậc khách hàng:</label>
                <div class="input-group">
                  <select class="form-select" id="customerTier" v-model="selectedTier" :disabled="updatingTier">
                    <option value="BRONZE">Đồng</option>
                    <option value="SILVER">Bạc</option>
                    <option value="GOLD">Vàng</option>
                    <option value="DIAMOND">Kim cương</option>
                  </select>
                  <button class="btn btn-primary" @click="handleUpdateTier"
                    :disabled="updatingTier || selectedTier === user.tier">
                    <span v-if="updatingTier" class="spinner-border spinner-border-sm me-1"></span> Lưu Bậc
                  </button>
                </div>
                <div v-if="tierUpdateError" class="text-danger small mt-1">{{ tierUpdateError }}</div>
                <div v-if="tierUpdateSuccess" class="text-success small mt-1">Cập nhật bậc thành công!</div>
              </div>

              <!-- Nút cập nhật trạng thái tài khoản -->
              <div class="d-flex align-items-center gap-2">
                <label class="form-label mb-0 me-2">Trạng thái tài khoản:</label>
                <button class="btn btn-sm d-flex align-items-center justify-content-center gap-1"
                  :class="user.isActive ? 'btn-outline-warning' : 'btn-outline-success'" @click="handleUpdateStatus"
                  :disabled="updatingStatus">
                  <span v-if="updatingStatus" class="spinner-border spinner-border-sm me-1" role="status"
                    aria-hidden="true"></span>
                  <i v-else :class="user.isActive ? 'bi bi-lock-fill' : 'bi bi-unlock-fill'" class="me-1"></i>
                  <span>{{ user.isActive ? 'Khóa Tài Khoản' : 'Mở Khóa Tài Khoản' }}</span>
                </button>
              </div>
              <div v-if="statusUpdateError" class="text-danger small mt-1 ms-4">{{ statusUpdateError }}</div>
              <div v-if="statusUpdateSuccess" class="text-success small mt-1 ms-4">Cập nhật trạng thái thành công!</div>
            </div>
          </div>
        </div>


        <div class="col-lg-7">
          <!-- hiển thị Địa chỉ -->
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light"><strong>Địa chỉ đã lưu ({{ addresses.length }})</strong></div>
            <div class="card-body" v-if="addresses.length > 0">
              <ul class="list-group list-group-flush">
                <li v-for="addr in addresses" :key="addr.id" class="list-group-item px-0">
                  <strong>{{ addr.recipientName }}</strong> - {{ addr.recipientPhone }}
                  <span v-if="addr.isDefaultShipping" class="badge bg-secondary ms-1">Giao hàng mặc định</span>
                  <br>
                  <small class="text-muted">
                    {{ addr.streetAddress }}, {{ addr.ward ? addr.ward + ', ' : '' }}{{ addr.district }}, {{ addr.city
                    }}
                  </small>
                </li>
              </ul>
            </div>
            <div class="card-body text-muted text-center" v-else>
              Khách hàng chưa lưu địa chỉ nào.
            </div>
          </div>
          <!-- hiển thị 5 đơn hàng gần nhất -->


          <div class="card shadow-sm">
            <div class="card-header bg-light"><strong>Đơn hàng gần đây (Tối đa 5)</strong></div>
            <div class="card-body p-0" v-if="recentOrders.length > 0">
              <ul class="list-group list-group-flush">
                <li v-for="order in recentOrders" :key="order.orderId" class="list-group-item px-3 py-2">
                  <div class="d-flex justify-content-between align-items-center">
                    <div>
                      <router-link :to="{ name: 'adminOrderDetail', params: { orderId: order.orderId } }"
                        class="fw-medium">
                        {{ order.code ? order.code : ('#' + order.orderId) }}
                      </router-link>
                      <small class="d-block text-muted">{{ formatDate(order.orderDate) }}</small>
                    </div>
                    <div>
                      <span class="badge me-2" :class="getStatusClass(order.status)">{{ formatStatus(order.status)
                      }}</span>
                      <span class="fw-bold">{{ formatCurrency(order.totalAmount) }}</span>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <div class="card-body text-muted text-center" v-else>
              Khách hàng chưa có đơn hàng nào.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter, RouterLink } from 'vue-router';
import { getAdminCustomerDetail, updateAdminCustomerTier, updateAdminCustomerStatus, getUserAddresses, getRecentOrders } from '@/http/modules/admin/adminUserService.js';
import { formatCurrency, formatDate, formatStatus, getStatusClass } from '@/utils/formatters';

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
});
// --- Biến chứa địa chỉ và đơn hàng gần nhất ---
const addresses = ref([]);
const recentOrders = ref([]);

const route = useRoute();
const router = useRouter();

const user = ref(null);
const loading = ref(true);
const error = ref(null);


const selectedTier = ref('');
const updatingTier = ref(false);
const tierUpdateError = ref(null);
const tierUpdateSuccess = ref(false);


const updatingStatus = ref(false);
const statusUpdateError = ref(null);
const statusUpdateSuccess = ref(false);

const loadingOrders = ref(false);
const errorOrders = ref(null);

const loadingAddresses = ref(false);
const errorAddresses = ref(null);


// thông tin chi tiết khách hàng
const fetchUserDetail = async () => {
  loading.value = true;
  error.value = null;
  user.value = null;
  addresses.value = [];
  recentOrders.value = [];
  resetUpdateStates();

  try {
    // Lấy thông tin user cơ bản
    const responseUser = await getAdminCustomerDetail(props.userId);
    user.value = {
      ...responseUser.data,
      id: responseUser.data.userId || responseUser.data.id, // Ánh xạ id từ userId hoặc id
    };
    selectedTier.value = user.value?.tier || 'BRONZE';

   
  } catch (err) {
    console.error(`Error fetching user detail for ID ${props.userId}:`, err);
    error.value = err.response?.data?.message || "Không thể tải chi tiết khách hàng.";
  } finally {
    loading.value = false;
  }
};


// thông tin 5 đơn hàng gần nhất
const fetchRecentOrders = async () => {
  loadingOrders.value = true;
  errorOrders.value = null;
  try {
    const response = await getRecentOrders(props.userId); // Truyền userId cho API
    recentOrders.value = (response.data || []).map(order => ({
      orderId: order.orderId || order.id, // Ánh xạ orderId hoặc id
      code: order.code,
      orderDate: order.orderDate,
      status: order.status,
      totalAmount: order.totalAmount
    }));
  } catch (err) {
    console.error(`Error fetching recent orders for user ${props.userId}:`, err);
    errorOrders.value = "Lỗi tải đơn hàng gần đây.";
  } finally {
    loadingOrders.value = false;
  }
};

// thông tin địa chỉ khách hàng
const fetchUserAddresses = async () => {
  loadingAddresses.value = true;
  errorAddresses.value = null;
  try {
    const response = await getUserAddresses(props.userId);
    addresses.value = (response.data || []).map(addr => ({
      id: addr.id || addr.addressId, // Ánh xạ id hoặc addressId
      recipientName: addr.recipientName,
      recipientPhone: addr.recipientPhone,
      streetAddress: addr.streetAddress,
      ward: addr.ward,
      district: addr.district,
      city: addr.city,
      isDefaultShipping: addr.isDefaultShipping
    }));
  } catch (err) {
    console.error(`Error fetching addresses for user ${props.userId}:`, err);
    errorAddresses.value = "Lỗi tải danh sách địa chỉ.";
  } finally {
    loadingAddresses.value = false;
  }
};

const handleUpdateTier = async () => {
  if (!user.value || selectedTier.value === user.value.tier) return;
  updatingTier.value = true;
  tierUpdateError.value = null;
  tierUpdateSuccess.value = false;
  try {
    const response = await updateAdminCustomerTier(user.value.id, selectedTier.value);

    if (user.value) {
      user.value.tier = response.data.tier;
    }
    tierUpdateSuccess.value = true;
    setTimeout(() => tierUpdateSuccess.value = false, 3000);
  } catch (err) {
    console.error(`Error updating tier for user ${user.value.id}:`, err);
    tierUpdateError.value = err.response?.data?.message || "Lỗi cập nhật bậc khách hàng.";
    setTimeout(() => tierUpdateError.value = null, 5000);
  } finally {
    updatingTier.value = false;
  }
};

const handleUpdateStatus = async () => {
  if (updatingStatus.value) return;
  const newStatus = !user.value.isActive;
  const actionText = newStatus ? 'mở khóa' : 'khóa';
  if (!confirm(`Bạn có chắc muốn ${actionText} tài khoản "${user.value.username}"?`)) return;

  updatingStatus.value = true;
  try {
    await updateAdminCustomerStatus(user.value.id, newStatus);
    user.value.isActive = newStatus;
  } catch (err) {
    console.error(`Lỗi khi ${actionText} tài khoản cho user ${user.value.id}:`, err);
    alert(`Lỗi khi ${actionText} tài khoản: ${err.response?.data?.message || err.message}`);
  } finally {
    updatingStatus.value = false;
  }
};

const resetUpdateStates = () => {
  updatingTier.value = false; tierUpdateError.value = null; tierUpdateSuccess.value = false;
  updatingStatus.value = false; statusUpdateError.value = null; statusUpdateSuccess.value = false;
};


onMounted(() => {
  if (!props.userId) {
    console.error('User ID is missing in props');
    error.value = "User ID không hợp lệ.";
    return;
  }
  fetchUserDetail();
  fetchRecentOrders();
  fetchUserAddresses();
});

watch(() => props.userId, async () => {
  if (!props.userId) {
    console.error('User ID is missing in props');
    error.value = "User ID không hợp lệ.";
    return;
  }
  await fetchUserDetail();
  await fetchRecentOrders();
  await fetchUserAddresses();
}, { immediate: true });

</script>

<style scoped>
.admin-user-detail-view dt {
  font-weight: 500;
  color: #6c757d;
}

.admin-user-detail-view dd {
  margin-bottom: 0.5rem;
}

.list-group-item small {
  display: block;
}

.list-group-item .badge {
  font-size: 0.8em;
}
</style>
