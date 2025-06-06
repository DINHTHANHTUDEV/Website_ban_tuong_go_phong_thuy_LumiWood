<template>
  <div class="login-view d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="card shadow-sm" style="width: 100%; max-width: 400px;">
      <div class="card-body p-4">
        <h2 class="card-title text-center mb-4">Đăng Nhập</h2>
        <form @submit.prevent="handleLogin">

          <div v-if="authStore.loginError" class="alert alert-danger" role="alert">
            {{ authStore.loginError }}
          </div>

          <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập</label>
            <input type="text" class="form-control" id="username" v-model="username" required
              placeholder="Nhập tên đăng nhập" :disabled="authStore.loading" />
          </div>

          <div class="mb-3">
            <label for="password" class="form-label">Mật khẩu</label>
            <input type="password" class="form-control" id="password" v-model="password" required
              placeholder="Nhập mật khẩu" :disabled="authStore.loading" />
          </div>

          <div class="d-grid">
            <button type="submit" class="btn btn-primary btn-lg" :disabled="authStore.loading">
              <span v-if="authStore.loading" class="spinner-border spinner-border-sm me-2" role="status"
                aria-hidden="true"></span>
              {{ authStore.loading ? 'Đang xử lý...' : 'Đăng nhập' }}
            </button>
          </div>
        </form>

        <div class="text-center mt-3">
          Chưa có tài khoản?
          <router-link :to="{ name: 'register' }">Đăng ký ngay</router-link>
        </div>
        <div class="text-center mt-2">
          <router-link :to="{ name: 'home' }">Quay lại trang chủ</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth'; // ✅ Gọi đúng store vừa sửa
import { useCartStore } from '@/store/cart'; // Nếu có phần giỏ hàng

const username = ref('');
const password = ref('');
const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();

const handleLogin = async () => {
  const success = await authStore.login(username.value, password.value);

  if (success) {
    // ✅ Gộp giỏ hàng nếu có giỏ hàng tạm
    try {
      await cartStore.handleLoginMerge?.(); // dùng optional chaining để tránh lỗi nếu không có
    } catch (error) {
      console.error('Lỗi gộp giỏ hàng:', error);
    }

    // ✅ Điều hướng sau khi login
    const redirectPath = authStore.returnUrl;
    authStore.setReturnUrl(null);

    const finalRedirect = redirectPath || (authStore.isAdmin
      ? { name: 'adminDashboard' }
      : { name: 'home' });

    try {
      await router.push(finalRedirect);
    } catch (err) {
      console.error('Lỗi điều hướng:', err);
    }
  }
};
</script>

<style scoped>
.login-view {
  background: url('https://goldviet24k.vn/pic/Product/images/c%C3%B3c%20ng%E1%BA%ADm%20ti%E1%BB%81n%20v%C3%A0ng%20%C4%91%E1%BB%93ng%201d.jpg') no-repeat center center;
  background-size: cover;
}
</style>
