// //  gọi API backend ở đây
// const mockApiClient = () => Promise.resolve({ data: {} });
// const apiClient = {
//   get: mockApiClient,
//   post: mockApiClient,
//   put: mockApiClient,
//   delete: mockApiClient,
//   patch: mockApiClient,
//   defaults: { headers: { common: {} } }
// };
// export default apiClient;
// axios.js – Tạo và cấu hình axios instance dùng chung cho toàn bộ hệ thống

import axios from 'axios';
import { useAuthStore } from '@/store/auth';
import { getCartSessionId } from '@/utils/cartSession';

// Khởi tạo Axios với baseURL từ biến môi trường (KHÔNG kèm /api)
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  }
});

// Interceptor yêu cầu: Gắn Authorization token nếu có
apiClient.interceptors.request.use(
  config => {
    let authStore;
    try {
      authStore = useAuthStore(); // Dùng Pinia store
    } catch (error) {
      return config;
    }

    if (authStore.isAuthenticated && authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`;
      delete config.headers['X-Cart-Session-Id'];
    } else {
      const sessionId = getCartSessionId();
      if (sessionId) {
        config.headers['X-Cart-Session-Id'] = sessionId;
      }
      delete config.headers['Authorization'];
    }

    return config;
  },
  error => Promise.reject(error)
);

// Interceptor phản hồi: Tự động logout nếu token hết hạn (401)
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const originalRequest = error.config;
      const status = error.response.status;

      if (status === 401 && !originalRequest._retry) {
        let authStore;
        try {
          authStore = useAuthStore();
        } catch (storeError) {
          return Promise.reject(error);
        }

        if (authStore.isAuthenticated) {
          authStore.logout();
          if (typeof router !== 'undefined' && router) {
            router.push({ name: 'login', query: { sessionExpired: 'true' } }).catch(() => {});
          }
        }
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;

