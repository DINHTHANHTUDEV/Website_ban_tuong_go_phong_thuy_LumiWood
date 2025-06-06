// let mockUserProfile = {
//   id: "USR_PT_MOCK_001",
//   email: "khachhang.phongthuy@email.mock",
//   fullName: "Nguyễn Văn An Lạc",
//   phoneNumber: "0987654321",
//   avatarUrl: `https://i.pravatar.cc/150?u=phongthuyuser${Date.now()}`,
//   tier: "BẠCH KIM",
//   dateJoined: new Date(Date.now() - Math.random() * 365 * 3 * 24 * 60 * 60 * 1000).toISOString(), // Joined within last 3 years
//   defaultShippingAddressId: "ADDR_PT_MOCK_SHIP_01",
//   defaultBillingAddressId: "ADDR_PT_MOCK_BILL_01",
//   loyaltyPoints: Math.floor(Math.random() * 5000) + 1000,
// };

// let mockUserAddresses = [
//   {
//     id: "ADDR_PT_MOCK_SHIP_01",
//     recipientName: "Nguyễn Văn An Lạc",
//     phoneNumber: "0987654321",
//     street: "123 Đường Tượng Gỗ May Mắn, Khu Phố An Khang",
//     ward: "Phường Thịnh Vượng",
//     district: "Quận Bình An",
//     city: "TP. Hồ Chí Minh",
//     country: "Việt Nam",
//     isDefaultShipping: true,
//     isDefaultBilling: false,
//     type: "SHIPPING",
//     addressNotes: "Giao hàng giờ hành chính."
//   },
//   {
//     id: "ADDR_PT_MOCK_BILL_01",
//     recipientName: "Nguyễn Văn An Lạc",
//     phoneNumber: "0987654321",
//     street: "Sảnh A, Tòa Nhà Tài Lộc, Số 8 Đường Phát Đạt",
//     ward: "Phường Phú Quý",
//     district: "Quận Giàu Sang",
//     city: "TP. Hà Nội",
//     country: "Việt Nam",
//     isDefaultShipping: false,
//     isDefaultBilling: true,
//     type: "BILLING",
//   },
//   {
//     id: "ADDR_PT_MOCK_OTHER_02",
//     recipientName: "Trần Thị Hạnh Phúc (Vợ)",
//     phoneNumber: "0912345678",
//     street: "Biệt Thự Sen Vàng, Lô B2, Đường Hoa Cúc",
//     ward: "Phường Cát Tường",
//     district: "Quận Như Ý",
//     city: "TP. Đà Nẵng",
//     country: "Việt Nam",
//     isDefaultShipping: false,
//     isDefaultBilling: false,
//     type: "OTHER",
//   }
// ];
// let mockAddressIdCounter = 3;
// const MOCK_CURRENT_PASSWORD = "MatKhauCuPhongThuy123!";

// const simulateDelay = (dataFn, delayMs = 200 + Math.random() * 200) => {
//   return new Promise((resolve, reject) => {
//     setTimeout(() => {
//       try {
//         const result = dataFn();
//         resolve({ data: result });
//       } catch (error) {
//         reject(error);
//       }
//     }, delayMs);
//   });
// };


// export const getUserProfile = () => {
//   return simulateDelay(() => {
//     return { ...mockUserProfile };
//   });
// };

// export const updateUserProfile = (profileData) => {
//   if (!profileData) {
//     return Promise.reject(new Error("Dữ liệu cập nhật không được để trống."));
//   }
//   return simulateDelay(() => {
//     mockUserProfile = {
//       ...mockUserProfile,
//       ...profileData,
//       avatarUrl: profileData.avatarUrl || mockUserProfile.avatarUrl,
//       updatedAt: new Date().toISOString()
//     };
//     return { ...mockUserProfile };
//   });
// };

// export const changeUserPassword = (passwordData) => {
//   if (!passwordData || !passwordData.currentPassword || !passwordData.newPassword) {
//     return Promise.reject(new Error("Vui lòng cung cấp mật khẩu hiện tại và mật khẩu mới."));
//   }
//   if (passwordData.newPassword.length < 6) {
//     return Promise.reject(new Error("Mật khẩu mới phải có ít nhất 6 ký tự."));
//   }

//   return simulateDelay(() => {
//     if (passwordData.currentPassword !== MOCK_CURRENT_PASSWORD) {
//       const error = new Error("Mật khẩu hiện tại không chính xác.");
//       error.response = { status: 400, data: { message: "Mật khẩu hiện tại không chính xác." } };
//       throw error;
//     }
//     return { message: "Thay đổi mật khẩu thành công! (Mocked)" };
//   }, 500);
// };

// export const getUserAddresses = () => {
//   return simulateDelay(() => {
//     return [...mockUserAddresses.map(addr => ({...addr}))];
//   });
// };

// export const addUserAddress = (addressData) => {
//   if (!addressData || !addressData.recipientName || !addressData.street || !addressData.city) {
//     return Promise.reject(new Error("Tên người nhận, đường, thành phố là bắt buộc."));
//   }
//   return simulateDelay(() => {
//     const newId = `ADDR_PT_MOCK_${(addressData.type || 'NEW').toUpperCase()}_0${mockAddressIdCounter++}`;
//     const newAddress = {
//       id: newId,
//       country: "Việt Nam",
//       isDefaultShipping: false,
//       isDefaultBilling: false,
//       ...addressData,
//     };
//     if (newAddress.isDefaultShipping) {
//       mockUserAddresses.forEach(addr => addr.isDefaultShipping = false);
//     }
//     if (newAddress.isDefaultBilling) {
//       mockUserAddresses.forEach(addr => addr.isDefaultBilling = false);
//     }
//     mockUserAddresses.push(newAddress);
//     return { ...newAddress };
//   });
// };

// export const updateUserAddress = (addressId, addressData) => {
//   if (!addressId) return Promise.reject(new Error("ID địa chỉ là bắt buộc để cập nhật."));
//   if (!addressData) return Promise.reject(new Error("Dữ liệu cập nhật không được để trống."));

//   return simulateDelay(() => {
//     const addressIndex = mockUserAddresses.findIndex(addr => addr.id === addressId);
//     if (addressIndex === -1) {
//       const error = new Error("Không tìm thấy địa chỉ để cập nhật.");
//       error.response = { status: 404, data: { message: "Không tìm thấy địa chỉ để cập nhật." } };
//       throw error;
//     }
//     if (addressData.isDefaultShipping) {
//       mockUserAddresses.forEach(addr => addr.isDefaultShipping = false);
//     }
//     if (addressData.isDefaultBilling) {
//       mockUserAddresses.forEach(addr => addr.isDefaultBilling = false);
//     }

//     mockUserAddresses[addressIndex] = {
//       ...mockUserAddresses[addressIndex],
//       ...addressData,
//     };
//     return { ...mockUserAddresses[addressIndex] };
//   });
// };

// export const deleteUserAddress = (addressId) => {
//   if (!addressId) return Promise.reject(new Error("ID địa chỉ là bắt buộc để xóa."));
//   return simulateDelay(() => {
//     const initialLength = mockUserAddresses.length;
//     mockUserAddresses = mockUserAddresses.filter(addr => addr.id !== addressId);
//     if (mockUserAddresses.length === initialLength) {
//       const error = new Error("Không tìm thấy địa chỉ để xóa.");
//       error.response = { status: 404, data: { message: "Không tìm thấy địa chỉ để xóa." } };
//       throw error;
//     }
//     return { message: `Địa chỉ ${addressId} đã được xóa (mocked).` };
//   });
// };

// export const setDefaultAddress = (addressId, type) => {
//   if (!addressId || !type) return Promise.reject(new Error("ID địa chỉ và loại (shipping/billing) là bắt buộc."));
//   if (type.toUpperCase() !== 'SHIPPING' && type.toUpperCase() !== 'BILLING') {
//     return Promise.reject(new Error("Loại địa chỉ không hợp lệ. Phải là 'shipping' hoặc 'billing'."));
//   }

//   return simulateDelay(() => {
//     const address = mockUserAddresses.find(addr => addr.id === addressId);
//     if (!address) {
//       const error = new Error("Không tìm thấy địa chỉ để đặt làm mặc định.");
//       error.response = { status: 404, data: { message: "Không tìm thấy địa chỉ." } };
//       throw error;
//     }

//     if (type.toUpperCase() === 'SHIPPING') {
//       mockUserAddresses.forEach(addr => addr.isDefaultShipping = (addr.id === addressId));
//       mockUserProfile.defaultShippingAddressId = addressId;
//     } else { // BILLING
//       mockUserAddresses.forEach(addr => addr.isDefaultBilling = (addr.id === addressId));
//       mockUserProfile.defaultBillingAddressId = addressId;
//     }
//     return { ...address, isDefaultShipping: type.toUpperCase() === 'SHIPPING', isDefaultBilling: type.toUpperCase() === 'BILLING' };
//   });
// };
// src/http/modules/public/userService.js

import apiClient from '@/http/axios';

// Lấy thông tin tài khoản
export const getUserProfile = () => {
  return apiClient.get('/api/customer/my-profile');
};

// Cập nhật thông tin tài khoản (payload ví dụ: { fullName: ... })
export const updateUserProfile = (payload) => {
  return apiClient.put('/api/customer/my-profile', payload);
};

// Đổi mật khẩu (payload: { currentPassword, newPassword, confirmNewPassword })
export const changeUserPassword = (payload) => {
  // Nếu backend yêu cầu oldPassword thay vì currentPassword thì đổi tên key cho đúng
  // return apiClient.put('/api/customer/my-profile/change-password', { ...payload, oldPassword: payload.currentPassword });
  return apiClient.put('/api/customer/my-profile/change-password', payload);
};

// Lấy 5 đơn hàng gần nhất cho user đang đăng nhập
export const getRecentOrders  = () => {
  // Đảm bảo endpoint đúng như backend Spring Boot
  return apiClient.get('/api/customer/my-profile/recent-orders');
};
