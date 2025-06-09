const USER_ORDER_STATUSES = [
  "PENDING_PAYMENT",
  "PROCESSING",
  "SHIPPED",
  "DELIVERED",
  "CANCELLED_BY_USER",
  "COMPLETED",
  "AWAITING_CONFIRMATION",
];
let mockUserOrderIdCounter = 7000;

const generateRandomUserOrderItem = (orderIdPrefix) => {
  const quantity = Math.floor(Math.random() * 3) + 1;
  const price = parseFloat((Math.random() * 75 + 5).toFixed(2));
  const productId = Math.floor(Math.random() * 200) + 100;
  return {
    productId: productId,
    productName: `Mock Product ${String.fromCharCode(65 + (productId % 26))}-${productId % 10}`,
    quantity: quantity,
    unitPrice: price,
    itemTotalPrice: parseFloat((quantity * price).toFixed(2)),
    imageUrl: `https://picsum.photos/seed/prodOrder${productId}/80/80`,
  };
};

const generateMockUserAddress = (type = "shipping") => {
  return {
    recipientName: `Mock ${type === "shipping" ? "Recipient" : "Payer"} Name`,
    street: `${Math.floor(Math.random() * 800) + 100} Mock ${
      type === "shipping" ? "Delivery" : "Billing"
    } St.`,
    city: "Mockington",
    state: "MC",
    zipCode: `${Math.floor(Math.random() * 80000) + 10000}`,
    country: "Mocktania",
    phoneNumber: `555-MOCK-${Math.floor(Math.random() * 9000) + 1000}`,
  };
};

const generateMockOrderBase = (orderIdInput) => {
  const orderId = orderIdInput || `MU-${mockUserOrderIdCounter++}`;
  const orderDate = new Date(Date.now() - Math.random() * 60 * 24 * 60 * 60 * 1000); // Within last 60 days
  const items = Array.from({ length: Math.floor(Math.random() * 4) + 1 }, () =>
    generateRandomUserOrderItem(orderId)
  );
  const subtotal = parseFloat(items.reduce((sum, item) => sum + item.itemTotalPrice, 0).toFixed(2));
  const discountApplied =
    subtotal > 50 ? parseFloat((subtotal * (Math.random() * 0.15 + 0.05)).toFixed(2)) : 0;
  const shippingFee =
    subtotal > 30 && Math.random() > 0.3 ? parseFloat((Math.random() * 10 + 2).toFixed(2)) : 0;
  const totalAmount = parseFloat((subtotal - discountApplied + shippingFee).toFixed(2));
  const status = USER_ORDER_STATUSES[Math.floor(Math.random() * USER_ORDER_STATUSES.length)];

  let estimatedDeliveryDate = null;
  let trackingNumber = null;
  if (status === "SHIPPED" || status === "DELIVERED" || status === "COMPLETED") {
    const deliveryDays = Math.floor(Math.random() * 5) + 2;
    estimatedDeliveryDate = new Date(
      orderDate.getTime() + deliveryDays * 24 * 60 * 60 * 1000
    ).toISOString();
    if (status !== "DELIVERED" && status !== "COMPLETED") {
      trackingNumber = `MOCKTRACK${orderId.replace(/\D/g, "")}`;
    }
  }

  return {
    orderId,
    orderDate: orderDate.toISOString(),
    status,
    totalAmount,
    items,
    subtotal,
    discountApplied,
    shippingFee,
    estimatedDeliveryDate,
    trackingNumber,
  };
};

const generateMockOrderListDTO = (orderIdInput) => {
  const base = generateMockOrderBase(orderIdInput);
  return {
    orderId: base.orderId,
    orderDate: base.orderDate,
    totalAmount: base.totalAmount,
    status: base.status,
    itemCount: base.items.reduce((sum, item) => sum + item.quantity, 0),
  };
};

const generateMockOrderSummaryDTO = (orderIdInput) => {
  const base = generateMockOrderBase(orderIdInput);
  return {
    orderId: base.orderId,
    orderDate: base.orderDate,
    status: base.status,
    totalAmount: base.totalAmount,
    shippingAddressSimple: `${generateMockUserAddress().street}, ${generateMockUserAddress().city}`,
    paymentMethodName: ["MockCard", "MockPay", "COD_Mock"][Math.floor(Math.random() * 3)],
    items: base.items.map((i) => ({
      productName: i.productName,
      quantity: i.quantity,
      unitPrice: i.unitPrice,
    })),
    estimatedDeliveryDate: base.estimatedDeliveryDate,
  };
};

const generateMockUserOrderDetailDTO = (orderIdInput) => {
  const base = generateMockOrderBase(orderIdInput);
  const shippingAddress = generateMockUserAddress("shipping");
  const billingAddress = Math.random() > 0.7 ? shippingAddress : generateMockUserAddress("billing");

  const orderHistory = [{ status: "AWAITING_CONFIRMATION", date: base.orderDate }];
  if (base.status !== "AWAITING_CONFIRMATION") {
    orderHistory.push({
      status: "PROCESSING",
      date: new Date(new Date(base.orderDate).getTime() + 1000 * 60 * 60).toISOString(),
    });
    if (base.status === "SHIPPED" || base.status === "DELIVERED" || base.status === "COMPLETED") {
      orderHistory.push({
        status: "SHIPPED",
        date: new Date(new Date(base.orderDate).getTime() + 1000 * 60 * 60 * 24).toISOString(),
      });
      if (base.status === "DELIVERED" || base.status === "COMPLETED") {
        orderHistory.push({
          status: "DELIVERED",
          date:
            base.estimatedDeliveryDate ||
            new Date(new Date(base.orderDate).getTime() + 1000 * 60 * 60 * 24 * 3).toISOString(),
        });
      }
    }
  }

  return {
    ...base,
    shippingAddress,
    billingAddress,
    shippingMethodName: ["Standard Mock", "Express Mock"][Math.floor(Math.random() * 2)],
    paymentMethodName: ["MockCard Ending 1234", "MockWallet Balance", "Cash On Delivery (Mock)"][
      Math.floor(Math.random() * 3)
    ],
    customerNotes:
      Math.random() > 0.6 ? "Please deliver in the afternoon if possible (mock note)." : null,
    orderHistory: orderHistory,
  };
};

const simulateDelay = (dataFn, delayMs = 300 + Math.random() * 400) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        const result = dataFn();
        resolve({ data: result });
      } catch (error) {
        reject(error);
      }
    }, delayMs);
  });
};

// export const getUserOrders = (params = {}) => {
//   return simulateDelay(() => {
//     const pageSize = params.size || 10;
//     const pageNumber = params.page || 1;
//     const totalElements = 23;
//     const totalPages = Math.ceil(totalElements / pageSize);

//     const startIndex = (pageNumber - 1) * pageSize;
//     const endIndex = Math.min(startIndex + pageSize, totalElements);
//     const orders = [];
//     for (let i = 0; i < totalElements; i++) {
//       if (i >= startIndex && i < endIndex) {
//         orders.push(generateMockOrderListDTO(`MU-${7000 + i}`));
//       }
//     }

//     return {
//       content: orders,
//       pageable: {
//         pageNumber: pageNumber,
//         pageSize: pageSize,
//         sort: {
//           sorted: params.sort ? true : false,
//           unsorted: params.sort ? false : true,
//           empty: params.sort ? false : true,
//         },
//         offset: startIndex,
//         paged: true,
//         unpaged: false,
//       },
//       last: pageNumber >= totalPages,
//       totalPages: totalPages,
//       totalElements: totalElements,
//       size: pageSize,
//       number: pageNumber - 1,
//       sort: {
//         sorted: params.sort ? true : false,
//         unsorted: params.sort ? false : true,
//         empty: params.sort ? false : true,
//       },
//       first: pageNumber === 1,
//       numberOfElements: orders.length,
//       empty: orders.length === 0,
//     };
//   });
// };

// hàm chat gpt mới update thay cho mockapi
import axios from "axios";

const API_URL = "http://localhost:8080/api/orders/getOrdersHistory";

export const getUserOrders = (params = {}) => {
  return axios.get(API_URL, { params });
};

///
export const getOrderSummaryById = (orderId) => {
  if (!orderId) return Promise.reject(new Error("Order ID is required"));
  return simulateDelay(() => {
    if (typeof orderId === "string" && Math.random() < 0.1 && !orderId.startsWith("MU-70")) {
      throw new Error(`Mock Order Summary for ID ${orderId} not found.`);
    }
    return generateMockOrderSummaryDTO(orderId);
  });
};

// export const getUserOrderDetail = (orderId) => {
//   if (!orderId) return Promise.reject(new Error("Order ID is required"));
//   return simulateDelay(() => {
//     if (typeof orderId === 'string' && Math.random() < 0.05 && !orderId.startsWith("MU-70")) {
//       throw new Error(`Mock Order Detail for ID ${orderId} not found.`);
//     }
//     return generateMockUserOrderDetailDTO(orderId);
//   });
// };

const API_Url = "http://localhost:8080/api/orders/getOrdersDetail";

export const getUserOrderDetail = (orderId) => {
  return axios.get(`${API_Url}/${orderId}`);
};
