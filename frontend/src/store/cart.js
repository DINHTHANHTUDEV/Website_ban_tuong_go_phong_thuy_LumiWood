import { defineStore } from "pinia";
import {
  getCart,
  addItemToCart,
  updateCartItemQuantity,
  removeCartItem,
  clearServerCart,
  mergeGuestCart,
} from "@/http/modules/public/cartService.js";
import { useAuthStore } from "./auth";
import { getCartSessionId, clearCartSessionId } from "@/utils/cartSession";

export const useCartStore = defineStore("cart", {
  state: () => ({
    items: [],
    cartId: null,
    isLoading: false,
    error: null,
  }),

  getters: {
    totalUniqueItems: (state) => state.items.length,
    totalItemsCount: (state) => state.items.reduce((sum, item) => sum + (item.quantity || 0), 0),
    subtotal: (state) =>
      state.items.reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 0), 0),
    findItem: (state) => (productId) => state.items.find((item) => item.productId === productId),
    isEmpty: (state) => state.items.length === 0 && !state.isLoading,
    isTrulyEmpty: (state) => state.items.length === 0 && !state.isLoading && !state.error,
  },

  actions: {
    async fetchCart() {
      if (this.isLoading) return;

      this.isLoading = true;
      this.error = null;
      try {
        const response = await getCart();
        this.items = response.data?.items || [];
        this.cartId = response.data?.id || response.data?.cartId || null;
      } catch (err) {
        if (err.response?.status !== 404) {
          this.error = err.response?.data?.message || err.message || "Không thể tải giỏ hàng.";
        }
        this.items = [];
        this.cartId = null;
      } finally {
        this.isLoading = false;
      }
    },

    async addItem(productId, quantity = 1) {
      if (!productId || quantity <= 0) return Promise.reject(new Error("Dữ liệu không hợp lệ"));
      this.isLoading = true;
      this.error = null;
      try {
        const response = await addItemToCart({ productId, quantity });
        this.items = response.data?.items || [];
        this.cartId = response.data?.id || response.data?.cartId || null;
      } catch (err) {
        this.error = err.response?.data?.message || "Lỗi khi thêm sản phẩm vào giỏ.";
        throw err;
      } finally {
        this.isLoading = false;
      }
    },

    async updateQuantity(productId, quantity) {
      if (!productId) return Promise.reject(new Error("Thiếu ID sản phẩm"));
      if (quantity <= 0) {
        return this.removeItem(productId);
      }

      this.isLoading = true;
      this.error = null;
      try {
        const response = await updateCartItemQuantity(productId, quantity);
        this.items = response.data?.items || [];
        this.cartId = response.data?.id || response.data?.cartId || null;
      } catch (err) {
        this.error = err.response?.data?.message || "Lỗi khi cập nhật số lượng.";
        await this.fetchCart();
      } finally {
        this.isLoading = false;
      }
    },

    async removeItem(productId) {
      if (!productId) return Promise.reject(new Error("Thiếu ID sản phẩm"));
      this.isLoading = true;
      this.error = null;
      try {
        const response = await removeCartItem(productId);
        this.items = response.data?.items || [];
        this.cartId = response.data?.id || response.data?.cartId || null;
      } catch (err) {
        this.error = err.response?.data?.message || "Lỗi khi xóa sản phẩm khỏi giỏ.";
        await this.fetchCart();
      } finally {
        this.isLoading = false;
      }
    },

    clearClientCart() {
      this.items = [];
      this.cartId = null;
      this.error = null;
    },

    async clearCart() {
      if (!this.cartId && this.items.length === 0) {
        this.clearClientCart();
        return;
      }

      this.isLoading = true;
      this.error = null;
      try {
        if (this.cartId) {
          await clearServerCart();
        }
        this.clearClientCart();
      } catch (err) {
        this.error = "Lỗi khi xóa giỏ hàng trên máy chủ.";
      } finally {
        this.isLoading = false;
      }
    },

    async handleLoginMerge() {
      const authStore = useAuthStore();
      if (!authStore.isAuthenticated) return;
      const guestSessionId = getCartSessionId();
      if (!guestSessionId) {
        await this.fetchCart();
        return;
      }
      this.isLoading = true;
      this.error = null;
      try {
        const response = await mergeGuestCart(guestSessionId);
        this.items = response.data?.items || [];
        this.cartId = response.data?.id || response.data?.cartId || null;
        clearCartSessionId();
      } catch (err) {
        this.error = "Lỗi khi gộp giỏ hàng từ phiên khách.";
        await this.fetchCart();
      } finally {
        this.isLoading = false;
      }
    },

    handleLogoutCleanup() {
      this.clearClientCart();
    },
  },
});
