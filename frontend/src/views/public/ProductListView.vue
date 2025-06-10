<template>
  <div class="product-list-view container mt-4 mb-5">
    
    <div class="row mb-4 align-items-center">
      
      <div class="col-md-6 col-lg-8">
        <h1 class="h3 mb-0">{{ pageTitle }}</h1>
        
        <small v-if="!loading && totalElements !== null" class="text-muted d-block mt-1">
          Tìm thấy {{ totalElements }} sản phẩm
        </small>
      </div>

      
      <div class="col-md-6 col-lg-4 text-md-end mt-3 mt-md-0">
        
        <button
          class="btn btn-outline-secondary me-2 d-lg-none"
          type="button"
          data-bs-toggle="offcanvas"
          data-bs-target="#filterOffcanvas"
          aria-controls="filterOffcanvas"
        >
          <i class="bi bi-funnel"></i> Bộ lọc
        </button>
        
        
        <div class="dropdown d-inline-block position-relative">
          
          <button
            ref="sortButton"
            class="btn btn-outline-secondary dropdown-toggle"
            type="button"
            id="sortDropdown"
            aria-expanded="false"
            @click="toggleSortDropdown"
          >
            <i class="bi bi-sort-down me-1"></i> {{ selectedSortLabel }}
          </button>
          <ul
            ref="sortMenu"
            class="dropdown-menu dropdown-menu-end"
            :class="{ show: isSortDropdownOpen }"
            aria-labelledby="sortDropdown"
            style="
              
              
            "
          >
            <li v-for="option in sortOptions" :key="option.value">
              <a
                class="dropdown-item"
                :class="{ active: currentSort === option.value }"
                href="#"
                @click.prevent="handleSortSelection(option.value)"
              >
                {{ option.label }}
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    
    <div
      v-if="loading"
      key="loading-state"
      class="d-flex justify-content-center align-items-center my-5 py-5"
      style="min-height: 40vh"
    >
      <div class="spinner-border text-primary" style="width: 3rem; height: 3rem" role="status">
        <span class="visually-hidden">Đang tải sản phẩm...</span>
      </div>
      <span class="ms-3 fs-5 text-muted">Đang tải sản phẩm...</span>
    </div>

    
    <div
      v-else-if="error"
      key="error-state"
      class="alert alert-danger text-center py-4"
      role="alert"
    >
      <i class="bi bi-exclamation-triangle-fill me-2 fs-4 align-middle"></i>
      <span>Lỗi tải dữ liệu: {{ error.message || "Không thể tải danh sách sản phẩm." }}</span>
      <p class="mt-2 small">
        Vui lòng thử <a href="#" @click.prevent="refreshData">tải lại trang</a> hoặc quay lại sau.
      </p>
    </div>

    
    <div v-else key="content-area" class="row g-4">
      
      <div class="col-lg-3 d-none d-lg-block">
        <div class="filter-panel card shadow-sm sticky-top" style="top: 80px">
          <div class="card-header bg-light">
            <h5 class="mb-0"><i class="bi bi-funnel-fill me-1"></i> Bộ lọc</h5>
          </div>
          <div class="card-body filter-content-desktop">
            
            <FilterContent
              :initial-filters="currentFilters"
              @apply="applyFilters"
              @reset="resetFilters"
            />
          </div>
        </div>
      </div>

      
      <div class="col-12 col-lg-9">
        
        <div
          v-if="hasActiveFilters"
          class="alert alert-light small p-2 d-flex flex-wrap gap-2 align-items-center mb-3 border"
        >
          <strong class="me-1">Đang lọc theo:</strong>
          
          <span
            v-if="
              currentFilters.categories &&
              currentFilters.categories.length > 0 &&
              !route.params.categorySlug
            "
            class="badge text-bg-light border"
          >
            Danh mục <span class="text-muted">({{ currentFilters.categories.length }})</span>
          </span>
          <span
            v-if="currentFilters.minPrice !== null || currentFilters.maxPrice !== null"
            class="badge text-bg-light border"
          >
            Giá: {{ formatCurrency(currentFilters.minPrice ?? 0) }} -
            {{ currentFilters.maxPrice ? formatCurrency(currentFilters.maxPrice) : "..." }}
          </span>
          <span v-if="currentFilters.material" class="badge text-bg-light border">
            Chất liệu: {{ currentFilters.material }}
          </span>
          <span v-if="currentFilters.size" class="badge text-bg-light border">
            Kích thước: {{ formatSize(currentFilters.size) }}
          </span>
          <button
            class="btn btn-link btn-sm text-danger p-0 ms-auto fw-normal"
            @click="resetFilters"
          >
            (Xóa tất cả)
          </button>
        </div>

        
        <div
          v-if="products.length > 0"
          class="row row-cols-1 row-cols-sm-2 row-cols-md-2 row-cols-xl-3 g-4 mb-4"
        >
          <ProductCard v-for="product in products" :key="product.id" :product="product" />
        </div>
        
        <div v-else class="alert alert-info text-center py-5">
          <i class="bi bi-search fs-1 d-block mb-3 text-secondary"></i>
          <h4>Không tìm thấy sản phẩm nào</h4>
          <p class="text-muted">Vui lòng thử thay đổi bộ lọc hoặc tìm kiếm từ khóa khác.</p>
          <button class="btn btn-outline-secondary mt-2" @click="resetFilters">Xóa bộ lọc</button>
        </div>

        
        <BasePagination
          v-if="totalPages > 1"
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="handlePageChange"
          class="d-flex justify-content-center mt-4 pt-2 border-top"
        />
      </div>
    </div>

    
    <div
      class="offcanvas offcanvas-start d-lg-none"
      tabindex="-1"
      id="filterOffcanvas"
      aria-labelledby="filterOffcanvasLabel"
    >
      <div class="offcanvas-header border-bottom">
        <h5 class="offcanvas-title" id="filterOffcanvasLabel">
          <i class="bi bi-funnel"></i> Bộ lọc sản phẩm
        </h5>
        <button
          type="button"
          class="btn-close"
          data-bs-dismiss="offcanvas"
          aria-label="Close"
        ></button>
      </div>
      <div class="offcanvas-body filter-content-mobile">
        
        <FilterContent
          :initial-filters="currentFilters"
          @apply="applyFiltersAndClose"
          @reset="resetFiltersAndClose"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getProducts } from "@/http/modules/public/productService.js";
import { getCategoryBySlug } from "@/http/modules/public/categoryService.js";
import ProductCard from "@/components/product/ProductCard.vue";
import BasePagination from "@/components/common/BasePagination.vue";
import FilterContent from "@/components/product/FilterContent.vue";
import { formatCurrency } from "@/utils/formatters.js";
import { Offcanvas } from "bootstrap";

const route = useRoute();
const router = useRouter();


const products = ref([]);
const currentCategory = ref(null);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const totalElements = ref(null);
const itemsPerPage = ref(12);


const sortOptions = ref([
  { value: "createdAt,desc", label: "Mới nhất" },
  { value: "price,asc", label: "Giá: Thấp đến cao" },
  { value: "price,desc", label: "Giá: Cao đến thấp" },
  { value: "name,asc", label: "Tên: A-Z" },
]);
console.log("Sort Options on Setup:", JSON.stringify(sortOptions.value));

const currentSort = ref(route.query.sort || sortOptions.value[0].value);



const currentFilters = computed(() => {
  const query = route.query;
  return {

    categories: query.categories ? String(query.categories).split(",") : [],
    minPrice: query.minPrice !== undefined && query.minPrice !== "" ? Number(query.minPrice) : null,
    maxPrice: query.maxPrice !== undefined && query.maxPrice !== "" ? Number(query.maxPrice) : null,
    material: query.material || null,
    size: query.size || null,

  };
});


const hasActiveFilters = computed(() => {
  const filters = currentFilters.value;

  const categoryFilterActive = !route.params.categorySlug && filters.categories.length > 0;
  return (
    categoryFilterActive ||
    filters.minPrice !== null ||
    filters.maxPrice !== null ||
    filters.material !== null ||
    filters.size !== null
  );
});


const pageTitle = computed(() => {
  if (route.query.keyword) {
    return `Kết quả tìm kiếm: "${route.query.keyword}"`;
  } else if (currentCategory.value) {
    return `Sản phẩm: ${currentCategory.value.name}`;
  } else {
    return "Tất cả sản phẩm";
  }
});

const selectedSortLabel = computed(() => {
  const selected = sortOptions.value.find((opt) => opt.value === currentSort.value);
  return selected ? selected.label : "Sắp xếp";
});


const formatSize = (sizeValue) => {
  const map = { small: "Nhỏ", medium: "Vừa", large: "Lớn" };
  return map[sizeValue] || sizeValue;
};


const fetchData = async () => {
  loading.value = true;
  error.value = null;
  currentCategory.value = null;
  totalElements.value = null;
  let categoryIdParam = null;

  const categorySlug = route.params.categorySlug;


  if (categorySlug) {
    try {
      const categoryResponse = await getCategoryBySlug(categorySlug);
      currentCategory.value = categoryResponse.data;
      categoryIdParam = currentCategory.value.id;
    } catch (catErr) {
      console.error(`Error fetching category by slug "${categorySlug}":`, catErr);

      error.value = new Error(
        `Không tìm thấy danh mục '${categorySlug}'. Đang hiển thị tất cả sản phẩm.`
      );

    }
  }


  const queryPage = parseInt(route.query.page || "0", 10);
  const querySort = route.query.sort || sortOptions.value[0].value;


  currentPage.value = queryPage;
  currentSort.value = querySort;


  const params = {
    page: currentPage.value,
    size: itemsPerPage.value,
    sort: currentSort.value,
    ...(route.query.keyword && { keyword: route.query.keyword }),
//Đã sửa ở đây
    ...(categoryIdParam && { categories: categoryIdParam }),

    ...(!categoryIdParam &&
      currentFilters.value.categories.length > 0 && {
        //có fix
        categories: currentFilters.value.categories.join(","),
      }),
//có fix
    ...(currentFilters.value.minPrice !== null && { minPrice: currentFilters.value.minPrice }),
    ...(currentFilters.value.maxPrice !== null && { maxPrice: currentFilters.value.maxPrice }),
    ...(currentFilters.value.material && { materials: currentFilters.value.material }),
    ...(currentFilters.value.size && { size: currentFilters.value.size }),

  };


  Object.keys(params).forEach(
    (key) =>
      (params[key] === null || params[key] === undefined || params[key] === "") &&
      delete params[key]
  );

  console.log("Fetching products with params:", params);


  try {
    const response = await getProducts(params);

    if (
      response.data &&
      Array.isArray(response.data.content) &&
      response.data.totalPages !== undefined &&
      response.data.number !== undefined
    ) {
      products.value = response.data.content;
      totalPages.value = response.data.totalPages;
      currentPage.value = response.data.number;
      totalElements.value = response.data.totalElements;
    } else {
      console.error("Invalid API response structure:", response.data);
      throw new Error("Dữ liệu trả về không đúng định dạng.");
    }
  } catch (prodErr) {

    console.error("Error fetching products:", prodErr);
    error.value = prodErr;
    products.value = [];
    totalPages.value = 0;
    totalElements.value = 0;
  } finally {

    loading.value = false;
  }
};


const handlePageChange = (newPage) => {

  router.push({ query: { ...route.query, page: newPage } });
};

const setSort = (sortValue) => {

  router.push({ query: { ...route.query, sort: sortValue, page: 0 } });
};


const applyFilters = (newFilters) => {
  console.log("ProductListView: Applying filters", newFilters);
  const currentQuery = { ...route.query };
  const queryToUpdate = {};





  if (newFilters.categories) {

    queryToUpdate.categories = newFilters.categories;
  } else {
    delete currentQuery.categories;
  }

  if (newFilters.minPrice !== null && newFilters.minPrice !== undefined) {
    queryToUpdate.minPrice = newFilters.minPrice;
  } else {
    delete currentQuery.minPrice;
  }

  if (newFilters.maxPrice !== null && newFilters.maxPrice !== undefined) {
    queryToUpdate.maxPrice = newFilters.maxPrice;
  } else {
    delete currentQuery.maxPrice;
  }

  if (newFilters.material) {
    queryToUpdate.material = newFilters.material;
  } else {
    delete currentQuery.material;
  }

  if (newFilters.size) {
    queryToUpdate.size = newFilters.size;
  } else {
    delete currentQuery.size;
  }



  const finalQuery = {
    ...currentQuery,
    ...queryToUpdate,
    page: 0,
  };


  Object.keys(finalQuery).forEach((key) => {
    if (finalQuery[key] === null || finalQuery[key] === undefined || finalQuery[key] === "") {
      delete finalQuery[key];
    }
  });


  router.push({ query: finalQuery });
};


const resetFilters = () => {
  console.log("ProductListView: Resetting filters");
  const query = { ...route.query };

  delete query.categories;
  delete query.minPrice;
  delete query.maxPrice;
  delete query.material;
  delete query.size;

  query.page = 0;


  router.push({ query });
};


const applyFiltersAndClose = (newFilters) => {
  applyFilters(newFilters);
  closeOffcanvas();
};

const resetFiltersAndClose = () => {
  resetFilters();
  closeOffcanvas();
};

const closeOffcanvas = () => {
  const offcanvasElement = document.getElementById("filterOffcanvas");
  if (offcanvasElement) {
    const bsOffcanvas = Offcanvas.getInstance(offcanvasElement);
    if (bsOffcanvas) {
      bsOffcanvas.hide();
    } else {

      const newBsOffcanvas = new Offcanvas(offcanvasElement);
      newBsOffcanvas.hide();
    }
  }
};


const refreshData = () => {
  error.value = null;
  fetchData();
};




watch(
  () => [route.params, route.query],
  () => {
    fetchData();
  },
  {
    immediate: true,
    deep: true,
  }
);


const isSortDropdownOpen = ref(false);
const sortButton = ref(null);
const sortMenu = ref(null);


const toggleSortDropdown = () => {
  isSortDropdownOpen.value = !isSortDropdownOpen.value;

  if (sortButton.value) {
    sortButton.value.setAttribute("aria-expanded", isSortDropdownOpen.value.toString());
  }
  console.log("Dropdown toggled:", isSortDropdownOpen.value);
};


const handleSortSelection = (sortValue) => {
  setSort(sortValue);
  isSortDropdownOpen.value = false;
  if (sortButton.value) {

    sortButton.value.setAttribute("aria-expanded", "false");
  }
};


const handleClickOutsideSortDropdown = (event) => {

  if (!isSortDropdownOpen.value) return;


  if (
    sortButton.value &&
    !sortButton.value.contains(event.target) &&
    sortMenu.value &&
    !sortMenu.value.contains(event.target)
  ) {
    isSortDropdownOpen.value = false;
    if (sortButton.value) {

      sortButton.value.setAttribute("aria-expanded", "false");
    }
    console.log("Clicked outside, dropdown closed.");
  }
};


onMounted(() => {

  document.addEventListener("click", handleClickOutsideSortDropdown);
});




</script>

<style scoped>

.product-list-view {
  min-height: 70vh;
}

.filter-panel .card-body {
  max-height: calc(100vh - 120px); 
  overflow-y: auto;
}

.dropdown-menu {
  
  
  transition: opacity 0.15s ease-in-out, transform 0.15s ease-in-out, visibility 0.15s;

  
  opacity: 0;
  transform: translateY(-10px); 
  visibility: hidden; 
  display: block; 
  
  
}

.dropdown-menu.show {
  
  opacity: 1;
  transform: translateY(0); 
  visibility: visible; 

  
   
}


.dropdown-item {
  transition: background-color 0.1s ease-in-out, color 0.1s ease-in-out;
}


.dropdown-menu {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15); 
  
}


.dropdown-menu {
  z-index: 1021; 
}


.dropdown.position-relative {
  position: relative !important; 
}
.dropdown-item.active,
.dropdown-item:active {
  color: #fff;
  background-color: var(--bs-primary);
  font-weight: 500;
}

.filter-content-mobile .btn {
  margin-top: 0.5rem;
}


.alert-light .badge {
  border-color: #ccc !important; 
  background-color: #f8f9fa !important; 
  color: var(--bs-body-color); 
  font-weight: 400; 
}

.alert-light .btn-link {
  text-decoration: none;
}
</style>

