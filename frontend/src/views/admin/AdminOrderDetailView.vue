<template>
  <div class="admin-order-detail-view container mt-4 mb-5">

    <div class="mb-3">
      <router-link :to="{ name: 'adminOrderList' }" class="btn btn-sm btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách
      </router-link>
    </div>

    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải chi tiết đơn hàng...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      Lỗi tải chi tiết đơn hàng: {{ error }}
    </div>

    <div v-else-if="order" class="order-detail-content">
      <h1 class="mb-4">Chi tiết Đơn hàng <strong class="text-primary">#{{ order.orderId }}</strong></h1>

      <div class="row g-4">

        <!-- Thông tin chung -->
        <div class="col-lg-7">
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light"><strong>Thông tin chung</strong></div>
            <div class="card-body">
              <dl class="row mb-0">
                <dt class="col-sm-4">Ngày đặt:</dt>
                <dd class="col-sm-8">{{ formatDateTime(order.orderDate) }}</dd>

                <dt class="col-sm-4">Trạng thái:</dt>
                <dd class="col-sm-8">
                  <span class="badge rounded-pill fs-6" :class="getStatusClass(order.status)">
                    {{ formatStatus(order.status) }}
                  </span>
                </dd>

                <dt class="col-sm-4">Thanh toán:</dt>
                <dd class="col-sm-8">{{ order.paymentMethod }}</dd>

                <dt class="col-sm-4" v-if="order.orderNote">Ghi chú KH:</dt>
                <dd class="col-sm-8" v-if="order.orderNote" style="white-space: pre-wrap;">{{ order.orderNote }}</dd>
              </dl>
            </div>
          </div>

          <!-- Thông tin khách hàng & giao hàng -->
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light"><strong>Thông tin Khách hàng & Giao hàng</strong></div>
            <div class="card-body">
              <dl class="row mb-0">
                <dt class="col-sm-4">Khách hàng:</dt>
                <dd class="col-sm-8">
                  <span v-if="order.customerName">{{ order.customerName }} (ID: {{ order.userId }})</span>
                  <span v-else-if="order.guestEmail">{{ order.guestEmail }} (Guest)</span>
                  <span v-else>(Không xác định)</span>
                </dd>

                <dt class="col-sm-4">Người nhận:</dt>
                <dd class="col-sm-8">{{ order.shippingRecipientName }}</dd>

                <dt class="col-sm-4">Điện thoại:</dt>
                <dd class="col-sm-8">{{ order.shippingRecipientPhone }}</dd>

                <dt class="col-sm-4">Địa chỉ:</dt>
                <dd class="col-sm-8">
                  {{ order.shippingStreetAddress }},
                  <span v-if="order.shippingWard">{{ order.shippingWard }}, </span>
                  {{ order.shippingDistrict }}, {{ order.shippingCity }}
                </dd>

                <dt class="col-sm-4">Vận chuyển:</dt>
                <dd class="col-sm-8">{{ order.shippingMethodName || 'N/A' }} ({{ formatCurrency(order.shippingCost) }})</dd>

                <dt class="col-sm-4" v-if="order.promotionCode">Khuyến mãi:</dt>
                <dd class="col-sm-8 text-success" v-if="order.promotionCode">
                  {{ order.promotionCode }} (-{{ formatCurrency(order.discountAmount) }})
                </dd>
              </dl>
            </div>
          </div>
        </div>

        <!-- Sản phẩm & cập nhật trạng thái -->
        <div class="col-lg-5">

          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light"><strong>Sản phẩm đã đặt</strong></div>
            <div class="card-body p-0">
              <ul class="list-group list-group-flush">
                <li v-for="item in order.items" :key="item.productId" class="list-group-item d-flex align-items-center">
                  <img
                    :src="item.productImageUrl || defaultImage"
                    :alt="item.productName"
                    width="50"
                    class="me-3 rounded border"
                    @error="onImageError"
                  >
                  <div class="flex-grow-1">
                    <div class="fw-medium">{{ item.productName }}</div>
                    <small class="text-muted">SL: {{ item.quantity }} x {{ formatCurrency(item.priceAtPurchase) }}</small>
                  </div>
                  <span class="fw-bold ms-2">{{ formatCurrency(item.quantity * item.priceAtPurchase) }}</span>
                </li>
              </ul>
            </div>
            <div class="card-footer bg-light d-flex justify-content-between fw-bold fs-5">
              <span>Tổng cộng:</span>
              <span class="text-danger">{{ formatCurrency(order.totalAmount) }}</span>
            </div>
          </div>

          <div class="card shadow-sm">
            <div class="card-header bg-warning"><strong>Cập nhật trạng thái</strong></div>
            <div class="card-body">

              <div v-if="!canUpdateStatus" class="alert alert-secondary small p-2 text-center">
                Đơn hàng ở trạng thái "{{ formatStatus(order.status) }}" không thể thay đổi.
              </div>

              <div v-else>
                <label for="orderStatusSelect" class="form-label">Chọn trạng thái mới:</label>
                <select
                  id="orderStatusSelect"
                  class="form-select"
                  v-model="selectedNewStatus"
                  :disabled="updatingStatus"
                >
                  <option disabled value="">-- Chọn trạng thái --</option>
                  <option v-for="status in availableNextStatuses" :key="status" :value="status">
                    {{ formatStatus(status) }}
                  </option>
                </select>

                <div v-if="selectedNewStatus === 'CANCELLED'" class="mt-3">
                  <label for="cancelReason" class="form-label">
                    Lý do hủy đơn hàng <span class="text-danger">*</span>:
                  </label>
                  <textarea
                    id="cancelReason"
                    class="form-control"
                    rows="3"
                    v-model="cancelReason"
                    :disabled="updatingStatus"
                    placeholder="Nhập lý do hủy đơn hàng"
                  ></textarea>
                </div>

                <div v-if="updateError" class="alert alert-danger small p-2 mt-2">{{ updateError }}</div>
                <div v-if="updateSuccess" class="alert alert-success small p-2 mt-2">
                  Cập nhật trạng thái thành công!
                </div>

                <button
                  class="btn btn-primary w-100 mt-3"
                  @click="handleUpdateStatus"
                  :disabled="updatingStatus || !selectedNewStatus"
                >
                  <span v-if="updatingStatus" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  {{ updatingStatus ? 'Đang cập nhật...' : 'Lưu thay đổi' }}
                </button>
              </div>
            </div>
          </div>

        </div>

      </div>
    </div>

    <div v-else-if="!loading && !order && !error" class="alert alert-warning text-center">
      Không tìm thấy đơn hàng với ID này.
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { getAdminOrderDetail, updateOrderStatus } from '@/http/modules/admin/adminOrderService.js';
import defaultImage from '@/assets/images/placeholder.png';

const props = defineProps({
  orderId: {
    type: [String, Number],
    required: false
  }
});

const route = useRoute();
const internalOrderId = ref(props.orderId || route.params.orderId || null);

watch(() => props.orderId, (newVal) => {
  if (newVal) internalOrderId.value = newVal;
});

watch(() => route.params.orderId, (newVal) => {
  if (newVal) internalOrderId.value = newVal;
});

const order = ref(null);
const loading = ref(true);
const error = ref(null);
const selectedNewStatus = ref('');
const cancelReason = ref('');
const updatingStatus = ref(false);
const updateError = ref(null);
const updateSuccess = ref(false);

const allStatuses = {
  PENDING: 'Chờ xác nhận',
  PROCESSING: 'Đang xử lý',
  SHIPPING: 'Đang giao hàng',
  COMPLETED: 'Đã hoàn thành',
  CANCELLED: 'Đã hủy',
  PAYMENT_PENDING: 'Chờ thanh toán',
};

const validTransitions = {
  PENDING: ['PROCESSING', 'CANCELLED'],
  PAYMENT_PENDING: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['SHIPPING', 'CANCELLED'],
  SHIPPING: ['COMPLETED', 'CANCELLED'],
  COMPLETED: [],
  CANCELLED: [],
};

const availableNextStatuses = computed(() => {
  if (!order.value?.status) return [];
  return validTransitions[order.value.status] || [];
});

const canUpdateStatus = computed(() => {
  return availableNextStatuses.value.length > 0;
});

const fetchOrderDetail = async () => {
  if (!internalOrderId.value) return;
  loading.value = true;
  error.value = null;
  order.value = null;
  updateSuccess.value = false;
  updateError.value = null;
  selectedNewStatus.value = '';
  cancelReason.value = '';

  try {
    const response = await getAdminOrderDetail(internalOrderId.value);
    order.value = response.data || response;
  } catch (err) {
    console.error(`Error fetching order detail for ID ${internalOrderId.value}:`, err);
    if (err.response?.status === 404) {
      error.value = "Đơn hàng không tồn tại.";
    } else if (err.response?.status === 403) {
      error.value = "Bạn không có quyền xem đơn hàng này.";
    } else {
      error.value = "Không thể tải chi tiết đơn hàng.";
    }
  } finally {
    loading.value = false;
  }
};

const handleUpdateStatus = async () => {
  updateError.value = null;
  updateSuccess.value = false;

  if (!selectedNewStatus.value) {
    updateError.value = 'Vui lòng chọn trạng thái mới.';
    return;
  }
  if (selectedNewStatus.value === order.value.status) {
    updateError.value = 'Trạng thái mới phải khác trạng thái hiện tại.';
    return;
  }
  if (selectedNewStatus.value === 'CANCELLED' && !cancelReason.value.trim()) {
    updateError.value = 'Vui lòng nhập lý do hủy đơn hàng.';
    return;
  }

  updatingStatus.value = true;

  try {
    const payload = {
      newStatus: selectedNewStatus.value,
      cancelReason: selectedNewStatus.value === 'CANCELLED' ? cancelReason.value.trim() : null,
    };
    await updateOrderStatus(
      internalOrderId.value,
      selectedNewStatus.value,
      selectedNewStatus.value === 'CANCELLED' ? cancelReason.value.trim() : null
    );


    // Sau khi update thành công, gọi lại fetch chi tiết đơn hàng mới nhất
    await fetchOrderDetail();

    updateSuccess.value = true;
    selectedNewStatus.value = '';
    cancelReason.value = '';

    setTimeout(() => {
      updateSuccess.value = false;
    }, 3000);
  } catch (err) {
    console.error(`Error updating order status for ID ${internalOrderId.value}:`, err);
    updateError.value = err.response?.data?.message || "Cập nhật trạng thái thất bại.";
    setTimeout(() => {
      updateError.value = null;
    }, 5000);
  } finally {
    updatingStatus.value = false;
  }
};

const formatCurrency = (value) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0);

const formatDateTime = (dateString) =>
  dateString ? new Date(dateString).toLocaleString('vi-VN') : '';

const onImageError = (event) => {
  event.target.src = defaultImage;
};

const formatStatus = (status) => allStatuses[status] || status || 'N/A';

const getStatusClass = (status) => {
  const classMap = {
    PENDING: 'text-bg-secondary',
    PROCESSING: 'text-bg-info',
    SHIPPING: 'text-bg-primary',
    COMPLETED: 'text-bg-success',
    CANCELLED: 'text-bg-danger',
    PAYMENT_PENDING: 'text-bg-warning text-dark',
  };
  return classMap[status] || 'text-bg-light';
};

watch(internalOrderId, fetchOrderDetail, { immediate: true });
</script>


<style scoped>
.admin-order-detail-view dt {
  font-weight: 500;
  color: #6c757d;
}
.admin-order-detail-view dd {
  margin-bottom: 0.5rem;
}
.badge.fs-6 {
  padding: 0.4em 0.8em;
}
</style>
