<template>
  <div class="filter-content">
    <!-- 1. Filter theo Danh mục -->
    <div class="mb-4 filter-group">
      <h6 class="filter-title d-flex justify-content-between align-items-center">
        <span>Theo Danh mục</span>
        <button
          class="btn btn-sm btn-link p-0 text-decoration-none"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#collapseCategoryFilter"
          aria-expanded="true"
          aria-controls="collapseCategoryFilter"
        >
          <i class="bi bi-chevron-up small"></i>
        </button>
      </h6>
      <div class="collapse show" id="collapseCategoryFilter">
        <div class="filter-options mt-2" style="max-height: 200px; overflow-y: auto">
          <div v-if="loadingCategories" class="text-muted small fst-italic py-2">Đang tải...</div>
          <div v-else-if="categoryError" class="text-danger small fst-italic py-2">
            {{ categoryError }}
          </div>
          <div v-else-if="categories.length === 0" class="text-muted small fst-italic py-2">
            Không có danh mục.
          </div>
          <div v-else>
            <div v-for="cat in categories" :key="cat.id" class="form-check mb-2">
              <input
                class="form-check-input"
                type="checkbox"
                :value="cat.id.toString()"
                :id="'catFilter-' + cat.id"
                v-model="localFilters.categories"
              />
              <label class="form-check-label" :for="'catFilter-' + cat.id">{{ cat.name }}</label>
            </div>
          </div>
        </div>
      </div>
    </div>
    <hr />

    <!-- 2. Filter theo Giá -->
    <div class="mb-4 filter-group">
      <h6 class="filter-title">Theo Giá</h6>
      <div class="row g-2 align-items-center">
        <div class="col">
          <label for="minPriceFilter" class="form-label small visually-hidden">Giá từ</label>
          <div class="input-group input-group-sm">
            <span class="input-group-text bg-light border-end-0 text-muted">Từ</span>
            <input
              type="number"
              class="form-control"
              id="minPriceFilter"
              placeholder="0"
              v-model.number="localFilters.minPrice"
              min="0"
              step="10000"
              aria-label="Giá tối thiểu"
            />
            <span class="input-group-text bg-light border-start-0 text-muted">₫</span>
          </div>
        </div>
        <div class="col-auto px-0 text-muted">-</div>
        <div class="col">
          <label for="maxPriceFilter" class="form-label small visually-hidden">Giá đến</label>
          <div class="input-group input-group-sm">
            <span class="input-group-text bg-light border-end-0 text-muted">Đến</span>
            <input
              type="number"
              class="form-control"
              id="maxPriceFilter"
              placeholder="Tối đa"
              v-model.number="localFilters.maxPrice"
              min="0"
              step="10000"
              aria-label="Giá tối đa"
            />
            <span class="input-group-text bg-light border-start-0 text-muted">₫</span>
          </div>
        </div>
      </div>
      <div v-if="priceValidationError" class="text-danger small mt-2">
        {{ priceValidationError }}
      </div>
    </div>
    <hr />

    <!-- 3. Filter theo Chất liệu -->
    <div class="mb-4 filter-group">
      <h6 class="filter-title">Theo Chất liệu</h6>
      <select
        class="form-select form-select-sm"
        v-model="localFilters.material"
        aria-label="Chọn chất liệu"
      >
        <option :value="null">-- Tất cả chất liệu --</option>
        <!-- Danh sách này nên lấy động từ API nếu có thể -->
        <option value="Gỗ Hương">Gỗ Hương</option>
        <option value="Gỗ Trắc">Gỗ Trắc</option>
        <option value="Gỗ Ngọc Am">Gỗ Ngọc Am</option>
        <option value="Gỗ Bách Xanh">Gỗ Bách Xanh</option>
        <option value="Gỗ Mun">Gỗ Mun</option>
        <option value="Gỗ Mun Sừng">Gỗ Mun Sừng</option>
        <option value="Gỗ Cẩm">Gỗ Cẩm</option>
        <option value="Gỗ Pơ Mu">Gỗ Pơ Mu</option>
        <option value="Đá Ngọc Pakistan">Đá Ngọc Pakistan</option>
        <option value="Gỗ Nu Nghiến">Gỗ Nu Nghiến</option>
        <option value="Gỗ Thông">Gỗ Thông</option>
      </select>
    </div>
    <hr />

    <!-- 4. Filter theo Kích thước (Lấy từ phiên bản 1) -->
    <div class="mb-4 filter-group">
      <h6 class="filter-title">Theo Kích thước</h6>
      <div class="filter-options mt-2">
        <div class="form-check mb-2">
          <input
            class="form-check-input"
            type="radio"
            name="sizeFilter"
            id="sizeAll"
            :value="null"
            v-model="localFilters.size"
          />
          <label class="form-check-label" for="sizeAll">Tất cả kích thước</label>
        </div>
        <div class="form-check mb-2">
          <input
            class="form-check-input"
            type="radio"
            name="sizeFilter"
            id="sizeSmall"
            value="small"
            v-model="localFilters.size"
          />
          <label class="form-check-label" Ffor="sizeSmall">Nhỏ (Dưới 30cm)</label>
        </div>
        <div class="form-check mb-2">
          <input
            class="form-check-input"
            type="radio"
            name="sizeFilter"
            id="sizeMedium"
            value="medium"
            v-model="localFilters.size"
          />
          <label class="form-check-label" for="sizeMedium">Vừa (30cm - 60cm)</label>
        </div>
        <div class="form-check mb-2">
          <input
            class="form-check-input"
            type="radio"
            name="sizeFilter"
            id="sizeLarge"
            value="large"
            v-model="localFilters.size"
          />
          <label class="form-check-label" for="sizeLarge">Lớn (Trên 60cm)</label>
        </div>
      </div>
    </div>
    <hr />

    <!-- Nút Apply và Reset -->
    <div class="d-grid gap-2 mt-4 pt-2 border-top">
      <button
        class="btn btn-primary"
        type="button"
        @click="emitApply"
        :disabled="!!priceValidationError"
      >
        <i class="bi bi-check-lg me-1"></i> Áp dụng bộ lọc
      </button>
      <button class="btn btn-outline-secondary" type="button" @click="emitReset">
        <i class="bi bi-arrow-clockwise me-1"></i> Xóa bộ lọc
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, computed } from "vue";
import { getAllCategories } from "@/http/modules/public/categoryService.js"; // Đảm bảo đường dẫn đúng

const props = defineProps({
  initialFilters: {
    type: Object,
    required: true,
    default: () => ({
      categories: null,
      minPrice: null,
      maxPrice: null,
      material: null,
      size: null,
    }), // Thêm size vào default
  },
});

const emit = defineEmits(["apply", "reset"]);

// State local
const localFilters = reactive({
  categories: [], // Luôn là mảng các string ID
  minPrice: null,
  maxPrice: null,
  material: null,
  size: null, // Thêm state cho size
});

// State cho danh mục
const categories = ref([]);
const loadingCategories = ref(false);
const categoryError = ref(null);

// Fetch Categories
const fetchCategories = async () => {
  loadingCategories.value = true;
  categoryError.value = null;
  try {
    const response = await getAllCategories();
    categories.value = response.data || [];
  } catch (err) {
    console.error("FilterContent: Error fetching categories", err);
    categoryError.value = "Lỗi tải danh mục";
  } finally {
    loadingCategories.value = false;
  }
};

// Computed property để kiểm tra lỗi giá
const priceValidationError = computed(() => {
  const min = localFilters.minPrice;
  const max = localFilters.maxPrice;
  if (min !== null && max !== null && min > 0 && max > 0 && min > max) {
    return "Giá tối thiểu không được lớn hơn giá tối đa.";
  }
  return null;
});

// Hàm emit apply
const emitApply = () => {
  if (priceValidationError.value) {
    alert(priceValidationError.value);
    return;
  }
  // Chuẩn bị dữ liệu để emit
  const filtersToApply = {
    // Chuyển mảng categories thành string (nếu backend yêu cầu)
    categories: localFilters.categories.length > 0 ? localFilters.categories.join(",") : undefined,
    minPrice:
      localFilters.minPrice !== null && localFilters.minPrice >= 0
        ? localFilters.minPrice
        : undefined,
    maxPrice:
      localFilters.maxPrice !== null && localFilters.maxPrice >= 0
        ? localFilters.maxPrice
        : undefined,
    material: localFilters.material || undefined,
    size: localFilters.size || undefined, // Thêm size
  };
  // Loại bỏ các key undefined
  Object.keys(filtersToApply).forEach((key) => {
    if (filtersToApply[key] === undefined) delete filtersToApply[key];
  });
  console.log("FilterContent: Emitting apply", filtersToApply);
  emit("apply", filtersToApply);
};

// Hàm emit reset
const emitReset = () => {
  console.log("FilterContent: Emitting reset");
  // Reset local state
  localFilters.categories = [];
  localFilters.minPrice = null;
  localFilters.maxPrice = null;
  localFilters.material = null;
  localFilters.size = null; // Reset size
  // Emit sự kiện
  emit("reset");
};

// Đồng bộ initialFilters từ prop vào localFilters
watch(
  () => props.initialFilters,
  (newFilters) => {
    console.log("FilterContent: Syncing initialFilters prop to localFilters", newFilters);

    // Xử lý categories: Đảm bảo là mảng string ID
    if (newFilters.categories) {
      // Nếu là string (vd: "1,2,3"), tách ra thành mảng
      if (typeof newFilters.categories === "string") {
        localFilters.categories = newFilters.categories
          .split(",")
          .map(String)
          .filter((id) => id); // Lọc bỏ id rỗng nếu có
      }
      // Nếu đã là mảng, đảm bảo các phần tử là string
      else if (Array.isArray(newFilters.categories)) {
        localFilters.categories = newFilters.categories.map(String);
      } else {
        localFilters.categories = []; // Không xác định được thì reset
      }
    } else {
      localFilters.categories = []; // Nếu không có thì là mảng rỗng
    }

    // Xử lý giá
    localFilters.minPrice =
      newFilters.minPrice !== null && !isNaN(Number(newFilters.minPrice))
        ? Number(newFilters.minPrice)
        : null;
    localFilters.maxPrice =
      newFilters.maxPrice !== null && !isNaN(Number(newFilters.maxPrice))
        ? Number(newFilters.maxPrice)
        : null;
    // Xử lý material và size
    localFilters.material = newFilters.material || null;
    localFilters.size = newFilters.size || null; // Đồng bộ size
  },
  { immediate: true, deep: true }
);

// Fetch categories khi component mount
onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
/* Giữ nguyên style từ phiên bản 1 */
.filter-content hr {
  margin-top: 1rem;
  margin-bottom: 1rem;
  opacity: 0.15;
}

.filter-title {
  font-weight: 600;
  font-size: 0.95rem;
  margin-bottom: 0.8rem;
}

.filter-options {
  padding-left: 0.25rem;
}

.form-check-label {
  font-size: 0.9em;
  cursor: pointer;
}

.form-control-sm,
.form-select-sm {
  font-size: 0.875rem;
}

.input-group-text {
  font-size: 0.8em;
}

.d-grid .btn {
  font-size: 0.9rem;
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.filter-title .btn-link {
  font-size: 0.8em;
  color: var(--bs-secondary-color);
}

.filter-title .btn-link:hover {
  color: var(--bs-primary);
}

.filter-title .btn-link i {
  transition: transform 0.2s ease-in-out;
}

.filter-title .btn-link[aria-expanded="false"] i {
  transform: rotate(-90deg);
}
</style>
