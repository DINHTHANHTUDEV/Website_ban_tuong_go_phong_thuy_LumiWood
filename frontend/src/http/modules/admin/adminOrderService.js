const API_BASE = "http://localhost:8080/api/admin/orders";

// Lấy danh sách đơn hàng có phân trang, sort và lọc
export const getAllOrders = async (params = {}) => {
  const page = (params.page && params.page > 0) ? params.page - 1 : 0;
  const size = params.size || 10;
  const sort = params.sort ? `&sort=${encodeURIComponent(params.sort)}` : "";

  const keyword = params.keyword ? `&keyword=${encodeURIComponent(params.keyword)}` : "";
  const status = params.status ? `&status=${encodeURIComponent(params.status)}` : "";
  const startDate = params.startDate ? `&startDate=${encodeURIComponent(params.startDate)}` : "";
  const endDate = params.endDate ? `&endDate=${encodeURIComponent(params.endDate)}` : "";

  const url = `${API_BASE}?page=${page}&size=${size}${sort}${keyword}${status}${startDate}${endDate}`;

  try {
    const response = await fetch(url, { method: "GET" });
    if (!response.ok) {
      throw new Error(`Lỗi tải đơn hàng: ${response.statusText}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    return Promise.reject(error);
  }
};

// Lấy chi tiết đơn hàng theo id
export const getAdminOrderDetail = async (orderId) => {
  if (!orderId) return Promise.reject(new Error("Order ID is required"));
  try {
    const response = await fetch(`${API_BASE}/${orderId}`, { method: "GET" });
    if (!response.ok) {
      if (response.status === 404) throw new Error("Đơn hàng không tồn tại");
      throw new Error(`Lỗi tải chi tiết đơn hàng: ${response.statusText}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    return Promise.reject(error);
  }
};

// Cập nhật trạng thái đơn hàng (có hỗ trợ lý do hủy)
// Trả về dữ liệu đơn hàng mới sau cập nhật
export const updateOrderStatus = async (orderId, newStatus, cancelReason = null) => {
  if (!orderId) return Promise.reject(new Error("Order ID is required"));
  if (!newStatus) return Promise.reject(new Error("New status is required"));

  const body = { newStatus };
  if (cancelReason) {
    body.cancelReason = cancelReason;
  }

  try {
    const response = await fetch(`${API_BASE}/${orderId}/status`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Lỗi cập nhật trạng thái đơn hàng: ${errorText || response.statusText}`);
    }
    const updatedOrder = await response.json();  // Nhận dữ liệu đơn hàng mới sau cập nhật
    return updatedOrder;
  } catch (error) {
    return Promise.reject(error);
  }
};
