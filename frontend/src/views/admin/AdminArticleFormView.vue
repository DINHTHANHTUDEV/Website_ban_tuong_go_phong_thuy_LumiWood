<template>
  <div class="admin-article-form-view container mt-4 mb-5">
    
    <div class="mb-3">
      <router-link :to="{ name: 'adminArticleList' }" class="btn btn-sm btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách Bài viết
      </router-link>
    </div>

    <h1 class="mb-4">{{ pageTitle }}</h1>

    
    <div v-if="loadingInitial" class="text-center my-5 py-5">
      <div class="spinner-border text-primary" role="status"><span class="visually-hidden">Đang tải dữ liệu...</span>
      </div>
    </div>

    
    <div v-else-if="initialError" class="alert alert-danger">
      Lỗi tải dữ liệu bài viết: {{ initialError }}
    </div>

    
    <form v-else @submit.prevent="handleSubmit" novalidate>
      <div class="card shadow-sm">
        <div class="card-body p-4">
          
          <div v-if="submitError" class="alert alert-danger mb-3">{{ submitError }}</div>

          <div class="row g-3">
            
            <div class="col-12">
              <label for="articleTitle" class="form-label">Tiêu đề <span
                class="text-danger">*</span></label>
              <input type="text" class="form-control"
                     :class="{'is-invalid': validationErrors.title}" id="articleTitle"
                     v-model.trim="formData.title" required :disabled="submitting" maxlength="250">
              <div class="invalid-feedback">{{ validationErrors.title }}</div>
            </div>

            
            <div class="col-12">
              <label for="articleContent" class="form-label">Nội dung <span
                class="text-danger">*</span></label>
              <textarea class="form-control" :class="{'is-invalid': validationErrors.content}"
                        id="articleContent" rows="15" v-model="formData.content" required
                        :disabled="submitting"
                        placeholder="Nhập nội dung bài viết ở đây..."></textarea>
              
              <div class="invalid-feedback">{{ validationErrors.content }}</div>
            </div>

            
            <div class="col-12">
              <label for="articleExcerpt" class="form-label">Tóm tắt (SEO & Preview)</label>
              <textarea class="form-control" :class="{'is-invalid': validationErrors.excerpt}"
                        id="articleExcerpt" rows="3" v-model="formData.excerpt"
                        :disabled="submitting" maxlength="500"
                        placeholder="Mô tả ngắn gọn nội dung bài viết..."></textarea>
              <div class="invalid-feedback">{{ validationErrors.excerpt }}</div>
            </div>

            
            <div class="col-12">
              <label for="articleImageUrl" class="form-label">URL Ảnh đại diện</label>
              <input type="url" class="form-control"
                     :class="{'is-invalid': validationErrors.featuredImageUrl}" id="articleImageUrl"
                     v-model.trim="formData.featuredImageUrl" placeholder="https://..."
                     :disabled="submitting" maxlength="512">
              <div class="invalid-feedback">{{ validationErrors.featuredImageUrl }}</div>
              <img v-if="formData.featuredImageUrl && !validationErrors.featuredImageUrl"
                   :src="formData.featuredImageUrl" alt="Xem trước ảnh đại diện"
                   class="mt-2 rounded border"
                   style="max-height: 150px; max-width: 100%; object-fit: contain;"
                   @error="onImagePreviewError">
              <small v-if="imagePreviewError" class="text-danger d-block mt-1">Không thể tải ảnh xem
                trước.</small>
            </div>
          </div>

          
          <hr class="my-4">
          <div class="d-flex flex-wrap justify-content-end align-items-center gap-2">
            
            <span v-if="isEditMode" class="me-auto text-muted small">
                 Trạng thái hiện tại:
                 <span class="badge rounded-pill"
                       :class="isPublished ? 'text-bg-success' : 'text-bg-secondary'">
                     {{ isPublished ? 'Đã xuất bản' : 'Bản nháp' }}
                 </span>
             </span>

            <router-link :to="{ name: 'adminArticleList' }" class="btn btn-outline-secondary"
                         :disabled="submitting || togglingPublish">Hủy
            </router-link>

            
            <button v-if="isEditMode && !isPublished" type="button" class="btn btn-success"
                    @click="handleTogglePublish(true)" :disabled="submitting || togglingPublish">
              <span v-if="togglingPublish && isPublishingAction"
                    class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-check-circle me-1"></i>
              Lưu & Xuất bản
            </button>
            <button v-if="isEditMode && isPublished" type="button" class="btn btn-warning"
                    @click="handleTogglePublish(false)" :disabled="submitting || togglingPublish">
              <span v-if="togglingPublish && !isPublishingAction"
                    class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-arrow-down-square me-1"></i>
              Lưu & Chuyển về Nháp
            </button>

            
            <button type="submit" class="btn btn-primary" :disabled="submitting || togglingPublish">
              <span v-if="submitting" class="spinner-border spinner-border-sm me-1"></span>
              {{ submitButtonText }}
            </button>
          </div>
          
          <div v-if="togglePublishError" class="alert alert-danger small mt-3 p-2 mb-0">
            {{ togglePublishError }}
          </div>
          <div v-if="togglePublishSuccess" class="alert alert-success small mt-3 p-2 mb-0">
            {{ togglePublishSuccess }}
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted, watch} from 'vue';
import {useRoute, useRouter, RouterLink} from 'vue-router';
import {
  getAdminArticleById,
  createAdminArticle,
  updateAdminArticle,
  publishAdminArticle,
  unpublishAdminArticle
} from '@/http/modules/admin/adminArticleService.js';


const props = defineProps({
  id: {type: [String, Number], default: null}
});

const route = useRoute();
const router = useRouter();


const isEditMode = computed(() => !!props.id);
const pageTitle = computed(() => isEditMode.value ? 'Chỉnh sửa Bài viết' : 'Thêm Bài viết mới');
const submitButtonText = computed(() => {
  if (submitting.value) return 'Đang lưu...';
  if (isEditMode.value) return isPublished.value ? 'Lưu bài viết (Đã XB)' : 'Lưu bản nháp';
  return 'Tạo bài viết (Nháp)';
});


const formData = reactive({
  title: '',
  content: '',
  excerpt: '',
  featuredImageUrl: ''
});
const isPublished = ref(false);


const loadingInitial = ref(false);
const initialError = ref(null);
const submitting = ref(false);
const submitError = ref(null);
const validationErrors = reactive({});
const imagePreviewError = ref(false);


const togglingPublish = ref(false);
const isPublishingAction = ref(false);
const togglePublishError = ref(null);
const togglePublishSuccess = ref(null);


const fetchArticleData = async () => {
  if (!isEditMode.value) return;
  loadingInitial.value = true;
  initialError.value = null;
  resetFormMessages();
  try {
    const response = await getAdminArticleById(props.id);
    const article = response.data;
    if (!article) throw new Error("Không tìm thấy bài viết");
    formData.title = article.title || '';
    formData.content = article.content || '';
    formData.excerpt = article.excerpt || '';
    formData.featuredImageUrl = article.featuredImageUrl || '';
    isPublished.value = article.isPublished || false;
    imagePreviewError.value = false;
  } catch (err) {
    console.error(`Error fetching article data for ID ${props.id}:`, err);
    initialError.value = err.response?.data?.message || err.message || "Không thể tải dữ liệu bài viết.";
  } finally {
    loadingInitial.value = false;
  }
};


const validateForm = () => {
  Object.keys(validationErrors).forEach(key => delete validationErrors[key]);
  let isValid = true;
  if (!formData.title || formData.title.length < 5 || formData.title.length > 250) {
    validationErrors.title = 'Tiêu đề phải từ 5 đến 250 ký tự.';
    isValid = false;
  }
  if (!formData.content || formData.content.length < 50) {

    validationErrors.content = 'Nội dung phải có ít nhất 50 ký tự.';
    isValid = false;
  }
  if (formData.excerpt && formData.excerpt.length > 500) {
    validationErrors.excerpt = 'Tóm tắt không quá 500 ký tự.';
    isValid = false;
  }
  if (formData.featuredImageUrl) {
    try {
      new URL(formData.featuredImageUrl);
      imagePreviewError.value = false;
    } catch (_) {
      validationErrors.featuredImageUrl = 'URL ảnh không hợp lệ.';
      isValid = false;
      imagePreviewError.value = true;
    }
  } else {
    imagePreviewError.value = false;
  }
  return isValid;
};


const onImagePreviewError = () => {
  imagePreviewError.value = true;
}


const resetFormMessages = () => {
  submitError.value = null;
  togglePublishError.value = null;
  togglePublishSuccess.value = null;
  Object.keys(validationErrors).forEach(key => delete validationErrors[key]);
}


const handleSubmit = async () => {
  resetFormMessages();
  if (!validateForm()) return;

  submitting.value = true;
  try {
    const payload = {...formData};
    let response;
    if (isEditMode.value) {
      response = await updateAdminArticle(props.id, payload);

      const updatedArticle = response.data;
      formData.title = updatedArticle.title || '';
      formData.content = updatedArticle.content || '';
      formData.excerpt = updatedArticle.excerpt || '';
      formData.featuredImageUrl = updatedArticle.featuredImageUrl || '';


      alert("Đã lưu thay đổi!");
    } else {
      response = await createAdminArticle(payload);

      router.push({name: 'adminArticleEdit', params: {id: response.data.id}});

      alert("Đã tạo bài viết (bản nháp)!");
      return;
    }
  } catch (err) {
    console.error("Error saving article:", err);
    submitError.value = err.response?.data?.message || `Đã có lỗi xảy ra khi lưu bài viết.`;
    if (err.response?.status === 400 && err.response?.data?.errors) { 
    }
    if (err.response?.status === 409) {
      validationErrors.title = "Tiêu đề hoặc slug đã tồn tại.";
    }

  } finally {
    submitting.value = false;
  }
};


const handleTogglePublish = async (publishAction) => {
  if (!isEditMode.value || togglingPublish.value || submitting.value) return;


  submitError.value = null;
  if (!validateForm()) {
    alert("Vui lòng sửa các lỗi trong form trước khi xuất bản/chuyển về nháp.");
    return;
  }

  await handleSubmit();

  if (submitError.value) {
    return;
  }


  const actionText = publishAction ? 'xuất bản' : 'chuyển thành bản nháp';
  togglingPublish.value = true;
  isPublishingAction.value = publishAction;
  togglePublishError.value = null;
  togglePublishSuccess.value = null;

  try {
    const apiCall = publishAction ? publishAdminArticle : unpublishAdminArticle;
    const response = await apiCall(props.id);
    isPublished.value = response.data.isPublished;
    togglePublishSuccess.value = `${actionText.charAt(0).toUpperCase() + actionText.slice(1)} thành công!`;
    setTimeout(() => togglePublishSuccess.value = null, 3000);

  } catch (err) {
    console.error(`Error ${actionText} article ${props.id}:`, err);
    togglePublishError.value = err.response?.data?.message || `Lỗi khi ${actionText} bài viết.`;
    setTimeout(() => togglePublishError.value = null, 5000);

  } finally {
    togglingPublish.value = false;
  }
};


onMounted(() => {
  if (isEditMode.value) {
    fetchArticleData();
  }
});


watch(() => props.id, (newId, oldId) => {
  if (newId !== oldId) {
    resetFormMessages();
    if (newId) {
      fetchArticleData();
    } else {
      Object.assign(formData, {title: '', content: '', excerpt: '', featuredImageUrl: ''});
      isPublished.value = false;
    }
  }
});

</script>

<style scoped>
.admin-article-form-view {
  min-height: 80vh;
}

.is-invalid ~ .invalid-feedback {
  display: block;
}
</style>

