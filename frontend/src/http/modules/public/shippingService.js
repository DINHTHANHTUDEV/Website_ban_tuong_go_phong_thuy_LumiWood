const MOCK_ACTIVE_SHIPPING_METHODS_PHONGTHUY = [
  {
    id: "SHIP_PT_STANDARD_01",
    name: "Giao Hàng Tiêu Chuẩn - Bảo Vệ Tượng",
    description: "Vận chuyển đảm bảo, đóng gói chuyên nghiệp cho tượng gỗ. Thời gian dự kiến: 3-5 ngày làm việc.",
    cost: 35000, // VNĐ
    estimatedDeliveryTime: "3-5 ngày làm việc",
    logoUrl: "/assets/images/shipping/standard-logo.png", // Placeholder
  },
  {
    id: "SHIP_PT_EXPRESS_02",
    name: "Giao Hàng Nhanh - Hỏa Tốc Tượng Linh",
    description: "Ưu tiên vận chuyển, nhận hàng nhanh chóng sau 1-2 ngày. Phù hợp cho các đơn hàng cần gấp.",
    cost: 70000, // VNĐ
    estimatedDeliveryTime: "1-2 ngày làm việc",
    logoUrl: "/assets/images/shipping/express-logo.png", // Placeholder
  },
  {
    id: "SHIP_PT_FREE_OVER_1M_03",
    name: "Miễn Phí Vận Chuyển - Đơn Hàng Trên 1 Triệu",
    description: "Miễn phí giao hàng tiêu chuẩn cho các đơn hàng tượng gỗ phong thủy trị giá từ 1.000.000 VNĐ.",
    cost: 0, // Cost is 0 if eligible, actual cost might be e.g. 30000
    estimatedDeliveryTime: "3-5 ngày làm việc",
    eligibilityNote: "Áp dụng cho đơn hàng từ 1.000.000 VNĐ",
    logoUrl: "/assets/images/shipping/free-shipping-logo.png", // Placeholder
  },
  {
    id: "SHIP_PT_TIETKIEM_04",
    name: "Giao Hàng Tiết Kiệm - An Tâm",
    description: "Giải pháp vận chuyển kinh tế, thời gian giao hàng có thể lâu hơn một chút (4-7 ngày).",
    cost: 25000, // VNĐ
    estimatedDeliveryTime: "4-7 ngày làm việc",
    logoUrl: "/assets/images/shipping/tiet-kiem-logo.png", // Placeholder
  },
];

const simulateDelay = (dataFn, delayMs = 150 + Math.random() * 200) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      try {
        const result = dataFn();
        resolve({ data: result });
      } catch (error) {
        resolve({ data: { error: error.message } });
      }
    }, delayMs);
  });
};

export const getActiveShippingMethods = () => {
  return simulateDelay(() => {
    return MOCK_ACTIVE_SHIPPING_METHODS_PHONGTHUY.map(method => ({ ...method }));
  });
};
