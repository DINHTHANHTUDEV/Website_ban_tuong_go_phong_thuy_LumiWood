<template>
  <div class="admin-promotion-form-view">
    
    <div class="mb-3">
      <router-link :to="{ name: 'adminPromotionList' }" class="btn btn-sm btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách KM
      </router-link>
    </div>

    
    <h1 class="mb-4">{{ isEditMode ? 'Chỉnh sửa Khuyến mãi' : 'Thêm Khuyến mãi mới' }}</h1>

    
    <div v-if="isEditMode && loadingInitial" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải dữ liệu...</span>
      </div>
    </div>

    
    <div v-else-if="initialError" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ initialError }}
    </div>

    
    <form v-else @submit.prevent="handleSubmit" novalidate>
      <div class="card shadow-sm">
        <div class="card-body p-4">
          
          <div v-if="submitError" class="alert alert-danger mb-3">{{ submitError }}</div>

          <div class="row g-3">
            
            <div class="col-md-6">
              <label for="promoCode" class="form-label">Mã Code <span class="text-danger">*</span></label>
              <input type="text" class="form-control text-uppercase"
                     :class="{ 'is-invalid': validationErrors.code }"
                     id="promoCode"
                     v-model.trim="formData.code" required
                     :disabled="submitting"
                     @input="formData.code = formData.code.toUpperCase().replace(/[^A-Z0-9]/g, '')">
              <div class="form-text">Chỉ chứa chữ cái (viết hoa) và số, không dấu, không khoảng trắng.</div>
              <div v-if="validationErrors.code" class="invalid-feedback">{{ validationErrors.code }}</div>
            </div>

            
            <div class="col-md-6">
              <label for="promoName" class="form-label">Tên Khuyến mãi</label>
              <input type="text" class="form-control"
                     :class="{ 'is-invalid': validationErrors.name }"
                     id="promoName" v-model.trim="formData.name"
                     :disabled="submitting">
              <div v-if="validationErrors.name" class="invalid-feedback">{{ validationErrors.name }}</div>
            </div>

            
            <div class="col-12">
              <label for="promoDescription" class="form-label">Mô tả</label>
              <textarea class="form-control"
                        :class="{ 'is-invalid': validationErrors.description }"
                        id="promoDescription" rows="3"
                        v-model="formData.description" :disabled="submitting"></textarea>
              <div v-if="validationErrors.description" class="invalid-feedback">{{ validationErrors.description }}</div>
            </div>

            
            <div class="col-md-6">
              <label for="promoDiscountType" class="form-label">Loại giảm giá <span class="text-danger">*</span></label>
              <select class="form-select"
                      :class="{ 'is-invalid': validationErrors.discountType }"
                      id="promoDiscountType" v-model="formData.discountType" required
                      :disabled="submitting">
                <option value="PERCENTAGE">Theo Phần trăm (%)</option>
                <option value="FIXED_AMOUNT">Số tiền cố định (VND)</option>
              </select>
              <div v-if="validationErrors.discountType" class="invalid-feedback">{{ validationErrors.discountType }}</div>
            </div>
            <div class="col-md-6">
              <label for="promoDiscountValue" class="form-label">Giá trị giảm <span class="text-danger">*</span></label>
              <input type="number" :step="formData.discountType === 'PERCENTAGE' ? '0.01' : '1000'"
                     min="0" class="form-control"
                     :class="{ 'is-invalid': validationErrors.discountValue || validationErrors.validPercentage }"
                     id="promoDiscountValue" v-model.number="formData.discountValue" required
                     :disabled="submitting">
              
              <div v-if="validationErrors.discountValue || validationErrors.validPercentage" class="invalid-feedback">
                {{ validationErrors.discountValue || validationErrors.validPercentage }}
              </div>
            </div>

            
            <div class="col-md-6">
              <label for="promoStartDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
              <input type="datetime-local" class="form-control"
                     :class="{ 'is-invalid': validationErrors.startDate || validationErrors.endDateAfterStartDate }"
                     id="promoStartDate" v-model="formData.startDate" required
                     :disabled="submitting">
              
              <div v-if="validationErrors.startDate || validationErrors.endDateAfterStartDate" class="invalid-feedback">
                {{ validationErrors.startDate || validationErrors.endDateAfterStartDate }}
              </div>
            </div>
            <div class="col-md-6">
              <label for="promoEndDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
              <input type="datetime-local" class="form-control"
                     :class="{ 'is-invalid': validationErrors.endDate || validationErrors.endDateAfterStartDate }"
                     id="promoEndDate" v-model="formData.endDate" required :disabled="submitting">
              
              <div v-if="validationErrors.endDate && !validationErrors.endDateAfterStartDate" class="invalid-feedback">
                {{ validationErrors.endDate }}
              </div>
              
              <div v-if="validationErrors.endDateAfterStartDate" class="invalid-feedback">
                {{ validationErrors.endDateAfterStartDate }}
              </div>
            </div>

            
            <div class="col-md-6">
              <label for="promoMaxUsage" class="form-label">Lượt sử dụng tối đa</label>
              <input type="number" min="1" step="1" class="form-control"
                     :class="{ 'is-invalid': validationErrors.maxUsage }"
                     id="promoMaxUsage"
                     v-model.number="formData.maxUsage" placeholder="Để trống nếu không giới hạn"
                     :disabled="submitting">
              <div v-if="validationErrors.maxUsage" class="invalid-feedback">{{ validationErrors.maxUsage }}</div>
            </div>
            <div class="col-md-6">
              <label for="promoMinOrderValue" class="form-label">Giá trị đơn tối thiểu (VND)</label>
              <input type="number" min="0" step="1000" class="form-control"
                     :class="{ 'is-invalid': validationErrors.minOrderValue }"
                     id="promoMinOrderValue"
                     v-model.number="formData.minOrderValue"
                     placeholder="Để trống nếu không yêu cầu" :disabled="submitting">
              <div v-if="validationErrors.minOrderValue" class="invalid-feedback">{{ validationErrors.minOrderValue }}</div>
            </div>

            
            <div class="col-12">
              <label class="form-label mb-2">Áp dụng cho Bậc Khách hàng:</label>
              <div class="d-flex flex-wrap gap-3">
                
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" id="tierAll"
                         :checked="isAllTiersSelected" @change="toggleAllTiers"
                         :disabled="submitting">
                  <label class="form-check-label fw-medium" for="tierAll">Tất cả</label>
                </div>
                
                <div class="form-check" v-for="tier in availableTiers" :key="tier.code">
                  <input class="form-check-input" type="checkbox" :id="'tier-' + tier.code" :value="tier.code"
                         v-model="selectedTiers" :disabled="submitting || isAllTiersSelected">
                  <label class="form-check-label" :for="'tier-' + tier.code">{{ tier.name }}</label>
                </div>
              </div>
              
              <div v-if="validationErrors.targetTiers" class="text-danger small mt-1">
                {{ validationErrors.targetTiers }}
              </div>
            </div>

            
            <div class="col-12">
              <div class="form-check form-switch mt-2">
                <input class="form-check-input" type="checkbox" role="switch" id="promoIsActive"
                       v-model="formData.isActive" :disabled="submitting">
                <label class="form-check-label" for="promoIsActive">
                  {{ formData.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động' }}
                </label>
              </div>
            </div>
          </div>

          
          <hr class="my-4">
          <div class="d-flex justify-content-end">
            <router-link :to="{ name: 'adminPromotionList' }" class="btn btn-outline-secondary me-2"
                         :class="{ 'disabled': submitting }">Hủy
            </router-link>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              <span v-if="submitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Lưu thay đổi' : 'Thêm Khuyến mãi') }}
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter, RouterLink } from 'vue-router';

import {
  createAdminPromotion,
  updateAdminPromotion,
  getAdminPromotionById
} from '@/http/modules/admin/adminPromotionService.js';

import { formatTier, allTiers } from '@/utils/formatters';


const props = defineProps({
  id: {
    type: [String, Number],
    required: false,
    default: null
  }
});

const route = useRoute();
const router = useRouter();


const isEditMode = computed(() => props.id !== null && props.id !== undefined);


const formData = reactive({
  code: '',
  name: '',
  description: '',
  discountType: 'PERCENTAGE',
  discountValue: null,
  startDate: '',
  endDate: '',
  maxUsage: null,
  minOrderValue: null,
  isActive: true,

});


const selectedTiers = ref([]);
const availableTiers = ref(allTiers || []);


const loadingInitial = ref(false);
const initialError = ref(null);
const submitting = ref(false);
const submitError = ref(null);
const validationErrors = reactive({});


const isAllTiersSelected = computed(() => selectedTiers.value.length === 0);


const toggleAllTiers = (event) => {
  if (event.target.checked) {
    selectedTiers.value = [];
  }

};










const fetchPromotionData = async () => {
  if (!isEditMode.value) return;

  loadingInitial.value = true;
  initialError.value = null;
  console.log(`Fetching data for promotion ID: ${props.id}`);
  try {
    const response = await getAdminPromotionById(props.id);
    const promo = response.data;
    console.log('Fetched promotion data:', promo);


    formData.code = promo.code || '';
    formData.name = promo.name || '';
    formData.description = promo.description || '';
    formData.discountType = promo.discountType || 'PERCENTAGE';
    formData.discountValue = promo.discountValue;

    formData.startDate = formatDateTimeForInput(promo.startDate);
    formData.endDate = formatDateTimeForInput(promo.endDate);
    formData.maxUsage = promo.maxUsage;
    formData.minOrderValue = promo.minOrderValue;

    formData.isActive = promo.isActive === undefined ? true : promo.isActive;

    selectedTiers.value = promo.targetTiers ? promo.targetTiers.split(',').map(t => t.trim()).filter(t => t) : [];

  } catch (err) {
    console.error("Error fetching promotion data:", err);
    initialError.value = `Không thể tải dữ liệu khuyến mãi. Lỗi: ${err.response?.data?.message || err.message}`;
  } finally {
    loadingInitial.value = false;
  }
};


const formatDateTimeForInput = (dateTimeString) => {
  if (!dateTimeString) return '';
  try {
    const date = new Date(dateTimeString);
    if (isNaN(date.getTime())) return '';

    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  } catch (e) {
    console.error("Error formatting date for input:", dateTimeString, e);
    return '';
  }
};


const validateForm = () => {

  Object.keys(validationErrors).forEach(key => delete validationErrors[key]);
  let isValid = true;


  if (!formData.code) {
    validationErrors.code = 'Mã code là bắt buộc.'; isValid = false;
  } else if (!/^[A-Z0-9]+$/.test(formData.code)) {
    validationErrors.code = 'Mã chỉ được chứa chữ cái (viết hoa) và số.'; isValid = false;
  }


  if (formData.name && formData.name.length > 255) {
    validationErrors.name = 'Tên không được vượt quá 255 ký tự.'; isValid = false;
  }


  if (!formData.discountType) {
    validationErrors.discountType = 'Loại giảm giá là bắt buộc.'; isValid = false;
  }
  if (formData.discountValue === null || formData.discountValue === undefined) {
    validationErrors.discountValue = 'Giá trị giảm là bắt buộc.'; isValid = false;
  } else if (formData.discountValue <= 0) {
    validationErrors.discountValue = 'Giá trị giảm phải lớn hơn 0.'; isValid = false;
  } else if (formData.discountType === 'PERCENTAGE' && formData.discountValue > 100) {
    validationErrors.validPercentage = 'Giá trị giảm phần trăm không được lớn hơn 100.'; isValid = false;
  }


  if (!formData.startDate) {
    validationErrors.startDate = 'Ngày bắt đầu là bắt buộc.'; isValid = false;
  }
  if (!formData.endDate) {
    validationErrors.endDate = 'Ngày kết thúc là bắt buộc.'; isValid = false;
  } else if (formData.startDate && formData.endDate) {
    try {

      const start = new Date(formData.startDate);
      const end = new Date(formData.endDate);
      if (isNaN(start.getTime())) {
        validationErrors.startDate = 'Ngày bắt đầu không hợp lệ.'; isValid = false;
      }
      if (isNaN(end.getTime())) {
        validationErrors.endDate = 'Ngày kết thúc không hợp lệ.'; isValid = false;
      }
      if (!isNaN(start.getTime()) && !isNaN(end.getTime()) && end <= start) {
        validationErrors.endDateAfterStartDate = 'Ngày kết thúc phải sau ngày bắt đầu.'; isValid = false;
      }
    } catch (e) {
      validationErrors.startDate = 'Định dạng ngày giờ không hợp lệ.'; isValid = false;
      validationErrors.endDate = 'Định dạng ngày giờ không hợp lệ.'; isValid = false;
    }
  }



  if (formData.maxUsage !== null && formData.maxUsage !== '' && ( !Number.isInteger(Number(formData.maxUsage)) || Number(formData.maxUsage) < 1 ) ) {
    validationErrors.maxUsage = 'Lượt sử dụng tối đa phải là số nguyên lớn hơn hoặc bằng 1.'; isValid = false;
  }
  if (formData.minOrderValue !== null && formData.minOrderValue !== '' && Number(formData.minOrderValue) < 0) {
    validationErrors.minOrderValue = 'Giá trị đơn tối thiểu không được âm.'; isValid = false;
  }

  return isValid;
};


const handleSubmit = async () => {
  submitError.value = null;

  if (!validateForm()) {
    console.log("Client-side validation failed:", validationErrors);

    window.scrollTo({ top: 0, behavior: 'smooth' });
    return;
  }

  submitting.value = true;


  const payload = {
    ...formData,


    targetTiers: selectedTiers.value.length > 0 ? selectedTiers.value.join(',') : null,




  };

  console.log("Submitting payload:", payload);

  try {
    let response;
    if (isEditMode.value) {
      console.log(`Calling updateAdminPromotion for ID: ${props.id}`);
      response = await updateAdminPromotion(props.id, payload);

      console.log("Update successful:", response.data);
    } else {
      console.log("Calling createAdminPromotion");
      response = await createAdminPromotion(payload);

      console.log("Create successful:", response.data);
    }

    router.push({ name: 'adminPromotionList' });

  } catch (err) {
    console.error("Error submitting promotion form:", err);

    if (err.response) {
      console.error("Backend error response:", err.response.data);
      const errorData = err.response.data;

      submitError.value = errorData.message || `Đã có lỗi xảy ra khi ${isEditMode.value ? 'cập nhật' : 'thêm'} khuyến mãi.`;


      if (errorData.errors) {

        for (const errorField in errorData.errors) {
          validationErrors[errorField] = errorData.errors[errorField];
        }
      } else if (err.response.status === 409) {
        validationErrors.code = errorData.message || "Mã code này đã tồn tại.";
      }
    } else {

      submitError.value = "Đã có lỗi xảy ra. Vui lòng kiểm tra kết nối và thử lại.";
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  } finally {
    submitting.value = false;
  }
};


onMounted(() => {

  if (isEditMode.value) {
    fetchPromotionData();
  } else {

    formData.startDate = formatDateTimeForInput(new Date());
  }
});

</script>

<style scoped>

.is-invalid ~ .invalid-feedback,
.is-invalid ~ .text-danger {
  display: block;
}


.form-check-label {
  cursor: pointer;
  margin-left: 0.25rem;
}
.form-check-input {
  cursor: pointer;
}
.form-switch .form-check-input {
  width: 3em; 
}
</style>

