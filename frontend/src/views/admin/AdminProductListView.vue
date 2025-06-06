<template>
  <div class="admin-product-list-view container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Quản lý Sản phẩm</h1>
      <router-link :to="{ name: 'adminProductNew' }" class="btn btn-primary">
        <i class="bi bi-plus-lg"></i> Thêm Sản phẩm mới
      </router-link>
    </div>

    
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <form @submit.prevent="applyTableFilters">
          <div class="row g-3 align-items-end">
            
            <div class="col-md-4">
              <label for="filterKeyword" class="form-label">Tìm kiếm</label>
              <input
                type="text"
                class="form-control form-control-sm"
                id="filterKeyword"
                v-model="filters.keyword"
                placeholder="Tên, slug, mô tả..."
              />
            </div>
            
            <div class="col-md-3">
              <label for="filterCategory" class="form-label">Danh mục</label>
              <select
                class="form-select form-select-sm"
                id="filterCategory"
                v-model="filters.categoryId"
                :disabled="loadingCategories"
              >
                <option :value="null">-- Tất cả danh mục --</option>
                <option v-if="loadingCategories" disabled>Đang tải...</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
              <div v-if="categoryError" class="text-danger small mt-1">{{ categoryError }}</div>
            </div>
            
            <div class="col-md-3">
              <label for="filterStatus" class="form-label">Trạng thái</label>
              <select
                class="form-select form-select-sm"
                id="filterStatus"
                v-model="filters.isActive"
              >
                <option :value="null">-- Tất cả trạng thái --</option>
                <option :value="true">Đang bán</option>
                <option :value="false">Ngừng bán</option>
              </select>
            </div>
            
            <div class="col-md-2 d-flex gap-2">
              <button type="submit" class="btn btn-primary btn-sm flex-grow-1">Lọc</button>
              <button
                type="button"
                class="btn btn-outline-secondary btn-sm flex-grow-1"
                @click="resetFilters"
                title="Xóa bộ lọc"
              >
                Xóa lọc
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    
    <div v-if="loading" class="text-center my-5 py-5">
      <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem">
        <span class="visually-hidden">Đang tải sản phẩm...</span>
      </div>
    </div>

    
    <div v-else-if="error" class="alert alert-danger">Lỗi tải danh sách sản phẩm: {{ error }}</div>

    
    <div v-else-if="products.length > 0" class="table-responsive card shadow-sm">
      <table class="table table-hover table-striped mb-0 align-middle">
        <thead class="table-light">
          <tr>
            <th scope="col" style="width: 60px">Ảnh</th>
            <th scope="col">Tên Sản phẩm</th>
            <th scope="col">Danh mục</th>
            <th scope="col" class="text-end">Giá</th>
            <th scope="col" class="text-center">Tồn kho</th>
            <th scope="col" class="text-center">Trạng thái</th>
            <th scope="col" class="text-center" style="width: 120px">Hành Động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>
              <img
                :src="product.imageUrl || defaultImage"
                :alt="product.name"
                width="50"
                height="50"
                style="object-fit: cover"
                class="rounded border"
                @error="onImageError"
              />
            </td>
            <td>
              <router-link
                :to="{ name: 'adminProductEdit', params: { productId: product.id } }"
                class="fw-medium text-decoration-none link-dark"
              >
                {{ product.name }}
              </router-link>
              <small v-if="product.slug" class="d-block text-muted">Slug: {{ product.slug }}</small>
            </td>
            <td>{{ product.category?.name || "N/A" }}</td>
            <td class="text-end">{{ formatCurrency(product.price) }}</td>
            <td class="text-center">{{ product.stock ?? "N/A" }}</td>
            <td class="text-center">
              <span
                class="badge rounded-pill"
                :class="product.isActive ? 'text-bg-success' : 'text-bg-secondary'"
              >
                {{ product.isActive ? "Đang bán" : "Ngừng bán" }}
              </span>
            </td>
            <td class="text-center">
              <router-link
                :to="{ name: 'adminProductEdit', params: { productId: product.id } }"
                class="btn btn-sm btn-outline-secondary me-1"
                title="Sửa"
              >
                <i class="bi bi-pencil-square"></i>
              </router-link>
              <button
                class="btn btn-sm"
                :class="product.isActive ? 'btn-outline-warning' : 'btn-outline-success'"
                :title="product.isActive ? 'Ẩn sản phẩm' : 'Hiện sản phẩm'"
                @click="confirmToggleStatus(product)"
                :disabled="togglingStatusId === product.id"
              >
                <span
                  v-if="togglingStatusId === product.id"
                  class="spinner-border spinner-border-sm"
                  role="status"
                  aria-hidden="true"
                ></span>
                <i v-else :class="product.isActive ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="card-footer bg-light border-top-0" v-if="totalPages > 1">
        <BasePagination
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="handlePageChange"
          class="mt-3 d-flex justify-content-center mb-0"
        />
      </div>
    </div>

    
    <div v-else class="alert alert-info text-center py-5">
      Chưa có sản phẩm nào khớp với bộ lọc của bạn.
      <span v-if="!filters.keyword && !filters.categoryId && filters.isActive === null">
        <router-link :to="{ name: 'adminProductNew' }" class="btn btn-link p-0 ms-1"
          >Thêm sản phẩm mới</router-link
        >.
      </span>
      <span v-else>
        <button class="btn btn-link p-0 ms-1" @click="resetFilters">Xóa bộ lọc</button> để xem tất
        cả.
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRouter, useRoute, RouterLink } from "vue-router";
import {
  getAllProductsAdmin,
  toggleAdminProductStatus,
} from "@/http/modules/admin/adminProductService.js";
import BasePagination from "@/components/common/BasePagination.vue";
import { getAllCategories } from "@/http/modules/public/categoryService.js";
import defaultImage from "@/assets/images/placeholder.png";
import { formatCurrency } from "@/utils/formatters";

const router = useRouter();
const route = useRoute();

const products = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const itemsPerPage = ref(10);
const togglingStatusId = ref(null);


const filters = reactive({
  keyword: route.query.keyword || "",
  categoryId: route.query.categoryId || null,

  isActive: route.query.isActive !== undefined ? route.query.isActive === "true" : null,
});


const categories = ref([]);
const loadingCategories = ref(false);
const categoryError = ref(null);


const fetchCategoriesForFilter = async () => {
  loadingCategories.value = true;
  categoryError.value = null;
  try {
    const response = await getAllCategories();
    categories.value = response.data || [];
  } catch (err) {
    console.error("Error fetching categories for filter:", err);
    categoryError.value = "Lỗi tải danh mục.";
    categories.value = [];
  } finally {
    loadingCategories.value = false;
  }
};


const applyTableFilters = () => {
  const query = { ...route.query, page: 0 };
  if (filters.keyword) query.keyword = filters.keyword;
  else delete query.keyword;
  if (filters.categoryId) query.categoryId = filters.categoryId;
  else delete query.categoryId;

  if (filters.isActive !== null) query.isActive = String(filters.isActive);
  else delete query.isActive;



  router.push({ query });
};


const resetFilters = () => {
  filters.keyword = "";
  filters.categoryId = null;
  filters.isActive = null;

  const query = { ...route.query };
  delete query.keyword;
  delete query.categoryId;
  delete query.isActive;

  query.page = 0;
  router.push({ query });
};


const fetchProducts = async (page = 0) => {
  loading.value = true;
  error.value = null;
  try {
    const params = {
      page,
      size: itemsPerPage.value,
      sort: route.query.sort || "createdAt,desc",

      ...(filters.keyword && { keyword: filters.keyword }),
      ...(filters.categoryId && { categoryId: filters.categoryId }),

      ...(filters.isActive !== null && { isActive: filters.isActive }),
    };

    Object.keys(params).forEach(
      (key) => (params[key] === null || params[key] === undefined) && delete params[key]
    );

    const response = await getAllProductsAdmin(params);
    products.value = response.data.content || [];
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (err) {
    console.error("Error fetching admin products:", err);
    error.value = err.response?.data?.message || "Không thể tải danh sách sản phẩm.";
    products.value = [];
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (newPage) => {

  router.push({ query: { ...route.query, page: newPage } });
};

const confirmToggleStatus = async (product) => {
  if (togglingStatusId.value) return;
  const newStatus = !product.isActive;
  const actionText = newStatus ? "hiển thị (bán lại)" : "ẩn (ngừng bán)";
  if (!confirm(`Bạn có chắc muốn ${actionText} sản phẩm "${product.name}"?`)) {
    return;
  }

  togglingStatusId.value = product.id;
  try {
    const response = await toggleAdminProductStatus(product.id, newStatus);
    const updatedProduct = response.data;

    const productIndex = products.value.findIndex((p) => p.id === updatedProduct.id);
    if (productIndex !== -1) {

      products.value[productIndex] = { ...products.value[productIndex], ...updatedProduct };
    } else {

      console.warn(`Toggled product ${product.id} not found in current list.`);


    }


  } catch (err) {
    console.error(`Error toggling status for product ${product.id}:`, err);
    alert(`Lỗi khi ${actionText} sản phẩm: ${err.response?.data?.message || err.message}`);

  } finally {
    togglingStatusId.value = null;
  }
};

const onImageError = (event) => {
  event.target.src = defaultImage;
};


watch(
  () => route.query,
  (newQuery, oldQuery) => {

    filters.keyword = newQuery.keyword || "";
    filters.categoryId = newQuery.categoryId || null;
    filters.isActive = newQuery.isActive !== undefined ? newQuery.isActive === "true" : null;
    fetchProducts(parseInt(newQuery.page || "0", 10));
  },
  { immediate: true, deep: true }
);

onMounted(() => {
  fetchCategoriesForFilter();

});
</script>

<style scoped>
.admin-product-list-view {
  min-height: 80vh;
}

.table th,
.table td {
  vertical-align: middle;
}

.link-dark {
  transition: color 0.2s ease;
}

.link-dark:hover {
  color: var(--bs-primary) !important;
}

.form-label {
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}


.form-control-sm,
.form-select-sm {
  font-size: 0.875rem;
}


.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}


</style>

