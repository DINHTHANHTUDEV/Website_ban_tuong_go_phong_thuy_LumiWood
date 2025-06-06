let mockOrderIdCounter = 500;
const ORDER_STATUSES = ["PENDING", "PROCESSING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED"];

const generateFakeOrderItem = () => {
  const quantity = Math.floor(Math.random() * 5) + 1;
  const price = (Math.random() * 100 + 10).toFixed(2);
  return {
    productId: `PROD-${Math.floor(Math.random() * 1000)}`,
    productName: `Mock Product ${Math.floor(Math.random() * 100) + 1}`,
    quantity: quantity,
    unitPrice: parseFloat(price),
    totalPrice: parseFloat((quantity * price).toFixed(2)),
  };
};

const generateFakeOrderSummary = (idOverride) => {
  const id = idOverride || mockOrderIdCounter++;
  const totalAmount = (Math.random() * 500 + 50).toFixed(2);
  return {
    id: id,
    customerName: `Customer ${Math.floor(Math.random() * 1000) + 1}`,
    orderDate: new Date(Date.now() - Math.random() * 30000000000).toISOString(),
    totalAmount: parseFloat(totalAmount),
    status: ORDER_STATUSES[Math.floor(Math.random() * ORDER_STATUSES.length)],
    itemCount: Math.floor(Math.random() * 5) + 1,
  };
};

const generateFakeOrderDetail = (orderId, newStatus) => {
  const summary = generateFakeOrderSummary(orderId);
  const items = Array.from({ length: summary.itemCount }, generateFakeOrderItem);
  const calculatedTotal = items.reduce((sum, item) => sum + item.totalPrice, 0);

  return {
    ...summary,
    totalAmount: parseFloat(calculatedTotal.toFixed(2)),
    status: newStatus || summary.status,
    items: items,
    shippingAddress: {
      street: `${Math.floor(Math.random() * 900) + 100} Mock St`,
      city: "Mockville",
      zipCode: `${Math.floor(Math.random() * 90000) + 10000}`,
      country: "Mockland",
    },
    billingAddress: {
      street: `${Math.floor(Math.random() * 900) + 100} Bill St`,
      city: "Billington",
      zipCode: `${Math.floor(Math.random() * 90000) + 10000}`,
      country: "Mockland",
    },
    paymentMethod: "MockCard XXXX-1234",
    shippingMethod: "Standard Mock Shipping",
    trackingNumber: (newStatus === "SHIPPED" || newStatus === "DELIVERED") ? `MOCKTRACK${Date.now()}` : null,
    notes: "This is a mock order for demonstration purposes.",
    updatedAt: new Date().toISOString(),
  };
};

const simulateDelay = (callback) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(callback());
    }, 300 + Math.random() * 400);
  });
};

export const getAllOrders = (params = {}) => {
  return simulateDelay(() => {
    const pageSize = params.size || 10;
    const pageNumber = params.page || 1;
    const totalElements = 35;
    const totalPages = Math.ceil(totalElements / pageSize);
    const orders = Array.from({ length: Math.min(pageSize, totalElements - (pageNumber - 1) * pageSize) }, (_, i) =>
      generateFakeOrderSummary(mockOrderIdCounter + i)
    );
    mockOrderIdCounter += orders.length;


    return {
      data: {
        content: orders,
        pageable: {
          pageNumber: pageNumber,
          pageSize: pageSize,
          sort: {
            sorted: params.sort ? true : false,
            unsorted: params.sort ? false : true,
            empty: params.sort ? false : true,
          },
          offset: (pageNumber - 1) * pageSize,
          paged: true,
          unpaged: false,
        },
        last: pageNumber >= totalPages,
        totalPages: totalPages,
        totalElements: totalElements,
        size: pageSize,
        number: pageNumber - 1,
        sort: {
          sorted: params.sort ? true : false,
          unsorted: params.sort ? false : true,
          empty: params.sort ? false : true,
        },
        first: pageNumber === 1,
        numberOfElements: orders.length,
        empty: orders.length === 0,
      }
    };
  });
};

export const getAdminOrderDetail = (orderId) => {
  if (!orderId) return Promise.reject(new Error("Order ID is required"));
  return simulateDelay(() => {
    if (Math.random() < 0.05 && orderId > 10) {
      return Promise.reject({ response: { status: 404, data: { message: "Mock Order not found" } } });
    }
    return { data: generateFakeOrderDetail(orderId) };
  });
};

export const updateOrderStatus = (orderId, newStatus) => {
  if (!orderId) return Promise.reject(new Error("Order ID is required"));
  if (!newStatus) return Promise.reject(new Error("New status is required"));
  if (!ORDER_STATUSES.includes(newStatus.toUpperCase())) {
    return Promise.reject(new Error(`Invalid status: ${newStatus}. Valid statuses are: ${ORDER_STATUSES.join(', ')}`));
  }
  return simulateDelay(() => {
    return { data: generateFakeOrderDetail(orderId, newStatus.toUpperCase()) };
  });
};
