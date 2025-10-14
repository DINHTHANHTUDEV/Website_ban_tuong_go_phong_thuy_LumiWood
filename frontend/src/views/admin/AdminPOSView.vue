<template>
    <div class="container-fluid p-3">
        <!-- Tiêu đề trang: POS (bán hàng tại cửa hàng) -->
        <div class="d-flex align-items-center">
            <h1 class="me-4">POS - bán hàng tại cửa hàng</h1>
        </div>

        <!-- Đơn hàng đang chờ + nút tạo đơn mới -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <!-- Danh sách đơn hàng treo -->
            <div class="d-flex align-items-center">
                <div>
                    <h6 class="fw-bold text-muted mb-2">Đơn hàng đang chờ</h6>
                    <ul class="list-inline mb-0">
                        <li v-for="(order, index) in pendingOrders" :key="index" class="list-inline-item">
                            <button class="btn"
                                :class="selectedOrderIndex === index ? 'btn-warning' : 'btn-outline-secondary'"
                                @click="selectOrder(index)">
                                Đơn {{ index + 1 }}
                            </button>
                        </li>
                        <li v-if="!pendingOrders.length" class="list-inline-item text-muted">
                            Không có đơn hàng
                        </li>
                    </ul>
                </div>
            </div>

            <!-- Nút tạo đơn hàng mới -->
            <button class="btn btn-primary" @click="createNewOrder">Tạo đơn hàng mới</button>
        </div>

        <div class="row g-3">
            <!-- Box trái: Thông tin khách hàng -->
            <div class="col-md-6">
                <div class="card h-100 shadow-sm p-3">
                    <!-- 1. Địa chỉ giao hàng -->
                    <div class="card shadow-sm mb-3">
                        <div class="card-body">
                            <h6 class="fw-bold mb-3">1. Thông tin giao hàng</h6>

                            <div class="row g-2 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Họ tên người nhận *</label>
                                    <input v-model="address.receiverName" type="text" class="form-control"
                                        placeholder="Nhập họ tên" required />
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Số điện thoại *</label>
                                    <input v-model="address.phone" type="text" class="form-control"
                                        placeholder="Nhập số điện thoại" required />
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Địa chỉ cụ thể (Số nhà, tên đường) *</label>
                                <input v-model="address.detail" type="text" class="form-control" required />
                            </div>

                            <div class="row g-2">
                                <div class="col-md-4">
                                    <label class="form-label">Tỉnh/Thành phố *</label>
                                    <input v-model="address.city" type="text" class="form-control" required />
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Quận/Huyện *</label>
                                    <input v-model="address.district" type="text" class="form-control" required />
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Phường/Xã</label>
                                    <input v-model="address.ward" type="text" class="form-control" required />
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 2. Vận chuyển -->
                    <div class="card shadow-sm mb-3">
                        <div class="card-body">
                            <h6 class="fw-bold mb-3">2. Phương thức vận chuyển</h6>

                            <div class="form-check mb-3 d-flex align-items-start" style="gap: 0.5rem;">
                                <input class="form-check-input mt-1" type="radio" id="fast" value="Giao hàng nhanh"
                                    v-model="currentOrder.shippingMethod" />
                                <label class="form-check-label w-100" for="fast">
                                    <div class="d-flex justify-content-between">
                                        <span><i class="fas fa-shipping-fast me-1 text-primary"></i>Giao hàng
                                            nhanh</span>
                                        <strong class="text-success">50.000 ₫</strong>
                                    </div>
                                    <small class="text-muted d-block ms-4">Giao trong ngày hoặc hôm sau (nội thành) · Dự
                                        kiến: 1-2 ngày làm việc</small>
                                </label>
                            </div>

                            <div class="form-check d-flex align-items-start" style="gap: 0.5rem;">
                                <input class="form-check-input mt-1" type="radio" id="standard"
                                    value="Giao hàng tiêu chuẩn" v-model="currentOrder.shippingMethod" />
                                <label class="form-check-label w-100" for="standard">
                                    <div class="d-flex justify-content-between">
                                        <span><i class="fas fa-truck me-1 text-primary"></i>Giao hàng tiêu chuẩn</span>
                                        <strong class="text-success">30.000 ₫</strong>
                                    </div>
                                    <small class="text-muted d-block ms-4">Giao trong vòng 3-5 ngày làm việc</small>
                                </label>
                            </div>
                        </div>
                    </div>

                    <!-- 3. Thanh toán -->
                    <div class="card shadow-sm mb-3">
                        <div class="card-body">
                            <h6 class="fw-bold mb-3">3. Phương thức thanh toán</h6>

                            <div class="form-check mb-2 d-flex align-items-center" v-for="method in paymentMethods"
                                :key="method">
                                <input class="form-check-input me-2" type="radio" :value="method"
                                    v-model="currentOrder.paymentMethod" />
                                <label class="form-check-label">
                                    <i class="fas"
                                        :class="method === 'Thanh toán khi nhận hàng' ? 'fa-money-bill-wave' : 'fa-university'"
                                        style="color: #0d6efd; margin-right: 5px;"></i>
                                    {{ method }}
                                </label>
                            </div>

                            <!-- Thông tin chuyển khoản -->
                            <div v-if="currentOrder.paymentMethod === 'Chuyển khoản ngân hàng'"
                                class="p-3 border bg-light mt-3 rounded">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <strong class="text-primary"><i class="fas fa-info-circle me-1"></i>Thông tin
                                            chuyển khoản:</strong>
                                        <p class="mb-1">Ngân hàng: <strong>ACB</strong></p>
                                        <p class="mb-1">Số tài khoản: <strong>123456789</strong></p>
                                        <p class="mb-1">Chủ tài khoản: <strong>Cty Gỗ phong thủy LumiWood</strong></p>
                                        <p class="mb-0">Nội dung: <em>Tên khách hàng - Tên sản phẩm</em></p>
                                    </div>
                                    <div class="col-md-4 text-center">
                                        <img src="@/assets/images/qrcode.png" class="img-fluid rounded border mt-2"
                                            style="max-width: 150px" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>


                    <!-- 4. Mã giảm giá -->
                    <div class="card shadow-sm mb-3">
                        <div class="card-body">
                            <h6 class="fw-bold mb-3">4. Mã giảm giá</h6>

                            <div class="d-flex gap-2">
                                <select v-model="discountCode" class="form-select">
                                    <option disabled value="">-- Chọn mã giảm giá --</option>
                                    <option v-for="code in availableDiscounts" :key="code.code" :value="code.code">
                                        {{ code.name }} - {{ code.percent }}%
                                    </option>
                                </select>
                                <button class="btn btn-success" @click="applyDiscount">Áp dụng</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Box phải: Danh sách sản phẩm và xử lý đơn -->
            <div class="col-md-6">
                <div class="card h-100 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title text-primary fw-bold mb-3">Danh sách sản phẩm</h5>

                        <div class="row row-cols-1 row-cols-sm-2 g-3">
                            <div v-for="product in products" :key="product.id" class="col">
                                <div class="card h-100 border-0 shadow-sm hover-shadow">
                                    <div class="card-body text-center">
                                        <h6 class="card-subtitle mb-2 text-muted">{{ product.name }}</h6>
                                        <p class="fw-bold text-success">{{ product.price.toLocaleString() }} VND</p>
                                        <input type="number" v-model.number="productQuantity" class="form-control mb-2"
                                            min="1" />
                                        <button @click="selectProduct(product)" class="btn btn-outline-primary w-100">
                                            Chọn
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4">
                            <h6 class="fw-medium">Sản phẩm đã chọn</h6>
                            <ul class="list-group mb-3">
                                <li v-for="(item, index) in currentOrder.items" :key="index"
                                    class="list-group-item d-flex justify-content-between align-items-center">
                                    {{ item.name }} x{{ item.quantity }}
                                    <span class="text-success">{{ (item.price * item.quantity).toLocaleString() }}
                                        VND</span>
                                    <button @click="removeProduct(index)" class="btn btn-sm btn-danger">Xóa</button>
                                </li>
                                <li v-if="currentOrder.items.length === 0"
                                    class="list-group-item text-center text-muted">
                                    Chưa có sản phẩm
                                </li>
                            </ul>

                            <!-- Tổng tiền -->
                            <div class="p-3 bg-light border rounded mb-3">
                                <p class="mb-1">Tạm tính: {{ baseTotal.toLocaleString() }} VND</p>
                                <p class="mb-1">Phí vận chuyển: {{ shippingFee.toLocaleString() }} VND</p>
                                <p v-if="discountAmount > 0" class="mb-1 text-success">
                                    Giảm giá: -{{ discountAmount.toLocaleString() }} VND
                                </p>
                                <h5 class="fw-bold text-primary">
                                    Thành tiền: {{ calculateTotal.toLocaleString() }}
                                    VND
                                </h5>
                            </div>

                            <div class="d-grid gap-2">
                                <button class="btn btn-primary" :disabled="!currentOrder.items.length"
                                    @click="submitOrder">
                                    Đặt hàng
                                </button>
                                <button class="btn btn-success" :disabled="!lastOrder" @click="printInvoice">
                                    Xuất hóa đơn (PDF)
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, reactive } from "vue";

const products = ref([
  { id: 1, name: "Tượng Di Lặc Cưỡi Cá", price: 5500000 },
  { id: 2, name: "Tượng Gỗ Rồng Vàng", price: 7000000 },
  { id: 3, name: "Tượng Gỗ Phật Bà Quan Âm", price: 4500000 },
]);

const address = reactive({
  receiverName: "",
  phone: "",
  detail: "",
  city: "",
  district: "",
  ward: "",
});

const currentOrder = ref({
  items: [],
  shippingMethod: "Giao hàng nhanh",
  paymentMethod: "Thanh toán khi nhận hàng (COD)",
});

const pendingOrders = ref([]);
const selectedOrderIndex = ref(null);
const lastOrder = ref(null);
const productQuantity = ref(1);
const discountCode = ref("");
const discountAmount = ref(0);
const shippingMethods = ["Giao hàng nhanh", "Giao hàng tiêu chuẩn"];
const paymentMethods = [
  "Thanh toán khi nhận hàng (COD)",
  "Chuyển khoản ngân hàng",
  "Thanh toán qua VNPAY",
];

const logAction = (msg) => {
  const time = new Date().toLocaleTimeString();
  console.log(`[${time}]`, msg);
};

const selectProduct = (product) => {
  if (productQuantity.value <= 0) return;
  const existing = currentOrder.value.items.find((item) => item.id === product.id);
  if (existing) {
    existing.quantity += productQuantity.value;
  } else {
    currentOrder.value.items.push({ ...product, quantity: productQuantity.value });
  }
  productQuantity.value = 1;
};

const removeProduct = (index) => {
  currentOrder.value.items.splice(index, 1);
};

const shippingFee = computed(() =>
  currentOrder.value.shippingMethod === "Giao hàng nhanh" ? 50000 : 30000
);
const baseTotal = computed(() =>
  currentOrder.value.items.reduce((sum, item) => sum + item.price * item.quantity, 0)
);
const calculateTotal = computed(() => baseTotal.value + shippingFee.value - discountAmount.value);

// const applyDiscount = () => {
//   if (discountCode.value === "GIAM20") {
//     discountAmount.value = baseTotal.value * 0.2;
//   } else {
//     discountAmount.value = 0;
//   }
//   discountCode.value = "";
// };

const createNewOrder = () => {
  pendingOrders.value.push({
    ...JSON.parse(JSON.stringify(currentOrder.value)),
    address: JSON.parse(JSON.stringify(address)),
  });
  currentOrder.value.items = [];
  discountAmount.value = 0;
  address.receiverName = "";
  address.phone = "";
  address.detail = "";
  address.city = "";
  address.district = "";
  address.ward = "";
  selectedOrderIndex.value = pendingOrders.value.length - 1; // Chọn đơn vừa tạo
};

const selectOrder = (index) => {
  selectedOrderIndex.value = index;
  const selected = pendingOrders.value[index];
  if (selected) {
    currentOrder.value = JSON.parse(JSON.stringify(selected));
    address.receiverName = selected.address.receiverName;
    address.phone = selected.address.phone;
    address.detail = selected.address.detail;
    address.city = selected.address.city;
    address.district = selected.address.district;
    address.ward = selected.address.ward;
  }
  console.log(`🛒 Đã chọn đơn hàng số ${index + 1}`, pendingOrders.value[index]);
};

const submitOrder = () => {
  if (!address.receiverName || !address.phone || !address.detail) {
    logAction("Địa chỉ chưa đầy đủ");
    return;
  }
  if (!currentOrder.value.items.length) return;
  lastOrder.value = {
    ...JSON.parse(JSON.stringify(currentOrder.value)),
    address: JSON.parse(JSON.stringify(address)),
    total: calculateTotal.value,
  };
  currentOrder.value.items = [];
  discountAmount.value = 0;
  logAction("Đặt hàng thành công");
};

// áp mã giảm giá
const availableDiscounts = ref([
    { code: 'SALE10', name: 'Giảm 10%', percent: 10 },
    { code: 'LUMI20', name: 'Lumi Ưu đãi', percent: 20 },
    { code: 'FREESHIP', name: 'Miễn phí vận chuyển', percent: 100 }
])

function applyDiscount() {
    if (!discountCode.value) {
        alert('Vui lòng chọn mã giảm giá!')
        return
    }
    console.log('Áp dụng mã giảm giá:', discountCode.value)
}

const printInvoice = () => {
  if (!lastOrder.value) return;
  alert(`Xuất hóa đơn đơn hàng của ${lastOrder.value.address.receiverName}`);
};
</script>

<style scoped>
.hover-shadow:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  transition: 0.3s;
}
</style>
