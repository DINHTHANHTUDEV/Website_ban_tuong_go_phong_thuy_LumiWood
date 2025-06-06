const MOCK_PRODUCTS_DB = {
  101: { id: 101, name: "Mock Widget Alpha", price: 19.99, imageUrl: "https://picsum.photos/seed/widgetA/100", stock: 10 },
  102: { id: 102, name: "Mock Gadget Beta", price: 29.50, imageUrl: "https://picsum.photos/seed/gadgetB/100", stock: 5 },
  103: { id: 103, name: "Mock Thingamajig Gamma", price: 9.75, imageUrl: "https://picsum.photos/seed/thingG/100", stock: 20 },
  104: { id: 104, name: "Mock Doohickey Delta", price: 150.00, imageUrl: "https://picsum.photos/seed/doohickeyD/100", stock: 3 },
  105: { id: 105, name: "Mock Contrivance Epsilon", price: 45.25, imageUrl: "https://picsum.photos/seed/contrivanceE/100", stock: 0 },
};

let mockCartState = {
  id: `mock_cart_${Date.now()}`,
  items: [],
  totalItems: 0,
  subtotalPrice: 0,
  discountAmount: 0,
  shippingCost: 0,
  finalPrice: 0,
  appliedPromotions: [],
  currency: "USD",
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
};

const recalculateMockCart = () => {
  mockCartState.totalItems = 0;
  mockCartState.subtotalPrice = 0;
  mockCartState.items.forEach(item => {
    item.itemTotalPrice = parseFloat((item.unitPrice * item.quantity).toFixed(2));
    mockCartState.totalItems += item.quantity;
    mockCartState.subtotalPrice += item.itemTotalPrice;
  });
  mockCartState.subtotalPrice = parseFloat(mockCartState.subtotalPrice.toFixed(2));

  mockCartState.discountAmount = 0;
  mockCartState.appliedPromotions = [];
  if (mockCartState.subtotalPrice > 100) {
    mockCartState.discountAmount = parseFloat((mockCartState.subtotalPrice * 0.1).toFixed(2));
    mockCartState.appliedPromotions.push({
      code: "OVER100MOCK",
      description: "10% off for orders over $100 (mock)",
      discountValue: mockCartState.discountAmount,
    });
  }

  mockCartState.shippingCost = (mockCartState.subtotalPrice > 0 && mockCartState.subtotalPrice < 50) ? 5.99 : 0;
  mockCartState.finalPrice = parseFloat(
    (mockCartState.subtotalPrice - mockCartState.discountAmount + mockCartState.shippingCost).toFixed(2)
  );
  mockCartState.updatedAt = new Date().toISOString();
  return { ...mockCartState, items: [...mockCartState.items.map(item => ({...item}))] };
};

const simulateDelay = (dataFn) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      try {
        const result = dataFn();
        resolve({ data: result });
      } catch (error) {
        resolve({ data: { error: error.message } });
      }
    }, 200 + Math.random() * 300);
  });
};


export const getCart = () => {
  return simulateDelay(() => recalculateMockCart());
};

export const addItemToCart = (itemData) => {
  if (!itemData || itemData.productId === undefined || itemData.quantity === undefined) {
    return Promise.reject(new Error("Product ID and quantity are required"));
  }
  if (itemData.quantity <= 0) {
    return Promise.reject(new Error("Quantity must be positive"));
  }

  return simulateDelay(() => {
    const product = MOCK_PRODUCTS_DB[itemData.productId];
    if (!product) {
      throw new Error(`Mock product with ID ${itemData.productId} not found.`);
    }
    if (product.stock === 0) {
      throw new Error(`Mock product ${product.name} is out of stock.`);
    }

    const existingItemIndex = mockCartState.items.findIndex(item => item.productId === itemData.productId);
    let newQuantity = itemData.quantity;

    if (existingItemIndex > -1) {
      newQuantity += mockCartState.items[existingItemIndex].quantity;
    }

    if (newQuantity > product.stock) {
      throw new Error(`Cannot add ${itemData.quantity}. Only ${product.stock - (existingItemIndex > -1 ? mockCartState.items[existingItemIndex].quantity : 0)} of ${product.name} available.`);
    }


    if (existingItemIndex > -1) {
      mockCartState.items[existingItemIndex].quantity = newQuantity;
    } else {
      mockCartState.items.push({
        productId: product.id,
        productName: product.name,
        quantity: newQuantity,
        unitPrice: product.price,
        imageUrl: product.imageUrl,
        itemTotalPrice: 0,
      });
    }
    return recalculateMockCart();
  });
};

export const updateCartItemQuantity = (productId, quantity) => {
  if (!productId) return Promise.reject(new Error("Product ID is required for update"));
  if (quantity === undefined || quantity === null)
    return Promise.reject(new Error("Quantity is required for update"));

  return simulateDelay(() => {
    const itemIndex = mockCartState.items.findIndex(item => item.productId === productId);
    if (itemIndex === -1) {
      throw new Error(`Mock item with Product ID ${productId} not found in cart.`);
    }
    const product = MOCK_PRODUCTS_DB[productId];
    if (!product) {
      throw new Error(`Mock product with ID ${productId} not found (should not happen if in cart).`);
    }

    if (quantity < 0) {
      throw new Error("Quantity cannot be negative.");
    }
    if (quantity > product.stock) {
      throw new Error(`Cannot update to ${quantity}. Only ${product.stock} of ${product.name} available.`);
    }

    if (quantity === 0) {
      mockCartState.items.splice(itemIndex, 1);
    } else {
      mockCartState.items[itemIndex].quantity = quantity;
    }
    return recalculateMockCart();
  });
};

export const removeCartItem = (productId) => {
  if (!productId) return Promise.reject(new Error("Product ID is required for removal"));
  return simulateDelay(() => {
    const itemIndex = mockCartState.items.findIndex(item => item.productId === productId);
    if (itemIndex === -1) {
      throw new Error(`Mock item with Product ID ${productId} not found in cart to remove.`);
    }
    mockCartState.items.splice(itemIndex, 1);
    return recalculateMockCart();
  });
};

export const clearServerCart = () => {
  return simulateDelay(() => {
    mockCartState.items = [];
    mockCartState.id = `mock_cart_${Date.now()}`;
    mockCartState.createdAt = new Date().toISOString();
    return recalculateMockCart();
  });
};

export const mergeGuestCart = (guestSessionId) => {
  if (!guestSessionId) return Promise.reject(new Error("Guest session ID is required for merging"));
  return simulateDelay(() => {
    const guestItem = MOCK_PRODUCTS_DB[104];
    if (guestItem) {
      const existingItemIndex = mockCartState.items.findIndex(item => item.productId === guestItem.id);
      if (existingItemIndex === -1 && guestItem.stock > 0) {
        mockCartState.items.push({
          productId: guestItem.id,
          productName: guestItem.name + " (from guest)",
          quantity: 1,
          unitPrice: guestItem.price,
          imageUrl: guestItem.imageUrl,
          itemTotalPrice: guestItem.price,
        });
      } else if (existingItemIndex > -1 && mockCartState.items[existingItemIndex].quantity +1 <= guestItem.stock) {
        mockCartState.items[existingItemIndex].quantity +=1;
      }
    }
    return recalculateMockCart();
  });
};
