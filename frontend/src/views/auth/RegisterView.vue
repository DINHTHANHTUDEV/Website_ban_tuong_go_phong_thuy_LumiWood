<!-- <template>
  <div class="register-view d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="card shadow-sm" style="width: 100%; max-width: 450px;">
      <div class="card-body p-4 p-lg-5">
        <h2 class="card-title text-center mb-4">Đăng Ký Tài Khoản</h2>
        <form @submit.prevent="handleRegister" novalidate>

          <div v-if="error" class="alert alert-danger" role="alert">
            {{ error }}
          </div>

          <div v-if="successMessage" class="alert alert-success" role="alert">
            {{ successMessage }} Đang chuyển hướng đến trang đăng nhập...
          </div>

          <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập <span class="text-danger">*</span></label>
            <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.username }" id="username"
              v-model.trim="formData.username" required placeholder="Ít nhất 3 ký tự"
              :disabled="authStore.loading || !!successMessage" aria-describedby="usernameFeedback" />
            <div id="usernameFeedback" v-if="validationErrors.username" class="invalid-feedback">
              {{ validationErrors.username }}
            </div>
          </div>

          <div class="mb-3">
            <label for="fullName" class="form-label">Họ và Tên</label>
            <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.fullName }" id="fullName"
              v-model.trim="formData.fullName" required placeholder="Ví dụ: Nguyễn Văn A"
              :disabled="authStore.loading || !!successMessage" aria-describedby="fullNameFeedback" />
            <div id="fullNameFeedback" v-if="validationErrors.fullName" class="invalid-feedback">
              {{ validationErrors.fullName }}
            </div>
          </div>

          <div class="mb-3">
            <label for="password" class="form-label">Mật khẩu <span class="text-danger">*</span></label>
            <input type="password" class="form-control" :class="{ 'is-invalid': validationErrors.password }"
              id="password" v-model="formData.password" required placeholder="Ít nhất 6 ký tự"
              :disabled="authStore.loading || !!successMessage" aria-describedby="passwordFeedback" />
            <div id="passwordFeedback" v-if="validationErrors.password" class="invalid-feedback">
              {{ validationErrors.password }}
            </div>
          </div>

          <div class="mb-3">
            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
            <input type="password" class="form-control" :class="{ 'is-invalid': validationErrors.confirmPassword }"
              id="confirmPassword" v-model="formData.confirmPassword" required placeholder="Nhập lại mật khẩu"
              :disabled="authStore.loading || !!successMessage" aria-describedby="confirmPasswordFeedback" />
            <div id="confirmPasswordFeedback" v-if="validationErrors.confirmPassword" class="invalid-feedback">
              {{ validationErrors.confirmPassword }}
            </div>
          </div>

          <div class="d-grid">
            <button type="submit" class="btn btn-primary btn-lg" :disabled="authStore.loading || !!successMessage">
              <span v-if="authStore.loading" class="spinner-border spinner-border-sm me-2" role="status"
                aria-hidden="true"></span>
              {{ authStore.loading ? 'Đang xử lý...' : 'Đăng ký' }}
            </button>
          </div>
        </form>

        <div class="text-center mt-3 small">
          Bạn đã có tài khoản? <RouterLink :to="{ name: 'login' }">Đăng nhập</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const authStore = useAuthStore();
const router = useRouter();

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  fullName: ''
});

const validationErrors = reactive({});
const error = ref(null);
const successMessage = ref(null);

const validateForm = () => {
  Object.keys(validationErrors).forEach(k => delete validationErrors[k]);
  let isValid = true;

  if (!formData.username || formData.username.length < 3 || formData.username.length > 50) {
    validationErrors.username = 'Tên đăng nhập phải từ 3 đến 50 ký tự.';
    isValid = false;
  } else if (/\s/.test(formData.username)) {
    validationErrors.username = 'Tên đăng nhập không được chứa khoảng trắng.';
    isValid = false;
  }

  if (!formData.fullName || formData.fullName.trim() === '') {
    validationErrors.fullName = 'Họ tên không được để trống.';
    isValid = false;
  }
  else if (formData.fullName && formData.fullName.length > 150) {
    validationErrors.fullName = 'Họ tên không được quá 150 ký tự.';
    isValid = false;
  }

  if (!formData.password || formData.password.length < 6 || formData.password.length > 100) {
    validationErrors.password = 'Mật khẩu phải từ 6 đến 100 ký tự.';
    isValid = false;
  }

  if (!formData.confirmPassword) {
    validationErrors.confirmPassword = 'Vui lòng xác nhận mật khẩu.';
    isValid = false;
  } else if (formData.password !== formData.confirmPassword) {
    validationErrors.confirmPassword = 'Mật khẩu xác nhận không khớp.';
    isValid = false;
  }

  return isValid;
};

const handleRegister = async () => {
  error.value = null;
  successMessage.value = null;

  if (!validateForm()) {
    return;
  }

  try {
    const payload = {
      username: formData.username,
      password: formData.password,
      confirmPassword: formData.confirmPassword,
      fullName: formData.fullName || ''
    };

    const res = await authStore.register(payload);
    successMessage.value = res || 'Đăng ký thành công!';

    Object.keys(formData).forEach(key => (formData[key] = ''));
    setTimeout(() => router.push({ name: 'login' }), 2000);
  } catch (errMsg) {
    error.value = errMsg;
  }
};
</script>

<style scoped>
.register-view {
  background: url('https://goldviet24k.vn/pic/Product/images/c%C3%B3c%20ng%E1%BA%ADm%20ti%E1%BB%81n%20v%C3%A0ng%20%C4%91%E1%BB%93ng%201d.jpg') no-repeat center center;
  background-size: cover;
}
</style> -->
<template>
  <div class="register-view d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="card shadow-sm" style="width: 100%; max-width: 450px;">
      <div class="card-body p-4 p-lg-5">
        <h2 class="card-title text-center mb-4">Đăng Ký Tài Khoản</h2>

        <!-- Form Đăng ký ban đầu -->
        <form v-if="step === 1" @submit.prevent="handleSendOtp" novalidate>
          <div v-if="error" class="alert alert-danger" role="alert">
            {{ error }}
          </div>
          <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập <span class="text-danger">*</span></label>
            <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.username }" id="username"
              v-model.trim="formData.username" required placeholder="Ít nhất 3 ký tự" :disabled="loading" />
            <div v-if="validationErrors.username" class="invalid-feedback">
              {{ validationErrors.username }}
            </div>
          </div>
          <div class="mb-3">
            <label for="fullName" class="form-label">Họ và Tên</label>
            <input type="text" class="form-control" :class="{ 'is-invalid': validationErrors.fullName }" id="fullName"
              v-model.trim="formData.fullName" required placeholder="Ví dụ: Nguyễn Văn A" :disabled="loading" />
            <div v-if="validationErrors.fullName" class="invalid-feedback">
              {{ validationErrors.fullName }}
            </div>
          </div>
          <div class="mb-3">
            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
            <input type="email" class="form-control" :class="{ 'is-invalid': validationErrors.email }" id="email"
              v-model.trim="formData.email" required placeholder="Nhập địa chỉ email" :disabled="loading" />
            <div v-if="validationErrors.email" class="invalid-feedback">
              {{ validationErrors.email }}
            </div>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Mật khẩu <span class="text-danger">*</span></label>
            <input type="password" class="form-control" :class="{ 'is-invalid': validationErrors.password }"
              id="password" v-model="formData.password" required placeholder="Ít nhất 6 ký tự" :disabled="loading" />
            <div v-if="validationErrors.password" class="invalid-feedback">
              {{ validationErrors.password }}
            </div>
          </div>
          <div class="mb-3">
            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
            <input type="password" class="form-control" :class="{ 'is-invalid': validationErrors.confirmPassword }"
              id="confirmPassword" v-model="formData.confirmPassword" required placeholder="Nhập lại mật khẩu"
              :disabled="loading" />
            <div v-if="validationErrors.confirmPassword" class="invalid-feedback">
              {{ validationErrors.confirmPassword }}
            </div>
          </div>
          <div class="d-grid">
            <button type="submit" class="btn btn-primary btn-lg" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ loading ? 'Đang gửi mã OTP...' : 'Đăng ký' }}
            </button>
          </div>
        </form>

        <!-- Form nhập OTP -->
        <div v-else-if="step === 2">
          <div class="alert alert-info">Nhập mã OTP đã gửi về email: <strong>{{ formData.email }}</strong></div>
          <div v-if="otpError" class="alert alert-danger small" role="alert">{{ otpError }}</div>
          <div class="mb-3">
            <input v-model="otp" maxlength="6" type="text" class="form-control" placeholder="Nhập mã OTP"
              :disabled="verifying" @keyup.enter="handleVerifyOtp" />
          </div>
          <div class="d-flex align-items-center justify-content-between mb-3">
            <button class="btn btn-outline-secondary btn-sm" :disabled="countdown > 0 || verifying"
              @click="handleResendOtp">
              {{ countdown > 0 ? `Lấy mã mới (${countdown}s)` : 'Lấy mã mới' }}
            </button>
            <button class="btn btn-primary btn-sm" :disabled="verifying" @click="handleVerifyOtp">
              <span v-if="verifying" class="spinner-border spinner-border-sm me-1"></span>
              Đăng ký
            </button>
          </div>
          <div v-if="otpInfo" class="alert alert-success small" role="alert">{{ otpInfo }}</div>
        </div>

        <!-- Success -->
        <div v-else-if="step === 3">
          <div class="alert alert-success text-center">
            {{ successMessage }} <br />Đang chuyển hướng đến trang đăng nhập...
          </div>
        </div>

        <div class="text-center mt-3 small" v-if="step !== 2">
          Bạn đã có tài khoản? <RouterLink :to="{ name: 'login' }">Đăng nhập</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const authStore = useAuthStore();
const router = useRouter();

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  fullName: '',
  email: ''
});
const validationErrors = reactive({});
const error = ref(null);

const step = ref(1); // 1: đăng ký, 2: nhập OTP, 3: thành công
const loading = ref(false);

const otp = ref('');
const verifying = ref(false);
const otpError = ref(null);
const otpInfo = ref('');
const countdown = ref(0);
let timer = null;

const successMessage = ref(null);

function validateForm() {
  Object.keys(validationErrors).forEach(k => delete validationErrors[k]);
  let isValid = true;
  if (!formData.username || formData.username.length < 3) {
    validationErrors.username = 'Tên đăng nhập phải từ 3 ký tự.';
    isValid = false;
  }
  if (!formData.fullName) {
    validationErrors.fullName = 'Họ tên không được để trống.';
    isValid = false;
  }
  if (!formData.email) {
    validationErrors.email = 'Vui lòng nhập email.';
    isValid = false;
  } else if (!/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(formData.email)) {
    validationErrors.email = 'Email không hợp lệ.';
    isValid = false;
  }
  if (!formData.password || formData.password.length < 6) {
    validationErrors.password = 'Mật khẩu phải từ 6 ký tự.';
    isValid = false;
  }
  if (!formData.confirmPassword) {
    validationErrors.confirmPassword = 'Vui lòng xác nhận mật khẩu.';
    isValid = false;
  } else if (formData.password !== formData.confirmPassword) {
    validationErrors.confirmPassword = 'Mật khẩu xác nhận không khớp.';
    isValid = false;
  }
  return isValid;
}

async function handleSendOtp() {
  error.value = null;
  if (!validateForm()) return;
  loading.value = true;
  try {
    await authStore.sendOtp({ ...formData });
    step.value = 2;
    otpError.value = '';
    otpInfo.value = '';
    startCountdown();
  } catch (err) {
    error.value = err?.message || err || 'Đã xảy ra lỗi!';
  } finally {
    loading.value = false;
  }
}

function startCountdown() {
  countdown.value = 60;
  if (timer) clearInterval(timer);
  timer = setInterval(() => {
    if (countdown.value > 0) countdown.value--;
    else clearInterval(timer);
  }, 1000);
}

async function handleResendOtp() {
  otpError.value = '';
  otpInfo.value = '';
  try {
    await authStore.sendOtp({ ...formData });
    otpInfo.value = 'Mã OTP mới đã được gửi.';
    startCountdown();
  } catch (err) {
    otpError.value = err?.message || err || 'Không gửi lại được OTP!';
  }
}

async function handleVerifyOtp() {
  otpError.value = '';
  verifying.value = true;
  try {
    await authStore.verifyOtp({ email: formData.email, otp: otp.value });
    step.value = 3;
    successMessage.value = 'Đăng ký thành công!';
    setTimeout(() => router.push({ name: 'login' }), 1800);
  } catch (err) {
    otpError.value = err?.message || err || 'Mã OTP không đúng hoặc đã hết hạn!';
  } finally {
    verifying.value = false;
  }
}
</script>

<style scoped>
.register-view {
  background: url('https://goldviet24k.vn/pic/Product/images/c%C3%B3c%20ng%E1%BA%ADm%20ti%E1%BB%81n%20v%C3%A0ng%20%C4%91%E1%BB%93ng%201d.jpg') no-repeat center center;
  background-size: cover;
}
</style>

