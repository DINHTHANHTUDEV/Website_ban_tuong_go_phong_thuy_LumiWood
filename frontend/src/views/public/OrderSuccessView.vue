
<template>
  <div class="order-success-view container mt-5 mb-5 text-center">
    <div class="card shadow-sm p-4 p-md-5 mx-auto" style="max-width: 600px;">
      <div class="checkmark-container mb-3">
        <svg class="checkmark" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
          <circle class="checkmark__circle" cx="26" cy="26" r="25" fill="none"/>
          <path class="checkmark__check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
        </svg>
      </div>

      <h1 class="h3 mb-3 text-success">Đặt Hàng Thành Công!</h1>

      <p class="lead">Cảm ơn bạn đã mua hàng tại Tượng Gỗ Phong Thủy!</p>

      <p v-if="orderId" class="mb-4">
        Mã đơn hàng của bạn là: <strong class="text-primary">#{{ orderId }}</strong>
      </p>
      <p v-else class="text-muted mb-4">Đơn hàng của bạn đang được xử lý.</p>

      
      

      <p>Chúng tôi sẽ liên hệ với bạn sớm nhất để xác nhận đơn hàng (nếu cần thiết) và tiến hành giao hàng.</p>
      <p>Bạn có thể kiểm tra email để xem chi tiết đơn hàng (nếu có).</p>

      <div class="mt-4 d-flex justify-content-center gap-3">
        <router-link :to="{ name: 'home' }" class="btn btn-outline-secondary">
          <i class="bi bi-house-door"></i> Về Trang Chủ
        </router-link>
        
        <router-link v-if="!isGuest" :to="{ name: 'home' }" class="btn btn-primary"> {}
          <i class="bi bi-receipt"></i> Xem Lịch Sử Đơn Hàng
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, RouterLink } from 'vue-router';
import { useAuthStore } from '@/store/auth.js';



const route = useRoute();
const authStore = useAuthStore();

const orderId = ref(route.params.orderId || null);
const isGuest = computed(() => !authStore.isAuthenticated);

































onMounted(() => {
  console.log("Order Success Page Mounted. Order ID:", orderId.value);


});
</script>

<style scoped>
.order-success-view {
  min-height: 70vh;
  display: flex;
  align-items: center; 
}

.checkmark-container {
  width: 100px;
  height: 100px;
  margin: 0 auto;
}

.checkmark__circle {
  stroke-dasharray: 166;
  stroke-dashoffset: 166;
  stroke-width: 3; 
  stroke-miterlimit: 10;
  stroke: #4CAF50; 
  fill: none;
  animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards;
}

.checkmark {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: block;
  stroke-width: 3; 
  stroke: #fff;
  stroke-miterlimit: 10;
  margin: 10% auto;
  box-shadow: inset 0px 0px 0px #4CAF50;
  animation: fill .4s ease-in-out .4s forwards, scale .3s ease-in-out .9s both;
}

.checkmark__check {
  transform-origin: 50% 50%;
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  stroke-width: 4; 
  animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.8s forwards;
}

@keyframes stroke {
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes scale {
  0%, 100% {
    transform: none;
  }
  50% {
    transform: scale3d(1.1, 1.1, 1);
  }
}

@keyframes fill {
  100% {
    box-shadow: inset 0px 0px 0px 50px #4CAF50; 
  }
}

.order-details-summary {
  background-color: #f8f9fa;
}
</style>

