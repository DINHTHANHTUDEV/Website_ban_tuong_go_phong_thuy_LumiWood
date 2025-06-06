<template>
  <div class="chat-widget-container">
    <!-- Floating Action Button (FAB) -->
    <button
      class="btn btn-primary rounded-circle shadow-lg chat-fab"
      type="button"
      @click="toggleChat"
      aria-label="Mở cửa sổ chat"
      title="Chat với trợ lý ảo"
      :aria-expanded="isChatOpen.toString()"
    >
      <i v-if="!isChatOpen" class="bi bi-chat-dots-fill fs-4"></i>
      <i v-else class="bi bi-x-lg fs-4"></i>
    </button>

    <!-- Chat Window (Tự quản lý state hiển thị) -->
    <div
      v-if="isChatVisible"
      class="chat-window card shadow-lg"
      :class="{ 'open': isChatOpen }"
      ref="chatWindowRef"
      role="dialog"
      aria-modal="true"
      aria-labelledby="chatWindowTitle"
    >
      <!-- Header (Style giống Offcanvas header) -->
      <div
        class="card-header chat-header bg-light border-bottom d-flex justify-content-between align-items-center">
        <h5 class="card-title chat-title mb-0 fs-6 fw-semibold" id="chatWindowTitle">
          <i class="bi bi-robot me-2"></i> Trợ lý ảo Phong Thủy HV
        </h5>
        <button type="button" class="btn-close small chat-close-btn" @click="toggleChat"
                aria-label="Đóng"></button>
      </div>

      <!-- Body (Style giống Offcanvas body) -->
      <div class="card-body chat-body d-flex flex-column p-0">

        <!-- Message Area -->
        <div class="message-area flex-grow-1 p-3" ref="messageAreaRef">
          <!-- Lặp qua Lịch sử Chat -->
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="chat-message mb-2"
            :class="getMessageClass(msg.role)"
          >
            <div class="message-bubble" v-html="formatMessageContent(msg)"></div>
          </div>
          <!-- Loading Indicator -->
          <div v-if="isLoading" class="chat-message bot mb-2">
            <div class="message-bubble typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
          <!-- Error Message -->
          <div v-if="error" class="chat-message bot mb-2">
            <div class="message-bubble bg-danger-subtle text-danger border border-danger-subtle">
              <i class="bi bi-exclamation-circle-fill me-1"></i> {{ error }}
            </div>
          </div>
        </div>

        <!-- Input Area -->
        <div class="input-area p-3 border-top bg-light">
          <form @submit.prevent="sendMessage" class="input-form">
            <div class="input-group">
              <input
                type="text"
                class="form-control"
                v-model.trim="newMessage"
                placeholder="Nhập câu hỏi của bạn..."
                aria-label="Nhập câu hỏi"
                :disabled="isLoading"
                ref="messageInputRef"
              />
              <button class="btn btn-primary" type="submit" :disabled="isLoading || !newMessage">
                <span v-if="isLoading" class="spinner-border spinner-border-sm" role="status"
                      aria-hidden="true"></span>
                <i v-else class="bi bi-send-fill"></i>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, nextTick} from 'vue';
import {sendMessageToAI} from '@/http/chatbox/chatService.js';
import DOMPurify from 'dompurify';
import {marked} from 'marked';

// --- State ---
const isChatOpen = ref(false);
const isChatVisible = ref(false);
const messages = ref([]);
const newMessage = ref('');
const isLoading = ref(false);
const error = ref(null);
const chatWindowRef = ref(null);
const messageAreaRef = ref(null);
const messageInputRef = ref(null);

// --- Methods ---

// Mở/đóng chatbox (Logic của phiên bản tự quản lý state)
const toggleChat = () => {
  if (isChatOpen.value) {
    isChatOpen.value = false;
    // Đợi transition CSS hoàn thành trước khi ẩn hẳn bằng v-if
    setTimeout(() => {
      isChatVisible.value = false;
    }, 300); // <<< Thời gian này nên khớp với transition duration trong CSS
  } else {
    isChatVisible.value = true; // Hiện component để bắt đầu transition
    nextTick(() => { // Đợi DOM update xong mới thêm class 'open' để kích hoạt transition
      isChatOpen.value = true;
      messageInputRef.value?.focus();
      scrollToBottom(); // Scroll khi mở
    });
  }
};

// Cuộn xuống cuối khu vực tin nhắn
const scrollToBottom = () => {
  nextTick(() => {
    const el = messageAreaRef.value;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  });
};

// Lấy class CSS cho tin nhắn dựa trên vai trò
const getMessageClass = (role) => {
  return role === 'user' ? 'user' : 'bot';
};

// Định dạng nội dung tin nhắn (Hỗ trợ Markdown + Làm sạch HTML)
const formatMessageContent = (message) => {
  const text = message?.parts?.[0]?.text;
  if (!text) return '';
  try {
    // Parse Markdown thành HTML, sau đó làm sạch để tránh XSS
    const cleanHtml = DOMPurify.sanitize(marked.parse(text), {USE_PROFILES: {html: true}});
    return cleanHtml;
  } catch (e) {
    console.error("Error formatting message:", e);
    return text; // Trả về text gốc nếu có lỗi
  }
};

// Gửi tin nhắn đến backend
const sendMessage = async () => {
  if (!newMessage.value || isLoading.value) return;

  const userMsgText = newMessage.value;
  messages.value.push({role: 'user', parts: [{text: userMsgText}]});
  newMessage.value = '';
  isLoading.value = true;
  error.value = null; // Xóa lỗi cũ khi gửi tin mới
  scrollToBottom();

  // Chuẩn bị lịch sử gửi đi (giới hạn số lượng tin nhắn gần nhất)
  const historyLimit = 18; // Giới hạn số cặp tin nhắn user/model gửi đi làm context
  const historyToSend = messages.value.slice(Math.max(messages.value.length - 1 - historyLimit, 0), -1) // Lấy các tin trước tin nhắn user cuối cùng
    .map(msg => ({
      role: msg.role,
      content: msg.parts[0].text // API backend có thể cần định dạng khác
    }));

  try {
    // ----- GỌI BACKEND API -----
    const response = await sendMessageToAI(userMsgText, historyToSend);
    // --------------------------
    if (response && response.data && response.data.reply) { // <<< Kiểm tra response.data.reply
      messages.value.push({role: 'model', parts: [{text: response.data.reply}]}); // <<< Lấy từ response.data.reply
    } else {
      // Log cả response để debug nếu cấu trúc khác nữa
      console.warn("Invalid response structure from backend (expected response.data.reply):", response);
      error.value = "Phản hồi từ trợ lý ảo không hợp lệ hoặc thiếu nội dung.";
    }
  } catch (err) {
    console.error("Error sending message:", err);
    // Cố gắng lấy thông báo lỗi chi tiết hơn từ response nếu có
    const errorMsg = err.response?.data?.reply || err.response?.data?.error || err.response?.data?.message || err.message || "Không thể gửi tin nhắn.";
    error.value = errorMsg;
  } finally {
    isLoading.value = false;
    scrollToBottom();
    nextTick(() => {
      messageInputRef.value?.focus();
    }); // Focus lại input sau khi gửi/nhận
  }
};

// Xử lý khi click ra ngoài cửa sổ chat để đóng nó lại
const handleClickOutside = (event) => {
  if (isChatOpen.value && chatWindowRef.value && !chatWindowRef.value.contains(event.target)) {
    // Chỉ đóng nếu click ra ngoài và không phải click vào nút FAB
    const fabButton = event.target.closest('.chat-fab');
    if (!fabButton) {
      toggleChat(); // Đóng chat
    }
  }
};

// --- Lifecycle ---
onMounted(() => {
  // Thêm tin nhắn chào mừng ban đầu
  messages.value.push({
    role: 'model',
    parts: [{text: 'Xin chào! Tôi có thể giúp gì cho bạn về Tượng Gỗ Phong Thủy HV?'}]
  });
  // Thêm listener để xử lý click ra ngoài
  document.addEventListener('mousedown', handleClickOutside);
});

onBeforeUnmount(() => {
  // Gỡ bỏ listener khi component bị hủy để tránh memory leak
  document.removeEventListener('mousedown', handleClickOutside);
});

</script>

<style scoped>

.chat-fab {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1051;
  width: 60px;
  height: 60px;
  font-size: 1.6rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}

.chat-fab:hover {
  transform: scale(1.1);
}

.chat-window {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 370px;
  height: 70vh;
  max-height: 550px;
  z-index: 1050;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 0.75rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* Thuộc tính cho animation */
  opacity: 0;
  transform: translateY(30px) scale(0.95);
  transform-origin: bottom right;
  visibility: hidden;
  transition: opacity 0.3s ease-out, transform 0.3s ease-out, visibility 0.3s;
}

/* State khi mở */
.chat-window.open {
  opacity: 1;
  transform: translateY(0) scale(1);
  visibility: visible;
}
.chat-header {
  background-color: #e9ecef;
  padding: 0.8rem 1.2rem;
  border-bottom: 1px solid #dee2e6;
}

.chat-title { /* Có thể dùng .card-title */
  font-size: 1rem;
  font-weight: 600;
  color: #343a40;
}

.chat-close-btn {
  font-size: 0.75rem;
}

.message-area {
  overflow-y: auto;
  scroll-behavior: smooth;
  background-color: white;
  flex-grow: 1;
  min-height: 0;
}

/* Custom scrollbar */
.message-area::-webkit-scrollbar {
  width: 6px;
}

.message-area::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.message-area::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.message-area::-webkit-scrollbar-thumb:hover {
  background: #aaa;
}

.chat-message {
  display: flex;
  margin-bottom: 0.5rem;
}

.chat-message.user {
  justify-content: flex-end;
}

.chat-message.bot {
  justify-content: flex-start;
}

.message-bubble {
  padding: 0.7rem 1rem;
  border-radius: 1.1rem;
  max-width: 85%;
  word-wrap: break-word;
  font-size: 0.95rem;
  line-height: 1.5;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
}

.message-bubble :deep(p:last-child) {
  margin-bottom: 0;
}

/* Style cho list trong markdown */
.message-bubble :deep(ul), .message-bubble :deep(ol) {
  padding-left: 1.2rem;
  margin-bottom: 0.5rem;
}

.chat-message.user .message-bubble {
  background-color: var(--bs-primary);
  color: white;
  border-bottom-right-radius: 0.3rem;
}

.chat-message.bot .message-bubble {
  background-color: #ffffff;
  color: #212529;
  border: 1px solid #e9ecef;
  border-bottom-left-radius: 0.3rem;
}

/* Typing Indicator */
.typing-indicator span {
  display: inline-block;
  width: 8px;
  height: 8px;
  background-color: #adb5bd;
  border-radius: 50%;
  margin: 0 2px;
  animation: typing 1.2s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.15s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes typing {
  0%, 100% {
    opacity: 0.4;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1);
  }
}

/* Input Area */
.input-area {
  background-color: #f8f9fa;
  border-top: 1px solid #dee2e6;
}

.input-form {
}

.input-area .form-control {
  border-radius: 1.5rem 0 0 1.5rem !important;
  padding: 0.6rem 1rem;
  font-size: 0.95rem;
  border-color: #dee2e6;
  box-shadow: none !important;
}

.input-area .form-control:focus {
  border-color: var(--bs-primary);
}

.input-area .btn {
  border-radius: 0 1.5rem 1.5rem 0 !important;
  padding: 0.6rem 1rem;
  box-shadow: none !important;
}
.input-area .btn i {
  vertical-align: middle;
  font-size: 1.1rem;
}

</style>
