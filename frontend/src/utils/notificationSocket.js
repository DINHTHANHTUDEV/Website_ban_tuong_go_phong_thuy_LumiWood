import { Client } from '@stomp/stompjs';

let stompClient = null;

/**
 * ✅ Kết nối WebSocket và đăng ký nhận thông báo theo username và quyền admin
 * @param {function} onMessageReceived - Callback khi nhận được thông báo
 */
export function connectNotificationSocket(onMessageReceived) {
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user"));

  // 🔒 Nếu thiếu token hoặc username thì không kết nối
  if (!token || !user?.username) {
    console.warn("⚠️ Không có token hoặc username. Không thể kết nối WebSocket.");
    return;
  }

  const username = user.username;
  const isAdmin = user.role === "ROLE_ADMIN";

  // ✅ Tạo STOMP client sử dụng WebSocket
  stompClient = new Client({
    brokerURL: `ws://localhost:8080/ws?token=${token}`, // Gửi JWT token qua query param
    reconnectDelay: 5000,
    debug: (str) => console.log("[STOMP]", str),

    // Khi kết nối thành công
    onConnect: () => {
      console.log("✅ WebSocket kết nối thành công với username:", username);

      // 🔔 Đăng ký kênh nhận thông báo riêng của user (dựa vào Spring convertAndSendToUser)
      const userQueue = `/user/queue/notify`;
      console.log(`📩 Đăng ký nhận thông báo người dùng tại: ${userQueue}`);

      stompClient.subscribe(userQueue, (message) => {
        const notification = JSON.parse(message.body);
        console.log("📨 Thông báo mới từ server (user):", notification);
        onMessageReceived(notification);
      });

      // 🛠️ Nếu là admin thì đăng ký thêm kênh /topic/notify/admin
      if (isAdmin) {
        const adminTopic = `/topic/notify/admin`;
        console.log(`🛠️ Đăng ký nhận thông báo admin tại: ${adminTopic}`);

        stompClient.subscribe(adminTopic, (message) => {
          const notification = JSON.parse(message.body);
          console.log("📢 Thông báo mới từ server (admin):", notification);
          onMessageReceived(notification);
        });
      }
    },

    // Khi gặp lỗi kết nối hoặc lỗi STOMP
    onStompError: (frame) => {
      console.error("❌ STOMP Error:", frame);
    },
  });

  // 🔌 Kích hoạt kết nối
  stompClient.activate();
}

/**
 * ✅ Ngắt kết nối WebSocket (nếu đang kết nối)
 */
export function disconnectNotificationSocket() {
  if (stompClient) {
    stompClient.deactivate();
    console.log("❌ WebSocket đã ngắt kết nối.");
  }
}
