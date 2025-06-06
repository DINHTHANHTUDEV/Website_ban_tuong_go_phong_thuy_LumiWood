<template>
  <div class="col">
    <div class="card h-100 product-card shadow-sm">
      <!-- Link bao toàn bộ card để click vào đâu cũng đi đến chi tiết -->
      <router-link
        :to="{ name: 'productDetail', params: { slug: product.slug } }"
        class="text-decoration-none text-dark product-link"
      >
        <div class="product-image-container">
          <img
            :src="product.imageUrl || defaultImage"
            class="card-img-top product-image"
            :alt="product.name"
            @error="onImageError"
            loading="lazy"
          />

          <!--
            Nút "Thêm vào giỏ":
            - Chỉ hiển thị khi:
              1. Sản phẩm còn hàng (stock > 0)
              2. Người dùng hiện tại KHÔNG phải là Admin (!authStore.isAdmin)
          -->
          <button
            v-if="product.stock > 0 && !authStore.isAdmin"
            type="button"
            class="btn btn-add-to-cart"
            title="Thêm vào giỏ hàng"
            aria-label="Thêm vào giỏ hàng"
            @click.stop.prevent="handleAddToCart"
          :disabled="isAdding"
          >
          <!-- Hiển thị spinner khi đang xử lý -->
          <span v-if="isAdding" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
          <!-- Hiển thị icon giỏ hàng khi không xử lý -->
          <img
            v-else
            src="https://www.iconpacks.net/icons/2/free-add-to-cart-icon-3046-thumb.png"
            alt="Thêm vào giỏ"
            class="cart-icon-img"
          />
          </button>

          <!--
            Thông báo "Hết hàng":
            - Chỉ hiển thị khi sản phẩm hết hàng (stock <= 0).
            - Dùng v-else-if để không bị hiển thị nếu nút "Thêm vào giỏ" đã hiển thị.
            - Sẽ không hiển thị gì ở khu vực này nếu sản phẩm còn hàng VÀ người dùng là admin (vì cả 2 điều kiện v-if và v-else-if đều sai)
          -->
          <div v-else-if="product.stock <= 0" class="out-of-stock-overlay">Hết hàng</div>

        </div>
        <div class="card-body d-flex flex-column">
          <h5 class="card-title product-name flex-grow-1">{{ product.name }}</h5>
          <p class="card-text product-price fw-bold">{{ formatCurrency(product.price) }}</p>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref } from 'vue';
import { RouterLink, useRouter, useRoute } from 'vue-router'; // Đảm bảo import đủ
import defaultImage from '@/assets/images/placeholder.png'; // Đường dẫn đến ảnh placeholder
import { useCartStore } from '@/store/cart';
import { useAuthStore } from '@/store/auth'; // Import store quản lý trạng thái đăng nhập

// Định nghĩa prop 'product' nhận từ component cha
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
});

// Khởi tạo các store và hook cần thiết
const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();
const route = useRoute(); // Để lấy fullPath cho việc lưu returnUrl
const isAdding = ref(false); // Biến trạng thái kiểm soát việc đang thêm vào giỏ

// Hàm định dạng tiền tệ Việt Nam
const formatCurrency = (value) => {
  // Trả về chuỗi rỗng nếu giá trị không hợp lệ, thay vì hiển thị '₫0' hoặc lỗi
  if (value === null || value === undefined || typeof value !== 'number') return '';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// Hàm xử lý khi ảnh bị lỗi, thay thế bằng ảnh placeholder
const onImageError = (event) => {
  event.target.src = defaultImage;
};

// Hàm xử lý khi nhấn nút "Thêm vào giỏ hàng"
const handleAddToCart = async () => {
  // --- Bước 1: Kiểm tra đăng nhập ---
  // Nếu chưa đăng nhập, lưu lại URL hiện tại và chuyển hướng đến trang login
  if (!authStore.isAuthenticated) {
    console.log("ProductCard: Chưa đăng nhập. Chuyển đến trang login.");
    authStore.setReturnUrl(route.fullPath); // Lưu URL để quay lại sau khi login thành công
    router.push({ name: 'login' });
    return; // Dừng thực thi hàm
  }

  // --- Bước 2: Kiểm tra các điều kiện khác (phòng trường hợp gọi hàm từ nơi khác hoặc template bị lỗi) ---
  // Dừng nếu: là admin, không có product, hết hàng, hoặc đang trong quá trình thêm (tránh click đúp)
  if (authStore.isAdmin || !props.product || props.product.stock <= 0 || isAdding.value) {
    console.log("ProductCard: Điều kiện không hợp lệ để thêm vào giỏ (Admin, hết hàng, đang xử lý...).");
    return;
  }

  // --- Bước 3: Thực hiện thêm vào giỏ ---
  isAdding.value = true; // Bắt đầu trạng thái loading
  console.log(`ProductCard: Đang thêm sản phẩm ${props.product.id} vào giỏ...`);
  try {
    // Gọi action 'addItem' từ cartStore, chỉ thêm 1 sản phẩm mỗi lần click từ card
    await cartStore.addItem(props.product.id, 1);
    console.log(`ProductCard: Thêm sản phẩm ${props.product.id} thành công.`);
    // Tại đây, KHÔNG chuyển hướng người dùng đi đâu cả.
    // Có thể hiển thị thông báo nhanh (toast) cho người dùng biết đã thêm thành công.
    // Ví dụ: toast.success('Đã thêm vào giỏ hàng!');
  } catch (error) {
    console.error(`ProductCard: Lỗi khi thêm sản phẩm ${props.product.id} vào giỏ:`, error);
    // Hiển thị thông báo lỗi cho người dùng nếu cần.
    // Ví dụ: toast.error('Thêm vào giỏ thất bại. Vui lòng thử lại.');
  } finally {
    // Dùng setTimeout để đảm bảo người dùng nhìn thấy spinner một chút, ngay cả khi mạng rất nhanh.
    // Giúp cải thiện cảm nhận về mặt UX.
    setTimeout(() => {
      isAdding.value = false; // Kết thúc trạng thái loading
    }, 300); // Thời gian chờ ngắn (300ms)
  }
};
</script>

<style scoped>
/* --- CSS Styles --- */
/* Giữ nguyên các style như đã cung cấp, chúng khá tốt và đầy đủ */
.product-card {
  border: var(--border-width, 1px) solid var(--color-border, #dee2e6);
  border-radius: var(--border-radius-lg, 0.5rem);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background-color: var(--color-surface, #fff);
  overflow: hidden; /* Đảm bảo các hiệu ứng không tràn ra ngoài */
}

.product-card:hover {
  transform: translateY(-6px); /* Hiệu ứng nhấc lên khi hover */
  box-shadow: var(--box-shadow-lg, 0 1rem 3rem rgba(0,0,0,.175)); /* Đổ bóng lớn hơn */
}

.product-link {
  display: block; /* Đảm bảo link chiếm toàn bộ card */
  color: inherit; /* Kế thừa màu chữ */
  text-decoration: none; /* Bỏ gạch chân */
}
.product-link:hover {
  color: inherit; /* Giữ màu chữ khi hover link */
}

.product-image-container {
  position: relative; /* Làm gốc cho các phần tử absolute bên trong (button, overlay) */
  overflow: hidden; /* Đảm bảo ảnh phóng to không tràn */
}

.product-image {
  height: 250px; /* Chiều cao cố định cho ảnh */
  width: 100%;
  object-fit: cover; /* Cắt ảnh để vừa khung, giữ tỷ lệ */
  border-bottom: var(--border-width, 1px) solid var(--color-border, #dee2e6);
  transition: transform 0.35s ease; /* Hiệu ứng phóng to ảnh */
  display: block; /* Loại bỏ khoảng trắng thừa dưới ảnh */
}
.product-card:hover .product-image {
  transform: scale(1.05); /* Phóng to ảnh khi hover card */
}

.card-body {
  padding: 1rem;
  display: flex;
  flex-direction: column; /* Sắp xếp tên và giá theo chiều dọc */
  flex-grow: 1; /* Đảm bảo card-body chiếm hết không gian còn lại */
}

.product-name {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-heading, #343a40);
  margin-bottom: 0.5rem;
  line-height: 1.4;
  /* Giới hạn tên sản phẩm trong 2 dòng */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 2.8em; /* Đảm bảo chiều cao tối thiểu cho 2 dòng */
  flex-grow: 1; /* Đẩy giá xuống dưới cùng */
  transition: color 0.2s ease;
}
.product-link:hover .product-name {
  color: var(--color-primary, #6d4c41); /* Đổi màu tên khi hover */
}

.product-price {
  color: var(--color-danger, #dc3545); /* Màu giá nổi bật */
  font-size: 1.15rem;
  font-weight: 700;
  margin-top: auto; /* Đẩy giá xuống dưới cùng card-body */
  margin-bottom: 0; /* Reset margin mặc định */
}

/* === Kiểu nút Thêm vào giỏ hàng === */
.btn-add-to-cart {
  position: absolute;
  bottom: 15px; /* Vị trí từ đáy container ảnh */
  left: 50%; /* Căn giữa theo chiều ngang */
  transform: translateX(-50%) translateY(15px); /* Dịch lên trên khi hover */
  opacity: 0; /* Ẩn ban đầu */
  visibility: hidden; /* Ẩn hoàn toàn ban đầu */
  background-color: var(--color-accent, #ffc107); /* Màu nền nút */
  color: var(--color-dark, #212529); /* Màu icon/spinner */
  border: none;
  border-radius: 50%; /* Bo tròn */
  width: 44px; /* Kích thước nút */
  height: 44px;
  display: inline-flex; /* Sử dụng flex để căn giữa nội dung */
  align-items: center;
  justify-content: center;
  box-shadow: var(--box-shadow-md, 0 .5rem 1rem rgba(0,0,0,.15));
  transition: opacity 0.3s ease, transform 0.3s ease, background-color 0.2s ease, visibility 0.3s ease;
  z-index: 10; /* Nằm trên ảnh */
  cursor: pointer;
}

/* Hiển thị nút khi hover card */
.product-card:hover .btn-add-to-cart {
  opacity: 1;
  transform: translateX(-50%) translateY(0); /* Di chuyển về vị trí cuối cùng */
  visibility: visible;
}

/* Hiệu ứng hover cho nút (khi không bị disable) */
.btn-add-to-cart:hover:not(:disabled) {
  background-color: var(--color-accent-hover, #e0a800); /* Đổi màu nền */
  box-shadow: var(--box-shadow-lg, 0 1rem 3rem rgba(0,0,0,.175)); /* Tăng bóng đổ */
}

/* Kiểu nút khi bị disable (đang thêm hàng) */
.btn-add-to-cart:disabled {
  background-color: var(--color-text-muted, #6c757d);
  cursor: not-allowed; /* Con trỏ không cho phép */
  opacity: 0.7 !important; /* Giảm độ mờ nhưng vẫn thấy spinner */
  /* Giữ lại transform để không bị giật layout */
  transform: translateX(-50%) translateY(0);
}

/* Kích thước icon trong nút */
.btn-add-to-cart .cart-icon-img {
  width: 24px;
  height: 24px;
  vertical-align: middle; /* Căn giữa theo chiều dọc */
}

/* Kích thước spinner trong nút */
.btn-add-to-cart .spinner-border-sm {
  width: 1.2rem;
  height: 1.2rem;
  color: inherit; /* Kế thừa màu từ nút */
}

/* === Kiểu Thông báo Hết hàng === */
.out-of-stock-overlay {
  position: absolute;
  bottom: 15px; /* Cùng vị trí với nút add-to-cart */
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.6); /* Nền đen mờ */
  color: white;
  padding: 5px 12px;
  border-radius: var(--border-radius-sm, 0.25rem);
  font-size: 0.8rem;
  font-weight: 500;
  text-align: center;
  z-index: 11; /* Nằm trên cả nút add-to-cart (nếu có) */
  pointer-events: none; /* Không bắt sự kiện chuột */
  opacity: 0; /* Ẩn ban đầu */
  visibility: hidden;
  transition: opacity 0.3s ease, visibility 0.3s ease;
}

/* Hiển thị overlay khi hover card (nếu điều kiện v-else-if đúng) */
.product-card:hover .out-of-stock-overlay {
  opacity: 1;
  visibility: visible;
}
</style>
