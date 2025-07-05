// const MOCK_PROMOTIONS_PHONGTHUY = [
//   {
//     code: "ANLACVUIMUA",
//     name: "An Lạc Vui Mua - Giảm Giá Khai Trương",
//     description: "Giảm 10% cho tất cả các sản phẩm tượng gỗ phong thủy. Chào mừng quý khách đến với cửa hàng!",
//     discountType: "PERCENTAGE",
//     discountValue: 10,
//     minimumSpend: 500000, // 500k VNĐ
//     messageSuccess: "Chúc mừng! Bạn được giảm 10% cho đơn hàng."
//   },
//   {
//     code: "MAYMAN88",
//     name: "Tài Lộc May Mắn - Rước Tượng Phật Di Lặc",
//     description: "Giảm ngay 88.000 VNĐ khi mua Tượng Phật Di Lặc bằng gỗ Hương.",
//     discountType: "FIXED_AMOUNT",
//     discountValue: 88000,
//     targetProductNamesContain: ["di lặc", "dilac"], // Áp dụng nếu tên sản phẩm trong giỏ hàng chứa từ này
//     messageSuccess: "Tuyệt vời! Mã MAYMAN88 đã giảm 88.000 VNĐ cho Tượng Phật Di Lặc của bạn."
//   },
//   {
//     code: "LINHVAT2024",
//     name: "Linh Vật Hộ Mệnh 2024",
//     description: "Giảm 150.000 VNĐ cho đơn hàng từ 2.000.000 VNĐ có sản phẩm Tỳ Hưu hoặc Thiềm Thừ.",
//     discountType: "FIXED_AMOUNT",
//     discountValue: 150000,
//     minimumSpend: 2000000,
//     targetProductNamesContain: ["tỳ hưu", "ty huu", "thiềm thừ", "thiem thu", "cóc ba chân"],
//     messageSuccess: "Linh vật hộ mệnh! Giảm 150.000 VNĐ cho đơn hàng của bạn."
//   },
//   {
//     code: "FREESHIPMAX",
//     name: "Miễn Phí Vận Chuyển Toàn Quốc",
//     description: "Miễn phí vận chuyển cho mọi đơn hàng tượng gỗ từ 1.000.000 VNĐ.",
//     discountType: "FREE_SHIPPING",
//     discountValue: 0, // discountValue không dùng cho free_shipping, nhưng có thể để chi phí ship tối đa được miễn
//     minimumSpend: 1000000,
//     simulatedShippingCost: 50000, // Giả lập chi phí ship sẽ được miễn
//     messageSuccess: "An tâm mua sắm! Đơn hàng của bạn được miễn phí vận chuyển."
//   },
//   {
//     code: "TUONGDEPTET",
//     name: "Tượng Đẹp Đón Tết - Ưu Đãi Đặc Biệt",
//     description: "Giảm 20% (tối đa 500.000 VNĐ) cho các sản phẩm Tượng Tam Đa, Tượng Quan Công.",
//     discountType: "PERCENTAGE_MAX_CAP",
//     discountValue: 20, // %
//     maxCap: 500000, // VNĐ
//     targetProductNamesContain: ["tam đa", "quan công", "quancong"],
//     messageSuccess: "Tết an khang! Bạn được giảm giá đặc biệt cho Tượng Đẹp Đón Tết."
//   },
// ];

// const simulateDelay = (dataFn, delayMs = 200 + Math.random() * 300) => {
//   return new Promise((resolve, reject) => {
//     setTimeout(() => {
//       try {
//         const result = dataFn();
//         resolve({ data: result });
//       } catch (error)
//       {
//         reject(error);
//       }
//     }, delayMs);
//   });
// };

// export const applyPromotionCode = (code) => {
//   if (!code || typeof code !== 'string' || code.trim() === '') {
//     return Promise.reject(new Error("Mã khuyến mãi không được để trống"));
//   }

//   const normalizedCode = code.trim().toUpperCase();

//   return simulateDelay(() => {
//     const promotion = MOCK_PROMOTIONS_PHONGTHUY.find(p => p.code.toUpperCase() === normalizedCode);

//     const mockCartTotal = parseFloat((Math.random() * 3000000 + 700000).toFixed(0));
//     const mockCartItemsForPromoCheck = [
//       { productName: "Tượng Phật Di Lặc Gỗ Bách Xanh Mini", quantity: 1, price: 850000 },
//       { productName: "Tượng Tỳ Hưu Phong Thủy Gỗ Mun Đen", quantity: 1, price: 1200000 },
//       { productName: "Vòng Tay Trầm Hương 108 Hạt", quantity: 1, price: 650000 },
//     ]; // Giả lập sản phẩm trong giỏ hàng để check targetProductNamesContain

//     if (!promotion) {
//       // Ném lỗi để khớp với Promise.reject, hoặc trả về cấu trúc lỗi cụ thể nếu API thật làm vậy
//       // Ví dụ trả về lỗi 404 Not Found hoặc 400 Bad Request
//       const error = new Error("Mã khuyến mãi không hợp lệ hoặc đã hết hạn.");
//       error.response = { status: 404, data: { message: "Mã khuyến mãi không hợp lệ hoặc đã hết hạn." } };
//       throw error;
//     }

//     let appliedPromotionDTO = {
//       code: promotion.code,
//       message: "",
//       description: promotion.description,
//       discountAmount: 0,
//       originalCartTotal: mockCartTotal,
//       finalCartTotal: mockCartTotal,
//       applied: false,
//     };

//     if (promotion.minimumSpend && mockCartTotal < promotion.minimumSpend) {
//       appliedPromotionDTO.message = `Rất tiếc, mã '${promotion.code}' chỉ áp dụng cho đơn hàng từ ${promotion.minimumSpend.toLocaleString('vi-VN')} VNĐ. Đơn hàng của bạn hiện là ${mockCartTotal.toLocaleString('vi-VN')} VNĐ.`;
//       return appliedPromotionDTO;
//     }

//     if (promotion.targetProductNamesContain && promotion.targetProductNamesContain.length > 0) {
//       const hasTargetProduct = mockCartItemsForPromoCheck.some(item =>
//         promotion.targetProductNamesContain.some(keyword => item.productName.toLowerCase().includes(keyword.toLowerCase()))
//       );
//       if (!hasTargetProduct) {
//         appliedPromotionDTO.message = `Mã '${promotion.code}' chỉ áp dụng cho các sản phẩm cụ thể. Vui lòng kiểm tra lại giỏ hàng.`;
//         return appliedPromotionDTO;
//       }
//     }


//     let discount = 0;
//     switch (promotion.discountType) {
//       case "PERCENTAGE":
//         discount = mockCartTotal * (promotion.discountValue / 100);
//         break;
//       case "FIXED_AMOUNT":
//         discount = promotion.discountValue;
//         break;
//       case "FREE_SHIPPING":
//         discount = promotion.simulatedShippingCost || 50000; // Giả lập phí ship được miễn
//         // Trong DTO thực tế, có thể có trường riêng cho free_shipping
//         break;
//       case "PERCENTAGE_MAX_CAP":
//         discount = mockCartTotal * (promotion.discountValue / 100);
//         if (promotion.maxCap && discount > promotion.maxCap) {
//           discount = promotion.maxCap;
//         }
//         break;
//       default:
//         discount = 0;
//     }
//     discount = Math.round(discount); // Làm tròn số tiền giảm


//     if (discount > 0) {
//       appliedPromotionDTO.discountAmount = discount;
//       appliedPromotionDTO.finalCartTotal = Math.max(0, mockCartTotal - discount); // Đảm bảo không âm
//       appliedPromotionDTO.message = promotion.messageSuccess || `Mã '${promotion.code}' đã được áp dụng! Bạn được giảm ${discount.toLocaleString('vi-VN')} VNĐ.`;
//       appliedPromotionDTO.applied = true;
//     } else if (promotion.discountType !== "FREE_SHIPPING") { // Nếu ko phải free_shipping mà discount = 0 thì có gì đó sai
//       appliedPromotionDTO.message = `Không thể áp dụng mã '${promotion.code}' vào thời điểm này.`;
//     }


//     return appliedPromotionDTO;
//   });
// };
import axios from "axios";

export async function availablePromotions() {
  const token = localStorage.getItem("token"); // hoặc từ Vuex/Pinia nếu bạn dùng

  return axios.get("http://localhost:8080/api/promotion/hienthi", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}


export function applyPromotionCode(code, subtotal) {
  return axios.post("http://localhost:8080/api/promotion/apply", null, {
    params: { code: code,
      subtotal: subtotal }
  });
}