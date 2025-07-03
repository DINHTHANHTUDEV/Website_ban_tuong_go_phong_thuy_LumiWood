<template>
  <div class="checkout-view container mt-4 mb-5">
    <h1 class="text-center mb-4">Thanh Toán Đơn Hàng</h1>

    <div v-if="loadingInfo || loadingShipping" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải thông tin...</span>
      </div>
      <p v-if="loadingInfo" class="mt-2 small text-muted">Đang tải thông tin tài khoản...</p>
      <p v-if="loadingShipping" class="mt-2 small text-muted">Đang tải phương thức vận chuyển...</p>
    </div>
    <div v-else-if="errorInfo || errorShipping" class="alert alert-danger">
      <p v-if="errorInfo">Lỗi tải thông tin tài khoản: {{ errorInfo }}</p>
      <p v-if="errorShipping" :class="{ 'mt-2': errorInfo }">
        Lỗi tải phương thức vận chuyển: {{ errorShipping }}
      </p>
    </div>
    <form v-else @submit.prevent="handlePlaceOrder" novalidate>
      <div class="row g-4">
        <div class="col-lg-7">
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">1. Thông tin giao hàng</h5>
            </div>
            <div class="card-body">
              <!-- danh sách địa chỉ giao hàng đã lưu -->
              <div v-if="!isGuest && checkoutInfo?.savedAddresses?.length > 0">
                <div class="mb-3">
                  <label class="form-label fw-medium">Chọn địa chỉ đã lưu:</label>
                  <div v-for="addr in checkoutInfo.savedAddresses" :key="addr.id" class="form-check mb-2">
                    <input class="form-check-input" type="radio" name="shippingAddressOption" :id="'addr-' + addr.id"
                      :value="addr.id" v-model="selectedAddressId" @change="useNewAddress = false"
                      :disabled="placingOrder" />
                    <label class="form-check-label" :for="'addr-' + addr.id">
                      <strong>{{ addr.recipientName }}</strong> -
                      <span v-if="addr.recipientPhone">{{ addr.recipientPhone }}</span>
                      <span v-else class="text-muted">(Số điện thoại không có)</span><br />
                      {{ addr.streetAddress }},
                      {{ addr.ward ? addr.ward + ", " : "" }}{{ addr.district }}, {{ addr.city }}
                      <span v-if="addr.isDefaultShipping" class="badge bg-secondary ms-1">Mặc định</span>
                    </label>
                  </div>
                </div>
                <div class="mb-3">
                  <button type="button" class="btn btn-sm btn-outline-secondary" @click="toggleNewAddress"
                    :disabled="placingOrder">
                    {{ useNewAddress ? "Sử dụng địa chỉ đã lưu" : "Hoặc nhập địa chỉ mới" }}
                  </button>
                </div>
              </div>
              <!-- thêm địa chỉ giao hàng mới -->
              <div v-if="isGuest || useNewAddress || (!isGuest && !checkoutInfo?.savedAddresses?.length)">
                <h6 v-if="!isGuest && checkoutInfo?.savedAddresses?.length > 0" class="mb-3">
                  Nhập địa chỉ mới:
                </h6>
                <div class="row g-3">
                  <div class="col-md-6">
                    <label for="recipientName" class="form-label">Họ tên người nhận <span
                        class="text-danger">*</span></label>
                    <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.recipientName }"
                      id="recipientName" v-model.trim="shippingAddressInput.recipientName" required
                      :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.recipientName }}</div>
                  </div>
                  <div class="col-md-6">
                    <label for="recipientPhone" class="form-label">Số điện thoại <span
                        class="text-danger">*</span></label>
                    <input type="tel" class="form-control" :class="{ 'is-invalid': validationErrors.recipientPhone }"
                      id="recipientPhone" v-model.trim="shippingAddressInput.recipientPhone" required
                      :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.recipientPhone }}</div>
                  </div>
                  <div class="col-12" v-if="isGuest">
                    <label for="recipientEmail" class="form-label">Email <span class="text-danger">*</span></label>
                    <input type="email" class="form-control" :class="{ 'is-invalid': validationErrors.recipientEmail }"
                      id="recipientEmail" v-model.trim="shippingAddressInput.recipientEmail" required
                      :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.recipientEmail }}</div>
                  </div>
                  <div class="col-12">
                    <label for="streetAddress" class="form-label">Địa chỉ cụ thể (Số nhà, tên đường) <span
                        class="text-danger">*</span></label>
                    <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.streetAddress }"
                      id="streetAddress" v-model.trim="shippingAddressInput.streetAddress" required
                      :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.streetAddress }}</div>
                  </div>
                  <div class="col-md-4">
                    <label for="city" class="form-label">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.city }" id="city"
                      v-model.trim="shippingAddressInput.city" required :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.city }}</div>
                  </div>
                  <div class="col-md-4">
                    <label for="district" class="form-label">Quận/Huyện <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.district }"
                      id="district" v-model.trim="shippingAddressInput.district" required :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.district }}</div>
                  </div>
                  <div class="col-md-4">
                    <label for="ward" class="form-label">Phường/Xã</label>
                    <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.ward }" id="ward"
                      v-model.trim="shippingAddressInput.ward" :disabled="placingOrder" />
                    <div class="invalid-feedback">{{ validationErrors.ward }}</div>
                  </div>
                </div>
                <div class="mt-3">
                  <button type="button" class="btn btn-primary" @click="handleAddAddress" :disabled="placingOrder">
                    Lưu địa chỉ mới
                  </button>
                </div>
                <div v-if="errorInfo" class="text-danger small mt-2">
                  {{ errorInfo }} <!-- Hiển thị lỗi nếu có khi thêm địa chỉ -->
                </div>
                <div v-if="validationErrors.addressSelection" class="text-danger small mt-2">
                  {{ validationErrors.addressSelection }}
                </div>
              </div>
            </div>
          </div>
          <!-- phương thức vận chuyển -->
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">2. Phương thức vận chuyển</h5>
            </div>
            <div class="card-body">
              <div v-if="!availableShippingMethods || availableShippingMethods.length === 0"
                class="alert alert-warning small">
                Không có phương thức vận chuyển nào khả dụng.
              </div>
              <div v-else>
                <div v-for="method in availableShippingMethods" :key="method.id" class="form-check mb-2">
                  <input class="form-check-input" type="radio" name="shippingMethod" :id="'ship-' + method.id"
                    :value="method.id" v-model="selectedShippingMethodId" required :disabled="placingOrder" />
                  <label class="form-check-label w-100" :for="'ship-' + method.id">
                    <div class="d-flex justify-content-between">
                      <div>
                        <strong>{{ method.name }}</strong>
                        <small v-if="method.description" class="d-block text-muted">{{ method.description }}</small>
                        <small v-if="method.estimatedDelivery" class="d-block text-muted">Dự kiến: {{
                          method.estimatedDelivery }}</small>
                      </div>
                      <span class="fw-medium ms-2">{{ formatCurrency(method.baseCost) }}</span>
                    </div>
                  </label>
                </div>
                <div v-if="validationErrors.shippingMethodId" class="text-danger small mt-1">
                  {{ validationErrors.shippingMethodId }}
                </div>
              </div>
            </div>
          </div>


          <!-- phương thức thanh toán -->
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">3. Phương thức thanh toán</h5>
            </div>
            <div class="card-body">
              <div class="form-check mb-2">
                <input class="form-check-input" type="radio" name="paymentMethod" id="payment-cod" value="COD"
                  v-model="selectedPaymentMethod" required :disabled="placingOrder || (finalTotal >= 10000000)" />
                <label class="form-check-label" for="payment-cod"><i class="bi bi-cash-coin me-1"></i> Thanh toán khi
                  nhận hàng (COD)</label>
              </div>
              <div class="form-check mb-4">
                <input class="form-check-input" type="radio" name="paymentMethod" id="payment-bank"
                  value="BANK_TRANSFER" v-model="selectedPaymentMethod" required :disabled="placingOrder"
                  :checked="finalTotal >= 10000000" />
                <label class="form-check-label" for="payment-bank">
                  <i class="bi bi-bank text-primary me-2"></i> Chuyển khoản ngân hàng
                </label>
                <!-- Hiển thị thông tin chuyển khoản khi finalTotal >= 10000000 -->
                <div v-if="finalTotal >= 10000000 || selectedPaymentMethod === 'BANK_TRANSFER'" class="mt-3">
                  <div class="row g-3">
                    <div class="col-12">
                      <div class="card border-0 payment-info-box p-4"
                        style="background: linear-gradient(135deg, #ff7e5f, #feb47b); border-radius: 15px; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);">
                        <div class="row align-items-center">
                          <!-- Thông tin chuyển khoản -->
                          <div class="col-md-8">
                            <h5 class="text-white fw-bold mb-3">Thông tin chuyển khoản</h5>
                            <ul class="list-unstyled mb-0">
                              <li class="text-white mb-2"><strong>Ngân hàng:</strong> ABC Bank</li>
                              <li class="text-white mb-2"><strong>Số TK:</strong> 1234567890</li>
                              <li class="text-white mb-2"><strong>Chủ TK:</strong> Tượng Gỗ Phong Thủy</li>
                              <li class="text-white mb-2"><strong>Nội dung:</strong> Mã đơn hàng [Mã đơn hàng sẽ được
                                cấp sau khi đặt]</li>
                            </ul>
                          </div>
                          <!-- QR Code -->
                          <div class="col-md-4 text-center">
                            <p class="text-white fw-semibold mb-2">Mã QR Thanh Toán</p>
                            <img src="@/assets/images/qrcode.png" alt="QR Code Thanh Toán" class="img-fluid rounded"
                              style="max-width: 150px; height: auto; border: 2px solid #fff; border-radius: 10px;">
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-check mb-2">
                <input class="form-check-input" type="radio" name="paymentMethod" id="payment-vnpay" value="VNPAY"
                  v-model="selectedPaymentMethod" required :disabled="placingOrder || (finalTotal >= 10000000)" />
                <label class="form-check-label" for="payment-vnpay"><i class="bi bi-credit-card me-1"></i> Thanh toán
                  qua VNPAY</label>
              </div>
              <div v-if="validationErrors.paymentMethod" class="text-danger small mt-1">
                {{ validationErrors.paymentMethod }}
              </div>
            </div>
          </div>


          <!-- ghi chú đơn hàng -->
          <div class="card shadow-sm">
            <div class="card-header bg-light">
              <h5 class="mb-0">4. Ghi chú đơn hàng (tùy chọn)</h5>
            </div>
            <div class="card-body">
              <textarea class="form-control" id="orderNote" rows="3" v-model="orderNote"
                placeholder="Thêm ghi chú cho người bán..." :disabled="placingOrder"></textarea>
            </div>
          </div>
        </div>


        <!-- tóm tắt đơn hàng, các sản phẩm trong giỏ hàng, tổng tiền, phí vận chuyển, mã giảm giá -->
        <div class="col-lg-5">
          <div class="card shadow-sm sticky-top" style="top: 80px">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0">Tóm tắt đơn hàng</h5>
            </div>
            <div class="card-body">
              <ul class="list-unstyled mb-3 small">
                <li v-for="item in cartStore.items" :key="item.productId"
                  class="d-flex justify-content-between align-items-center mb-2 border-bottom pb-1">
                  <div class="d-flex align-items-center">
                    <img :src="item.imageUrl || defaultImage" :alt="item.productName" width="40" class="me-2 rounded" />
                    <span>{{ item.productName }}
                      <span class="text-muted">x {{ item.quantity }}</span></span>
                  </div>
                  <span class="fw-medium">{{ formatCurrency(item.price * item.quantity) }}</span>
                </li>
              </ul>
              <hr />
              <ul class="list-group list-group-flush mb-3">
                <li class="list-group-item d-flex justify-content-between px-0">
                  <span>Tạm tính:</span>
                  <span>{{ formatCurrency(cartStore.subtotal) }}</span>
                </li>
                <li class="list-group-item d-flex justify-content-between px-0">
                  <span>Phí vận chuyển:</span>
                  <span v-if="selectedShippingMethod">{{ formatCurrency(shippingCost) }}</span>
                  <span v-else class="text-muted">Chọn PTVC</span>
                </li>
                <li v-if="appliedPromotionSummary"
                  class="list-group-item d-flex justify-content-between px-0 text-success">
                  <span>Giảm giá ({{ appliedPromotionSummary.code }}):</span>
                  <span>- {{ formatCurrency(discountAmount) }}</span>
                </li>
                <li class="list-group-item d-flex justify-content-between px-0 fw-bold fs-5 border-top pt-2">
                  <span>Tổng cộng:</span>
                  <span>{{ formatCurrency(finalTotal) }}</span>
                </li>
                <!-- Hiển thị thông báo và thông tin đặt cọc khi tổng tiền >= 10 triệu -->
                <div v-if="finalTotal >= 10000000" class="mb-2">
                  <p class="text-danger fw-medium small">
                    <strong>Lưu ý:</strong> Với đơn hàng có giá trị trên 10.000.000 VNĐ, Quý khách vui lòng đặt cọc 30%
                    giá trị đơn hàng để xác nhận đặt mua. Số tiền còn lại sẽ được thanh toán sau khi nhận hàng. Cảm ơn
                    Quý khách!
                  </p>
                </div>
                <li v-if="finalTotal >= 10000000"
                  class="list-group-item d-flex justify-content-between px-0 text-danger fw-medium">
                  <span>Đặt cọc (30%):</span>
                  <span>{{ formatCurrency(finalTotal * 0.3) }}</span>
                </li>
                <li v-if="finalTotal >= 10000000"
                  class="list-group-item d-flex justify-content-between px-0 text-danger fw-medium">
                  <span>Phần còn lại (70%):</span>
                  <span>{{ formatCurrency(finalTotal * 0.7) }}</span>
                </li>
              </ul>
              <div v-if="errorPlaceOrder" class="alert alert-danger">
                {{ errorPlaceOrder }}
              </div>
              <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-danger btn-lg"
                  :disabled="placingOrder || cartStore.items.length === 0 || loadingInfo || loadingShipping || !availableShippingMethods || availableShippingMethods.length === 0">
                  <span v-if="placingOrder" class="spinner-border spinner-border-sm me-2" role="status"
                    aria-hidden="true"></span>
                  {{ placingOrder ? "Đang xử lý..." : "Đặt Hàng Ngay" }}
                </button>
                <router-link :to="{ name: 'shoppingCart' }" class="btn btn-outline-secondary">
                  <i class="bi bi-arrow-left-short"></i> Quay lại Giỏ hàng
                </router-link>
                <p v-if="cartStore.items.length === 0" class="text-danger small text-center mt-2">
                  Giỏ hàng trống, không thể đặt hàng.
                </p>
                <p v-if="availableShippingMethods && availableShippingMethods.length === 0 && !loadingShipping"
                  class="text-danger small text-center mt-2">
                  Không có PTVC, không thể đặt hàng.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
// Import các hook và tính năng từ Vue để quản lý trạng thái và vòng đời component
import { useRouter } from "vue-router";
// Import useRouter để điều hướng giữa các trang
import { useAuthStore } from "@/store/auth.js";
// Import store để kiểm tra trạng thái đăng nhập của user
import { useCartStore } from "@/store/cart.js";
// Import store để quản lý giỏ hàng
import { getCheckoutInfo, placeOrder, addAddress } from "@/http/modules/public/checkoutService.js";
// Import các hàm API liên quan đến thông tin thanh toán và đặt hàng
import { getActiveShippingMethods } from "@/http/modules/public/shippingService.js";
// Import hàm API để lấy danh sách phương thức vận chuyển
import defaultImage from "@/assets/images/placeholder.png";
// Import hình ảnh mặc định để sử dụng nếu cần

const router = useRouter();
// Khởi tạo router để điều hướng trang
const authStore = useAuthStore();
// Khởi tạo store xác thực để kiểm tra trạng thái đăng nhập
const cartStore = useCartStore();
// Khởi tạo store giỏ hàng để lấy thông tin giỏ hàng

// Khai báo các biến phản ứng để lưu trữ trạng thái
const checkoutInfo = ref(null);
// Lưu thông tin checkout (địa chỉ, phương thức vận chuyển, v.v.) từ API
const shippingMethods = ref([]);
// Lưu danh sách phương thức vận chuyển khả dụng
const loadingInfo = ref(false);
// Trạng thái tải thông tin checkout (true khi đang tải, false khi hoàn tất)
const errorInfo = ref(null);
// Lưu thông báo lỗi nếu có khi tải thông tin checkout
const loadingShipping = ref(false);
// Trạng thái tải danh sách phương thức vận chuyển
const errorShipping = ref(null);
// Lưu thông báo lỗi nếu có khi tải phương thức vận chuyển

// Biến điều khiển liên quan đến địa chỉ giao hàng
const selectedAddressId = ref(null);
// Lưu ID của địa chỉ được chọn từ danh sách địa chỉ đã lưu
const useNewAddress = ref(false);
// Cờ xác định xem user đang dùng địa chỉ mới hay địa chỉ đã lưu
const shippingAddressInput = reactive({
  // Đối tượng phản ứng để nhập thông tin địa chỉ mới
  recipientName: "",
  // Tên người nhận
  recipientPhone: "",
  // Số điện thoại người nhận
  recipientEmail: "",
  // Email người nhận (chỉ áp dụng cho khách lẻ)
  streetAddress: "",
  // Địa chỉ cụ thể (số nhà, tên đường)
  ward: "",
  // Phường/Xã
  district: "",
  // Quận/Huyện
  city: "",
  // Tỉnh/Thành phố
});

const selectedShippingMethodId = ref(null);
// Lưu ID của phương thức vận chuyển được chọn
const selectedPaymentMethod = ref("COD"); // Khởi tạo mặc định là COD, sẽ được cập nhật sau
const orderNote = ref("");
// Ghi chú cho đơn hàng (nếu có)
const placingOrder = ref(false);
// Trạng thái đặt hàng (true khi đang xử lý, false khi hoàn tất)
const errorPlaceOrder = ref(null);
// Lưu thông báo lỗi nếu có khi đặt hàng
const validationErrors = reactive({});
// Đối tượng phản ứng để lưu các lỗi xác thực form
const appliedPromotionSummary = ref(null);
// Lưu thông tin khuyến mãi áp dụng (nếu có)

const isGuest = computed(() => !authStore.isAuthenticated);
// Tính toán xem user có phải là khách lẻ không (true nếu chưa đăng nhập)

const availableShippingMethods = computed(() => {
  // Tính toán danh sách phương thức vận chuyển khả dụng
  if (!isGuest.value && checkoutInfo.value?.availableShippingMethods) {
    // Nếu user đã đăng nhập và checkoutInfo có phương thức vận chuyển, sử dụng từ checkoutInfo
    return checkoutInfo.value.availableShippingMethods;
  }
  // Ngược lại, sử dụng danh sách từ shippingMethods
  return shippingMethods.value;
});

const selectedShippingMethod = computed(() => {
  // Tính toán phương thức vận chuyển được chọn
  if (!selectedShippingMethodId.value || !availableShippingMethods.value) return null;
  // Tìm phương thức vận chuyển tương ứng với ID đã chọn
  return availableShippingMethods.value.find((m) => m.id === selectedShippingMethodId.value);
});

const shippingCost = computed(() => selectedShippingMethod.value?.baseCost || 0);
// Tính toán chi phí vận chuyển (lấy từ phương thức được chọn, mặc định 0 nếu không có)

const discountAmount = computed(() => appliedPromotionSummary.value?.discount || 0);
// Tính toán số tiền giảm giá (lấy từ khuyến mãi, mặc định 0 nếu không có)

const finalTotal = computed(() => {
  // Tính toán tổng tiền cuối cùng
  const total = cartStore.subtotal + shippingCost.value - discountAmount.value;
  return total > 0 ? total : 0; // Đảm bảo tổng không âm
});

const fetchShippingMethods = async () => {
  // Hàm lấy danh sách phương thức vận chuyển từ API
  loadingShipping.value = true; // Bắt đầu tải
  errorShipping.value = null; // Xóa lỗi cũ
  try {
    const response = await getActiveShippingMethods(); // Gọi API
    console.log("Response:", response.data); // Kiểm tra cấu trúc dữ liệu
    shippingMethods.value = response.data || []; // Cập nhật danh sách phương thức
    if (
      shippingMethods.value.length > 0 &&
      (!selectedShippingMethodId.value ||
        !shippingMethods.value.some((m) => m.id === selectedShippingMethodId.value))
    ) {
      // Nếu có phương thức và chưa chọn hoặc không hợp lệ, chọn phương thức đầu tiên
      selectedShippingMethodId.value = shippingMethods.value[0].id;
    } else if (shippingMethods.value.length === 0) {
      // Nếu không có phương thức, xóa chọn
      selectedShippingMethodId.value = null;
    }
  } catch (err) {
    // Xử lý lỗi khi gọi API
    console.error("Error fetching shipping methods:", err);
    errorShipping.value = "Lỗi tải phương thức vận chuyển.";
    shippingMethods.value = [];
    selectedShippingMethodId.value = null;
  } finally {
    // Kết thúc tải
    loadingShipping.value = false;
  }
};

const fetchCheckoutInfoForUser = async () => {
  // Hàm lấy thông tin checkout cho user đã đăng nhập
  if (isGuest.value) return; // Thoát nếu là khách lẻ
  loadingInfo.value = true; // Bắt đầu tải
  errorInfo.value = null; // Xóa lỗi cũ
  try {
    const response = await getCheckoutInfo(); // Gọi API lấy thông tin
    console.log("API Response for checkout info:", response); // Log để debug

    // Cập nhật thông tin địa chỉ từ response
    checkoutInfo.value = checkoutInfo.value || {}; // Đảm bảo checkoutInfo là object
    checkoutInfo.value.savedAddresses = response.savedAddresses || []; // Lấy danh sách địa chỉ từ API
    checkoutInfo.value.defaultShippingAddress = response.defaultShippingAddress || null; // Lấy địa chỉ mặc định

    // Chọn địa chỉ mặc định hoặc địa chỉ đầu tiên nếu có
    if (checkoutInfo.value.defaultShippingAddress) {
      selectedAddressId.value = checkoutInfo.value.defaultShippingAddress.id;
      useNewAddress.value = false;
    } else if (checkoutInfo.value.savedAddresses.length > 0) {
      selectedAddressId.value = checkoutInfo.value.savedAddresses[0].id;
      useNewAddress.value = false;
    } else {
      useNewAddress.value = true;
    }

    // Giữ nguyên phần phương thức vận chuyển (vì đã chạy tốt)
    if (response.data?.availableShippingMethods) {
      // Nếu API trả về phương thức vận chuyển, cập nhật
      shippingMethods.value = response.data.availableShippingMethods;
    }

    if (
      shippingMethods.value.length > 0 &&
      (!selectedShippingMethodId.value ||
        !shippingMethods.value.some((m) => m.id === selectedShippingMethodId.value))
    ) {
      // Nếu có phương thức và chưa chọn hoặc không hợp lệ, chọn phương thức đầu tiên
      selectedShippingMethodId.value = shippingMethods.value[0].id;
    } else if (shippingMethods.value.length === 0) {
      // Nếu không có phương thức, xóa chọn
      selectedShippingMethodId.value = null;
    }
  } catch (err) {
    // Xử lý lỗi khi gọi API
    console.error("Error fetching checkout info:", err);
    errorInfo.value = "Không thể tải thông tin thanh toán tài khoản.";
    if (shippingMethods.value.length === 0) {
      // Nếu không có phương thức, gọi lại fetchShippingMethods
      await fetchShippingMethods();
    }
  } finally {
    // Kết thúc tải
    loadingInfo.value = false;
  }
};

const toggleNewAddress = () => {
  // Chuyển đổi giữa dùng địa chỉ đã lưu và địa chỉ mới
  useNewAddress.value = !useNewAddress.value;
  if (!useNewAddress.value && checkoutInfo.value?.savedAddresses?.length > 0) {
    // Nếu dùng địa chỉ đã lưu và có danh sách, chọn địa chỉ mặc định hoặc đầu tiên
    selectedAddressId.value =
      checkoutInfo.value.defaultShippingAddress?.id || checkoutInfo.value.savedAddresses[0].id;
  } else {
    // Nếu dùng địa chỉ mới, xóa chọn
    selectedAddressId.value = null;
  }

  // Xóa các lỗi xác thực liên quan đến địa chỉ
  delete validationErrors.addressSelection;
  delete validationErrors.recipientName;
  delete validationErrors.recipientPhone;
  delete validationErrors.recipientEmail;
  delete validationErrors.streetAddress;
  delete validationErrors.city;
  delete validationErrors.district;
  delete validationErrors.ward;
};

// Hàm xử lý thêm địa chỉ mới
const handleAddAddress = async () => {
  // Hàm xử lý thêm địa chỉ mới
  placingOrder.value = true; // Bắt đầu xử lý, vô hiệu hóa form
  errorInfo.value = null; // Xóa lỗi cũ

  try {
    // Chuẩn bị dữ liệu từ form
    const addressData = {
      recipientName: shippingAddressInput.recipientName,
      recipientPhone: shippingAddressInput.recipientPhone,
      recipientEmail: isGuest.value ? shippingAddressInput.recipientEmail : undefined, // Chỉ gửi email nếu là khách
      streetAddress: shippingAddressInput.streetAddress,
      city: shippingAddressInput.city,
      district: shippingAddressInput.district,
      ward: shippingAddressInput.ward || '', // Phường/Xã có thể rỗng
      isDefaultShipping: !checkoutInfo.value?.defaultShippingAddress, // Đặt làm mặc định nếu chưa có địa chỉ mặc định
    };

    // Gọi API để thêm địa chỉ
    const newAddress = await addAddress(addressData);
    console.log('New address added:', newAddress);

    // Cập nhật checkoutInfo với địa chỉ mới
    if (!checkoutInfo.value) checkoutInfo.value = {};
    if (!checkoutInfo.value.savedAddresses) checkoutInfo.value.savedAddresses = [];
    checkoutInfo.value.savedAddresses.push(newAddress); // Thêm địa chỉ mới vào danh sách
    if (!checkoutInfo.value.defaultShippingAddress) {
      checkoutInfo.value.defaultShippingAddress = newAddress; // Đặt làm mặc định nếu chưa có
    }
    useNewAddress.value = false; // Chuyển về dùng địa chỉ đã lưu
    selectedAddressId.value = newAddress.id; // Chọn địa chỉ vừa thêm (nếu API trả về id)

    // Xóa form sau khi thêm thành công
    Object.keys(shippingAddressInput).forEach(key => (shippingAddressInput[key] = ''));
  } catch (err) {
    console.error('Failed to add address:', err);
    errorInfo.value = 'Không thể thêm địa chỉ. Vui lòng thử lại.';
  } finally {
    placingOrder.value = false; // Kết thúc xử lý
  }
};

const formatCurrency = (value) => {
  // Định dạng giá tiền theo kiểu tiền tệ Việt Nam (VND)
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value || 0);
};

const validateClientForm = () => {
  // Hàm kiểm tra tính hợp lệ của form
  Object.keys(validationErrors).forEach((key) => delete validationErrors[key]); // Xóa lỗi cũ
  let isValid = true;

  // Kiểm tra địa chỉ nếu không phải khách lẻ và không dùng địa chỉ mới
  if (!isGuest.value && !selectedAddressId.value && !useNewAddress.value) {
    validationErrors.addressSelection = "Vui lòng chọn hoặc nhập địa chỉ giao hàng.";
    isValid = false;
  }
  if (useNewAddress.value || isGuest.value) {
    if (!shippingAddressInput.recipientName) {
      validationErrors.recipientName = "Vui lòng nhập tên người nhận.";
      isValid = false;
    }
    if (!shippingAddressInput.recipientPhone) {
      validationErrors.recipientPhone = "Vui lòng nhập số điện thoại.";
      isValid = false;
    } else if (!/^(0[3|5|7|8|9])+([0-9]{8})\b/.test(shippingAddressInput.recipientPhone)) {
      validationErrors.recipientPhone = "Số điện thoại không hợp lệ.";
      isValid = false;
    }

    if (isGuest.value) {
      if (!shippingAddressInput.recipientEmail) {
        validationErrors.recipientEmail = "Vui lòng nhập email.";
        isValid = false;
      } else if (!/\S+@\S+\.\S+/.test(shippingAddressInput.recipientEmail)) {
        validationErrors.recipientEmail = "Email không hợp lệ.";
        isValid = false;
      }
    }

    if (!shippingAddressInput.streetAddress) {
      validationErrors.streetAddress = "Vui lòng nhập địa chỉ cụ thể.";
      isValid = false;
    }
    if (!shippingAddressInput.district) {
      validationErrors.district = "Vui lòng nhập Quận/Huyện.";
      isValid = false;
    }
    if (!shippingAddressInput.city) {
      validationErrors.city = "Vui lòng nhập Tỉnh/Thành phố.";
      isValid = false;
    }
  }

  if (!selectedShippingMethodId.value) {
    validationErrors.shippingMethodId = "Vui lòng chọn phương thức vận chuyển.";
    isValid = false;
  }

  if (finalTotal >= 10000000 && selectedPaymentMethod.value !== "BANK_TRANSFER") {
    validationErrors.paymentMethod = "Đơn hàng trên 10 triệu phải thanh toán qua Chuyển khoản ngân hàng.";
    isValid = false;
  } else if (!selectedPaymentMethod.value) {
    validationErrors.paymentMethod = "Vui lòng chọn phương thức thanh toán.";
    isValid = false;
  }

  return isValid; // Trả về kết quả xác thực
};

const handlePlaceOrder = async () => {
  // Hàm xử lý đặt hàng, gửi dữ liệu đến backend để lưu vào database
  errorPlaceOrder.value = null; // Xóa lỗi cũ trước khi xử lý
  if (!validateClientForm()) {
    // Kiểm tra tính hợp lệ của form, dừng nếu không hợp lệ
    const firstError = document.querySelector(".is-invalid, .text-danger.small");
    firstError?.scrollIntoView({ behavior: "smooth", block: "center" });
    return;
  }

  placingOrder.value = true; // Bật trạng thái đang xử lý đặt hàng

  // ⚠️ Nếu tổng đơn hàng ≥ 10 triệu, ép payment method là BANK_TRANSFER
  if (finalTotal.value >= 10000000) {
    selectedPaymentMethod.value = "BANK_TRANSFER";
    console.log("✅ Đơn hàng ≥ 10 triệu → Ép paymentMethod = BANK_TRANSFER");
  }

  // Chuẩn bị payload để gửi đến API backend
  const orderPayload = {
    shippingMethodId: selectedShippingMethodId.value, // ID phương thức vận chuyển được chọn
    paymentMethod: selectedPaymentMethod.value, // Phương thức thanh toán (COD hoặc BANK_TRANSFER)
    selectedShippingAddressId: !isGuest.value && !useNewAddress.value ? selectedAddressId.value : null, // ID địa chỉ đã lưu (nếu user không dùng địa chỉ mới)
    shippingAddressInput: (isGuest.value || useNewAddress.value) ? {
      // Thông tin địa chỉ mới (nếu guest hoặc dùng địa chỉ mới)
      recipientName: shippingAddressInput.recipientName,
      recipientPhone: shippingAddressInput.recipientPhone,
      recipientEmail: shippingAddressInput.recipientEmail, // Bao gồm email cho guest
      streetAddress: shippingAddressInput.streetAddress,
      ward: shippingAddressInput.ward,
      district: shippingAddressInput.district,
      city: shippingAddressInput.city
    } : null,
    orderNote: orderNote.value || null, // Ghi chú cho đơn hàng (tùy chọn)
    appliedPromotionCode: appliedPromotionSummary.value?.code || null // Mã giảm giá áp dụng (tùy chọn)
  };

  // Xác minh dữ liệu trước khi gửi
  if (!availableShippingMethods.value.some(m => m.id === orderPayload.shippingMethodId)) {
    errorPlaceOrder.value = "Phương thức vận chuyển không hợp lệ. Vui lòng chọn lại.";
    placingOrder.value = false;
    return;
  }

  // Xóa email nếu không phải khách lẻ (theo logic hiện tại)
  if (!isGuest.value && orderPayload.shippingAddressInput) {
    delete orderPayload.shippingAddressInput.recipientEmail; // Loại bỏ email cho user đã đăng nhập
  }

  // Kiểm tra email cho khách lẻ
  if (isGuest.value && orderPayload.shippingAddressInput && !orderPayload.shippingAddressInput.recipientEmail) {
    console.error("Guest email missing in payload!"); // Log lỗi nếu thiếu email
    errorPlaceOrder.value = "Lỗi: Email khách hàng bị thiếu."; // Hiển thị lỗi
    placingOrder.value = false; // Tắt trạng thái xử lý
    return;
  }

  console.log("Placing order with payload:", JSON.stringify(orderPayload, null, 2)); // Log payload để debug
  console.log("💳 Phương thức thanh toán đã chọn:", selectedPaymentMethod.value);

  try {
    const orderSummary = await placeOrder(orderPayload); // Gọi API để đặt hàng và lưu vào database
    console.log("Order placed successfully:", orderSummary); // Log phản hồi từ API

    // Sau khi đặt hàng thành công, xóa giỏ hàng và khuyến mãi khỏi client
    cartStore.clearClientCart(); // Xóa giỏ hàng trong store
    sessionStorage.removeItem("appliedPromo"); // Xóa mã khuyến mãi đã áp dụng

    // Xử lý chuyển hướng dựa trên phản hồi từ API
    if (orderSummary.paymentUrl) {
      window.location.href = orderSummary.paymentUrl; // Chuyển đến URL thanh toán nếu có
    } else {
      router.push({ name: "orderSuccess", params: { orderId: orderSummary.orderId } }); // Chuyển đến trang thành công
    }
  } catch (err) {
    // Xử lý lỗi nếu có khi gọi API
    console.error("Error placing order - Details:", err); // Log chi tiết lỗi để debug
    placingOrder.value = false; // Tắt trạng thái xử lý
    if (err.response) {
      errorPlaceOrder.value = `Lỗi server: ${err.response.status} - ${err.response.data.message || 'Vui lòng thử lại.'}`;
    } else if (err.message) {
      errorPlaceOrder.value = err.message; // Hiển thị thông báo lỗi từ API
    } else {
      errorPlaceOrder.value = "Đã có lỗi xảy ra trong quá trình đặt hàng. Vui lòng thử lại sau.";
    }
    window.scrollTo({ top: 0, behavior: "smooth" }); // Cuộn lên đầu trang để hiển thị lỗi
  } finally {
    placingOrder.value = false; // Đảm bảo trạng thái xử lý được tắt (dù thành công hay lỗi)
  }
  

};

onMounted(async () => {
  // Hàm chạy khi component được mount (tải lần đầu)
  if (cartStore.items.length === 0) {
    // Nếu giỏ hàng rỗng, chuyển hướng về trang giỏ hàng
    console.warn("Checkout page loaded with empty cart. Redirecting...");
    router.replace({ name: "shoppingCart" });
    return;
  }

  try {
    // Lấy thông tin khuyến mãi từ sessionStorage
    const savedPromo = sessionStorage.getItem("appliedPromo");
    if (savedPromo) {
      appliedPromotionSummary.value = JSON.parse(savedPromo);
      console.log("Loaded applied promo:", appliedPromotionSummary.value);
    }
  } catch (e) {
    // Xử lý lỗi khi parse dữ liệu khuyến mãi
    console.error("Could not parse promo from sessionStorage:", e);
    sessionStorage.removeItem("appliedPromo");
  }

  if (isGuest.value) {
    // Nếu là khách lẻ, yêu cầu nhập địa chỉ mới và tải phương thức vận chuyển
    useNewAddress.value = true;
    await fetchShippingMethods();
  } else {
    // Nếu đã đăng nhập, tải cả thông tin checkout và phương thức vận chuyển
    await Promise.all([
      fetchCheckoutInfoForUser(),
      fetchShippingMethods(),
    ]);
  }

  // Đặt phương thức thanh toán mặc định khi load trang
  if (finalTotal >= 10000000) {
    selectedPaymentMethod.value = "BANK_TRANSFER"; // Chọn Chuyển khoản nếu cần đặt cọc
    console.log("✅ Đơn hàng >= 10 triệu, chọn mặc định: BANK_TRANSFER");
  } else {
    selectedPaymentMethod.value = "COD"; // Khôi phục COD nếu dưới 10 triệu
    console.log("✅ Đơn hàng < 10 triệu, chọn mặc định: COD");
  }

  console.log("🎯 Phương thức thanh toán hiện tại:", selectedPaymentMethod.value);
});

// Theo dõi sự thay đổi của finalTotal để đặt phương thức thanh toán mặc định
watch(() => finalTotal, (newTotal) => {
  if (newTotal >= 10000000) {
    selectedPaymentMethod.value = "BANK_TRANSFER"; // Chọn Chuyển khoản nếu cần đặt cọc
    console.log("💳 Cập nhật: BANK_TRANSFER do tổng >= 10 triệu");
  } else {
    selectedPaymentMethod.value = "COD"; // Khôi phục COD nếu dưới 10 triệu
    console.log("💵 Cập nhật: COD do tổng < 10 triệu");
  }
});
</script>

<style scoped>
.checkout-view {
  min-height: 80vh;
}

.sticky-top {
  top: 80px;
}
</style>