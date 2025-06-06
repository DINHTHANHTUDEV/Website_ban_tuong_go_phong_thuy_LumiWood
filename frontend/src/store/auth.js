// // auth.js – Pinia store kết hợp gọi API đăng nhập / đăng ký backend

// import { defineStore } from 'pinia';
// import apiClient from '@/http/axios';

// export const useAuthStore = defineStore('auth', {
//   state: () => ({
//     user: JSON.parse(localStorage.getItem('user')) || null,
//     token: localStorage.getItem('token') || null,
//     loginError: null,
//     loading: false,
//     returnUrl: null
//   }),

//   getters: {
//     isAuthenticated: (state) => !!state.token,
//     isAdmin: (state) => state.user?.role === 'ROLE_ADMIN'
//   },

//   actions: {
//     /**
//      * Gọi API backend để đăng nhập
//      * Gắn token + user vào localStorage
//      */
//     async login(username, password) {
//       this.loading = true;
//       this.loginError = null;

//       try {
//         const response = await apiClient.post('/api/auth/login', {
//           username,
//           password
//         });

//         const { token, role } = response.data;

//         this.token = token;
//         this.user = { username, role };

//         localStorage.setItem('token', token);
//         localStorage.setItem('user', JSON.stringify(this.user));

//         return true;
//       } catch (error) {
//         this.loginError = error.response?.data || 'Đăng nhập thất bại';
//         return false;
//       } finally {
//         this.loading = false;
//       }
//     },

//     /**
//      * Gọi API backend để đăng ký người dùng mới
//      */
//     async register(registerData) {
//       try {
//         const response = await apiClient.post('/api/auth/register', registerData);
//         return response.data; // "Đăng ký thành công" hoặc lỗi
//       } catch (error) {
//         throw error.response?.data || 'Đăng ký thất bại';
//       }
//     },

//     /**
//      * Xoá token và user khỏi localStorage và state
//      */
//     logout() {
//       this.token = null;
//       this.user = null;
//       this.loginError = null;
//       this.returnUrl = null;

//       localStorage.removeItem('token');
//       localStorage.removeItem('user');
//     },

//     /**
//      * Lưu URL cần redirect sau khi đăng nhập
//      */
//     setReturnUrl(url) {
//       this.returnUrl = url;
//     }
//   }
// });
// auth.js – Pinia store dùng với các endpoint backend bạn đã làm

// auth.js – Pinia store kết hợp gọi API đăng nhập / đăng ký backend

import { defineStore } from 'pinia';
import apiClient from '@/http/axios';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    loginError: null,
    loading: false,
    returnUrl: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ROLE_ADMIN',
  },

  actions: {
    /**
     * Gọi API backend để đăng nhập
     * Gắn token + user vào localStorage
     */
    async login(username, password) {
      this.loading = true;
      this.loginError = null;

      try {
        const response = await apiClient.post('/api/auth/login', {
          username,
          password,
        });

        const { token, role } = response.data;

        this.token = token;
        this.user = { username, role };

        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(this.user));

        return true;
      } catch (error) {
        this.loginError = error.response?.data || 'Đăng nhập thất bại';
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * Gửi OTP tới email, đồng thời lưu thông tin đăng ký tạm thời ở backend
     * Chỉ gửi username, password, confirmPassword, fullName, email
     */
    async sendOtp(registerData) {
      this.loading = true;
      try {
        const response = await apiClient.post('/api/auth/send-otp', {
          username: registerData.username,
          password: registerData.password,
          confirmPassword: registerData.confirmPassword,
          fullName: registerData.fullName,
          email: registerData.email,
        });
        return response.data; // "OTP đã được gửi tới email: xxx"
      } catch (error) {
        throw error.response?.data || 'Gửi OTP thất bại';
      } finally {
        this.loading = false;
      }
    },

    /**
     * Xác thực OTP và đăng ký chính thức nếu OTP đúng
     * Gửi email, otp, và các dữ liệu đăng ký tạm thời đều có thể để trong body.
     */
    async verifyOtp({ email, otp }) {
      this.loading = true;
      try {
        const response = await apiClient.post('/api/auth/verify-otp', {
          email,
          otp,
        });
        return response.data; // "Đăng ký thành công!"
      } catch (error) {
        throw error.response?.data || 'Xác thực OTP hoặc đăng ký thất bại!';
      } finally {
        this.loading = false;
      }
    },

    /**
     * Xoá token và user khỏi localStorage và state
     */
    logout() {
      this.token = null;
      this.user = null;
      this.loginError = null;
      this.returnUrl = null;

      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },

    /**
     * Lưu URL cần redirect sau khi đăng nhập
     */
    setReturnUrl(url) {
      this.returnUrl = url;
    },
  },
});

