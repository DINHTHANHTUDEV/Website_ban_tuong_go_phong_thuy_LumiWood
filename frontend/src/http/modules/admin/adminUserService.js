// const USER_TIERS = ["BRONZE", "SILVER", "GOLD", "DIAMOND", "UNRANKED"];
// let mockUserIdCounter = 1000;

// const generateRandomString = (length) => {
//   const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
//   let result = '';
//   for (let i = 0; i < length; i++) {
//     result += characters.charAt(Math.floor(Math.random() * characters.length));
//   }
//   return result;
// };

// const generateFakeAdminUserListDTO = (idOverride, inputData = {}) => {
//   const id = idOverride || mockUserIdCounter++;
//   const firstName = ["Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona"][Math.floor(Math.random() * 6)];
//   const lastName = ["Smith", "Jones", "Williams", "Brown", "Davis", "Miller"][Math.floor(Math.random() * 6)];

//   const user = {
//     id: id,
//     fullName: inputData.fullName || `${firstName} ${lastName}`,
//     email: inputData.email || `${firstName.toLowerCase()}.${lastName.toLowerCase()}${Math.floor(Math.random() * 100)}@mockemail.com`,
//     tier: inputData.tier || USER_TIERS[Math.floor(Math.random() * USER_TIERS.length)],
//     totalSpent: inputData.totalSpent !== undefined ? inputData.totalSpent : parseFloat((Math.random() * 2000).toFixed(2)),
//     orderCount: inputData.orderCount !== undefined ? inputData.orderCount : Math.floor(Math.random() * 50),
//     isActive: inputData.isActive !== undefined ? inputData.isActive : Math.random() > 0.1,
//     joinedDate: inputData.joinedDate || new Date(Date.now() - Math.random() * 365 * 2 * 24 * 60 * 60 * 1000).toISOString(),
//   };
//   return { ...user, ...inputData };
// };

// const generateFakeAddress = () => {
//   return {
//     id: `addr_${generateRandomString(8)}`,
//     street: `${Math.floor(Math.random() * 900) + 100} Mockingbird Lane`,
//     city: ["Springfield", "Shelbyville", "Capital City"][Math.floor(Math.random() * 3)],
//     state: "MS",
//     zipCode: `${Math.floor(Math.random() * 90000) + 10000}`,
//     country: "Mocktropolis",
//     isDefaultShipping: Math.random() > 0.7,
//     isDefaultBilling: Math.random() > 0.7,
//   };
// };

// const generateFakeAdminUserDetailDTO = (userId, inputData = {}) => {
//   const listDto = generateFakeAdminUserListDTO(userId, inputData);
//   const detailDto = {
//     ...listDto,
//     phoneNumber: inputData.phoneNumber || `555-${Math.floor(Math.random() * 900) + 100}-${Math.floor(Math.random() * 9000) + 1000}`,
//     addresses: inputData.addresses || Array.from({ length: Math.floor(Math.random() * 3) + 1 }, generateFakeAddress),
//     lastLogin: inputData.lastLogin || new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
//     notes: inputData.notes || (Math.random() > 0.5 ? `Admin note: User ${userId} is a mock user.` : null),
//     recentOrders: inputData.recentOrders || Array.from({ length: Math.floor(Math.random() * 5) }, () => ({
//       id: `order_${generateRandomString(6)}`,
//       orderDate: new Date(Date.now() - Math.random() * 90 * 24 * 60 * 60 * 1000).toISOString(),
//       totalAmount: parseFloat((Math.random() * 300).toFixed(2)),
//       status: ["PENDING", "COMPLETED", "CANCELLED"][Math.floor(Math.random() * 3)],
//     })),
//   };
//   return { ...detailDto, ...inputData };
// };


// const simulateDelay = (callback, delayMs = 300 + Math.random() * 400) => {
//   return new Promise((resolve) => {
//     setTimeout(() => {
//       resolve(callback());
//     }, delayMs);
//   });
// };

// export const getAdminCustomers = (params = {}) => {
//   return simulateDelay(() => {
//     const pageSize = params.size || 10;
//     const pageNumber = params.page || 1;
//     let allMockUsers = Array.from({ length: 50 }, (_, i) => generateFakeAdminUserListDTO(1000 + i)); // Base set

//     if (params.keyword) {
//       const keywordLower = params.keyword.toLowerCase();
//       allMockUsers = allMockUsers.filter(user =>
//         user.fullName.toLowerCase().includes(keywordLower) ||
//         user.email.toLowerCase().includes(keywordLower)
//       );
//     }
//     if (params.tier) {
//       allMockUsers = allMockUsers.filter(user => user.tier === params.tier.toUpperCase());
//     }
//     if (params.isActive !== undefined && params.isActive !== null) {
//       allMockUsers = allMockUsers.filter(user => user.isActive === params.isActive);
//     }

//     const totalElements = allMockUsers.length;
//     const totalPages = Math.ceil(totalElements / pageSize);
//     const startIndex = (pageNumber - 1) * pageSize;
//     const paginatedUsers = allMockUsers.slice(startIndex, startIndex + pageSize);

//     return {
//       data: {
//         content: paginatedUsers,
//         pageable: {
//           pageNumber: pageNumber,
//           pageSize: pageSize,
//           sort: {
//             sorted: params.sort ? true : false,
//             unsorted: params.sort ? false : true,
//             empty: params.sort ? false : true,
//           },
//           offset: startIndex,
//           paged: true,
//           unpaged: false,
//         },
//         last: pageNumber >= totalPages,
//         totalPages: totalPages,
//         totalElements: totalElements,
//         size: pageSize,
//         number: pageNumber -1,
//         sort: {
//           sorted: params.sort ? true : false,
//           unsorted: params.sort ? false : true,
//           empty: params.sort ? false : true,
//         },
//         first: pageNumber === 1,
//         numberOfElements: paginatedUsers.length,
//         empty: paginatedUsers.length === 0,
//       }
//     };
//   });
// };

// export const getAdminCustomerDetail = (userId) => {
//   if (!userId) return Promise.reject(new Error("User ID is required"));
//   return simulateDelay(() => {
//     if (Math.random() < 0.05 && userId > 1010) { // Simulate not found for some higher IDs
//       return Promise.reject({ response: { status: 404, data: { message: "Mock User not found" } } });
//     }
//     return { data: generateFakeAdminUserDetailDTO(userId) };
//   });
// };

// export const updateAdminCustomerTier = (userId, newTier) => {
//   if (!userId) return Promise.reject(new Error("User ID is required"));
//   if (!newTier) return Promise.reject(new Error("New tier is required"));
//   if (!USER_TIERS.includes(newTier.toUpperCase())) {
//     return Promise.reject(new Error(`Invalid tier: ${newTier}. Valid tiers are: ${USER_TIERS.join(', ')}`));
//   }
//   return simulateDelay(() => {
//     const updatedUserListDTO = generateFakeAdminUserListDTO(userId, { tier: newTier.toUpperCase() });
//     return { data: updatedUserListDTO };
//   });
// };

// export const updateAdminCustomerStatus = (userId, isActive) => {
//   if (!userId) return Promise.reject(new Error("User ID is required"));
//   if (isActive === undefined || isActive === null)
//     return Promise.reject(new Error("Active status is required"));
//   return simulateDelay(() => {
//     const updatedUserListDTO = generateFakeAdminUserListDTO(userId, { isActive: isActive });
//     return { data: updatedUserListDTO };
//   });
// };
// adminUserService.js
import apiClient from '@/http/axios';

// Lấy danh sách khách hàng phân trang, có filter tier, status, search, page
export const getAdminCustomers = (params = {}) => {
  const apiParams = {
    page: params.page ?? 0,
    tier: (params.tier === null || params.tier === 'all' || params.tier === '') ? undefined : params.tier,
    status: (params.status === null || params.status === 'all' || params.status === '') 
      ? undefined 
      : params.status, // Gửi đúng giá trị 'active' hoặc 'locked'
    search: params.search || '',
  };

  Object.keys(apiParams).forEach(key => {
    if (apiParams[key] === undefined) {
      delete apiParams[key];
    }
  });

  return apiClient.get('/api/admin/users', { params: apiParams });
};


// Lấy chi tiết user theo userId
export const getAdminCustomerDetail = (userId) => {
  if (!userId) return Promise.reject(new Error('User ID is required'));
  return apiClient.get(`/api/admin/users/${userId}`);
};

// Cập nhật trạng thái hoạt động (isActive: boolean) của user
export const updateAdminCustomerStatus = (userId, isActive) => {
  if (!userId) return Promise.reject(new Error('User ID is required'));
  if (typeof isActive !== 'boolean') return Promise.reject(new Error('isActive must be boolean'));
  return apiClient.put(`/api/admin/users/${userId}/status`, { isActive });
};

// Lấy danh sách địa chỉ giao hàng của user
export const getUserAddresses = (userId) => {
  if (!userId) return Promise.reject(new Error('User ID is required'));
  return apiClient.get(`/api/admin/users/${userId}/addresses`);
};

// Lấy 5 đơn hàng gần nhất của user
export const getRecentOrders = (userId) => {
  if (!userId) return Promise.reject(new Error('User ID is required'));
  return apiClient.get(`/api/admin/users/${userId}/recent-orders`);
};

// Cập nhật bậc khách hàng (tier) với payload { tier: string }
export const updateAdminCustomerTier = (userId, tier) => {
  if (!userId) return Promise.reject(new Error('User ID is required'));
  if (!tier || typeof tier !== 'string') return Promise.reject(new Error('Tier must be a non-empty string'));
  return apiClient.put(`/api/admin/users/${userId}/tier`, { tier });
};
