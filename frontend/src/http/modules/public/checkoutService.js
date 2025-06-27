// // Mảng chứa các phương thức vận chuyển giả lập (mock) cho quy trình thanh toán
// const MOCK_SHIPPING_METHODS_CHECKOUT = [
//   // Phương thức vận chuyển tiêu chuẩn: giao hàng trong 3-5 ngày, chi phí 5 USD
//   { id: "ship_std_mock", name: "Standard Mock Shipping", description: "Delivers in 3-5 mock days", cost: 5.00, estimatedDeliveryTime: "3-5 business days" },
//   // Phương thức vận chuyển nhanh: giao hàng trong 1-2 ngày, chi phí 15 USD
//   { id: "ship_exp_mock", name: "Express Mock Delivery", description: "Delivers in 1-2 mock days", cost: 15.00, estimatedDeliveryTime: "1-2 business days" },
//   // Phương thức vận chuyển miễn phí (cho đơn hàng trên 50 USD): giao hàng trong 5-7 ngày
//   { id: "ship_free_mock", name: "Free Mock Shipping (Orders > $50)", description: "Delivers in 5-7 mock days", cost: 0.00, estimatedDeliveryTime: "5-7 business days" },
// ];

// Mảng chứa các phương thức thanh toán giả lập (mock) cho quy trình thanh toán
// const MOCK_PAYMENT_METHODS_CHECKOUT = [
//   // Thanh toán khi nhận hàng (COD)
//   { id: "pay_cod_mock", name: "Cash on Delivery (Mock)", description: "Pay when you receive the order." },
//   // Thanh toán bằng thẻ tín dụng/thẻ ghi nợ
//   { id: "pay_card_mock", name: "Credit/Debit Card (Mock)", description: "Secure online payment." },
//   // Thanh toán bằng ví MockPay
//   { id: "pay_wallet_mock", name: "MockPay Wallet", description: "Use your MockPay balance." },
// ];

// // Hàm tạo địa chỉ giả lập với thông tin ngẫu nhiên
// const generateMockAddress = (id, isDefault = false) => {
//   // Danh sách tên và họ để tạo tên người nhận ngẫu nhiên
//   const firstNames = ["Alex", "Jamie", "Casey", "Robin", "Jordan"];
//   const lastNames = ["Smith", "Doe", "Patel", "Kim", "Garcia"];
  
//   return {
//     // ID địa chỉ dạng chuỗi với tiền tố "addr_mock_" và số id
//     id: `addr_mock_${id}`,
//     // Tên người nhận được tạo ngẫu nhiên bằng cách kết hợp firstName và lastName
//     recipientName: `${firstNames[Math.floor(Math.random() * firstNames.length)]} ${lastNames[Math.floor(Math.random() * lastNames.length)]}`,
//     // Số điện thoại giả lập, bắt đầu bằng "555-01" và thêm 2 số ngẫu nhiên
//     phoneNumber: `555-01${String(Math.floor(Math.random() * 90) + 10).padStart(2, '0')}`,
//     // Địa chỉ đường phố ngẫu nhiên, ví dụ: "123 Mockingbird Ave, Apt 5"
//     street: `${Math.floor(Math.random() * 900) + 100} Mockingbird Ave, Apt ${Math.floor(Math.random() * 20) + 1}`,
//     // Phường/xã ngẫu nhiên, ví dụ: "Ward A"
//     ward: `Ward ${String.fromCharCode(65 + Math.floor(Math.random() * 10))}`,
//     // Quận/huyện ngẫu nhiên, ví dụ: "District 5"
//     district: `District ${Math.floor(Math.random() * 10) + 1}`,
//     // Thành phố ngẫu nhiên từ danh sách: MockCity, Faketown, Simville
//     city: ["MockCity", "Faketown", "Simville"][Math.floor(Math.random() * 3)],
//     // Quốc gia cố định là Mockland
//     country: "Mockland",
//     // Xác định xem đây có phải là địa chỉ giao hàng mặc định hay không (ngẫu nhiên với xác suất 70% nếu isDefault = true)
//     isDefaultShipping: isDefault && Math.random() > 0.3,
//     // Xác định xem đây có phải là địa chỉ thanh toán mặc định hay không (ngẫu nhiên với xác suất 70% nếu isDefault = true)
//     isDefaultBilling: isDefault && Math.random() > 0.3,
//   };
// };

// Hàm tạo thông tin giỏ hàng giả lập
const generateMockCartSummary = () => {
  // Tạo tổng giá trị đơn hàng ngẫu nhiên từ 20 đến 220 USD, làm tròn 2 chữ số thập phân
  const subtotal = parseFloat((Math.random() * 200 + 20).toFixed(2));
  // Áp dụng giảm giá 10% nếu tổng giá trị trên 100 USD, nếu không thì giảm giá bằng 0
  const discount = subtotal > 100 ? parseFloat((subtotal * 0.1).toFixed(2)) : 0;
  // Phí vận chuyển miễn phí nếu tổng giá trị trên 50 USD, nếu không thì tính 5 USD
  const shipping = subtotal > 50 ? 0 : 5.00;
  
  return {
    // Số lượng sản phẩm trong giỏ hàng, ngẫu nhiên từ 1 đến 5
    totalItems: Math.floor(Math.random() * 5) + 1,
    // Tổng giá trị trước giảm giá
    subtotalPrice: subtotal,
    // Số tiền giảm giá
    discountAmount: discount,
    // Phí vận chuyển
    shippingCost: shipping,
    // Tổng giá trị cuối cùng sau khi trừ giảm giá và cộng phí vận chuyển
    finalPrice: parseFloat((subtotal - discount + shipping).toFixed(2)),
    // Đơn vị tiền tệ là USD
    currency: "USD",
    // Danh sách sản phẩm trong giỏ hàng, ngẫu nhiên từ 1 đến 3 sản phẩm
    items: Array.from({length: Math.floor(Math.random() * 3) + 1}, (_, i) => ({
      // ID sản phẩm bắt đầu từ 100
      productId: 100 + i,
      // Tên sản phẩm, ví dụ: "Mock Product A"
      productName: `Mock Product ${String.fromCharCode(65 + i)}`,
      // Số lượng sản phẩm, ngẫu nhiên từ 1 đến 2
      quantity: Math.floor(Math.random() * 2) + 1,
      // Đơn giá sản phẩm, ngẫu nhiên từ 10 đến 60 USD
      unitPrice: parseFloat((Math.random() * 50 + 10).toFixed(2)),
      // URL hình ảnh sản phẩm, sử dụng picsum.photos với seed cố định
      imageUrl: `https://picsum.photos/seed/cartProd${i}/50`
    }))
  };
};

// Hàm mô phỏng độ trễ (delay) để giả lập việc gọi API
const simulateDelay = (dataFn, delayMs = 250 + Math.random() * 350) => {
  // Trả về một Promise để xử lý bất đồng bộ
  return new Promise((resolve) => {
    // Tạo độ trễ ngẫu nhiên từ 250ms đến 600ms
    setTimeout(() => {
      try {
        // Gọi hàm dataFn để lấy dữ liệu
        const result = dataFn();
        // Trả về dữ liệu thành công
        resolve({ data: result });
      } catch (error) {
        // Trả về lỗi nếu có vấn đề trong quá trình thực thi
        resolve({ data: { error: error.message } });
      }
    }, delayMs);
  });
};

export const getCheckoutInfo = () => {
  // Gọi API để lấy danh sách địa chỉ của người dùng đã đăng nhập
  // Endpoint: GET /api/customer/checkout/addresses (sử dụng token để xác định userId)
  return apiClient
    .get('/api/customer/checkout/addresses')
    .then(response => {
      // Kiểm tra nếu response.data không phải mảng hoặc không tồn tại
      if (!Array.isArray(response.data)) {
        throw new Error('Dữ liệu địa chỉ từ API không hợp lệ');
      }

      // Lấy dữ liệu địa chỉ từ API
      const savedAddresses = response.data.map(address => ({
        id: address.id,
        recipientName: address.recipientName,
        recipientPhone: address.recipientPhone,
        streetAddress: address.streetAddress,
        ward: address.ward || '',
        district: address.district,
        city: address.city,
        isDefaultShipping: address.isDefaultShipping || false,
        // Loại bỏ isDefaultBilling nếu API không hỗ trợ (dựa trên dữ liệu mẫu)
      }));

      // Tìm địa chỉ giao hàng mặc định
      const defaultShippingAddress = savedAddresses.find(addr => addr.isDefaultShipping) || null;

      // Trả về thông tin thanh toán
      return {
        savedAddresses: savedAddresses,
        availableShippingMethods: [], // Sẽ được cập nhật từ fetchShippingMethods
        availablePaymentMethods: [], // FE đã xử lý phương thức thanh toán
        cartSummary: { totalItems: 0, subtotalPrice: 0, discountAmount: 0, shippingCost: 0, finalPrice: 0, currency: 'VND', items: [] }, // FE sẽ tính toán
        userTier: null, // Không cần thiết nếu FE không sử dụng
        defaultShippingAddress: defaultShippingAddress,
      };
    })
    .catch(error => {
      // Xử lý lỗi từ API (ví dụ: 401, 500) hoặc dữ liệu không hợp lệ
      console.error('Error fetching checkout info:', error);
      throw error; // Ném lỗi để FE xử lý (ví dụ: hiển thị thông báo)
    });
};

// Hàm đặt hàng (place order)
export const placeOrder = (orderData) => {
  // Kiểm tra xem dữ liệu đơn hàng có hợp lệ không (cần có địa chỉ giao hàng và phương thức thanh toán)
  if (!orderData || !orderData.shippingAddressId || !orderData.paymentMethodId) {
    return Promise.reject(new Error("Shipping address, and payment method are required for mock order."));
  }
  
  // Sử dụng simulateDelay để giả lập độ trễ khi đặt hàng
  return simulateDelay(() => {
    // Tạo ID đơn hàng giả lập với định dạng MOCKORD-thời gian hiện tại-số ngẫu nhiên
    const orderId = `MOCKORD-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
    // Tạo thông tin giỏ hàng giả lập
    const cartInfo = generateMockCartSummary();
    // Tìm phương thức vận chuyển dựa trên ID, nếu không tìm thấy thì dùng phương thức đầu tiên
    const shippingMethod = MOCK_SHIPPING_METHODS_CHECKOUT.find(sm => sm.id === orderData.shippingMethodId) || MOCK_SHIPPING_METHODS_CHECKOUT[0];
    // Tìm phương thức thanh toán dựa trên ID, nếu không tìm thấy thì dùng phương thức đầu tiên
    const paymentMethod = MOCK_PAYMENT_METHODS_CHECKOUT.find(pm => pm.id === orderData.paymentMethodId) || MOCK_PAYMENT_METHODS_CHECKOUT[0];

    // Xác định thời gian giao hàng ước tính dựa trên phương thức vận chuyển
    let estimatedDeliveryDays = 5;
    if (shippingMethod.name.toLowerCase().includes("express")) estimatedDeliveryDays = 2;
    else if (shippingMethod.name.toLowerCase().includes("standard")) estimatedDeliveryDays = 4;

    // Tạo ngày đặt hàng và ngày giao hàng ước tính
    const orderDate = new Date();
    const estimatedDeliveryDate = new Date(orderDate);
    estimatedDeliveryDate.setDate(orderDate.getDate() + estimatedDeliveryDays);

    // Trả về thông tin đơn hàng chi tiết
    return {
      // ID đơn hàng
      orderId: orderId,
      // Ngày đặt hàng (định dạng ISO)
      orderDate: orderDate.toISOString(),
      // Trạng thái đơn hàng ban đầu
      status: "PENDING_CONFIRMATION_MOCK",
      // Tổng số tiền của đơn hàng
      totalAmount: cartInfo.finalPrice,
      // Địa chỉ giao hàng (sử dụng địa chỉ được chọn hoặc tạo mới)
      shippingAddress: orderData.selectedShippingAddress || generateMockAddress("final_ship"),
      // Địa chỉ thanh toán (sử dụng địa chỉ được chọn hoặc tạo mới)
      billingAddress: orderData.selectedBillingAddress || generateMockAddress("final_bill"),
      // Tên phương thức vận chuyển
      shippingMethodName: shippingMethod.name,
      // Tên phương thức thanh toán
      paymentMethodName: paymentMethod.name,
      // Danh sách sản phẩm trong đơn hàng
      items: cartInfo.items.map(item => ({
        productId: item.productId,
        productName: item.productName,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        // Tổng giá trị của từng sản phẩm (số lượng * đơn giá)
        itemTotalPrice: parseFloat((item.quantity * item.unitPrice).toFixed(2))
      })),
      // Tổng giá trị trước giảm giá
      subtotal: cartInfo.subtotalPrice,
      // Số tiền giảm giá
      discountApplied: cartInfo.discountAmount,
      // Phí vận chuyển
      shippingFee: cartInfo.shippingCost,
      // Ngày giao hàng ước tính (định dạng ISO)
      estimatedDeliveryDate: estimatedDeliveryDate.toISOString(),
      // Số theo dõi đơn hàng (hiện tại để null vì là giả lập)
      trackingNumber: null,
      // Ghi chú của khách hàng (nếu có)
      customerNotes: orderData.notes || null
    };
  });
};

import apiClient from '@/http/axios';

// // Lấy thông tin tài khoản (bao gồm danh sách địa chỉ)
// export const getCheckoutInfo = () => {
//   // Gọi API để lấy danh sách địa chỉ của người dùng
//   // Endpoint: GET /api/customer/checkout/addresses
//   return apiClient.get('/api/customer/checkout/addresses');
// };

// // Lấy danh sách phương thức vận chuyển đang hoạt động
// export const getActiveShippingMethods = () => {
//   // Gọi API để lấy danh sách phương thức vận chuyển
//   // Endpoint: GET /api/customer/checkout/shipping-methods
//   return apiClient.get('/api/customer/checkout/shipping-methods');
// };

// Thêm địa chỉ giao hàng mới
export const addAddress = (addressData) => {
  // Gọi API để thêm địa chỉ mới
  // Endpoint: POST /api/customer/checkout/add-address
  // Dữ liệu gửi đi: addressData (họ tên, số điện thoại, địa chỉ, isDefaultShipping, ...)
  return apiClient
    .post('/api/customer/checkout/add-address', {
      ...addressData,
      isDefaultShipping: addressData.isDefaultShipping || false, // Mặc định là false nếu không chỉ định
    })
    .then(response => {
      console.log('Address added successfully:', response.data); // Log thành công
      return response.data; // Trả về dữ liệu từ API (có thể chứa ID địa chỉ mới)
    })
    .catch(error => {
      console.error('Error adding address:', error); // Log lỗi
      throw error; // Ném lỗi để FE xử lý
    });
};
