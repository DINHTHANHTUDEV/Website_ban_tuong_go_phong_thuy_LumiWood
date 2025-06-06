const MOCK_SHIPPING_METHODS_CHECKOUT = [
  { id: "ship_std_mock", name: "Standard Mock Shipping", description: "Delivers in 3-5 mock days", cost: 5.00, estimatedDeliveryTime: "3-5 business days" },
  { id: "ship_exp_mock", name: "Express Mock Delivery", description: "Delivers in 1-2 mock days", cost: 15.00, estimatedDeliveryTime: "1-2 business days" },
  { id: "ship_free_mock", name: "Free Mock Shipping (Orders > $50)", description: "Delivers in 5-7 mock days", cost: 0.00, estimatedDeliveryTime: "5-7 business days" },
];

const MOCK_PAYMENT_METHODS_CHECKOUT = [
  { id: "pay_cod_mock", name: "Cash on Delivery (Mock)", description: "Pay when you receive the order." },
  { id: "pay_card_mock", name: "Credit/Debit Card (Mock)", description: "Secure online payment." },
  { id: "pay_wallet_mock", name: "MockPay Wallet", description: "Use your MockPay balance." },
];

const generateMockAddress = (id, isDefault = false) => {
  const firstNames = ["Alex", "Jamie", "Casey", "Robin", "Jordan"];
  const lastNames = ["Smith", "Doe", "Patel", "Kim", "Garcia"];
  return {
    id: `addr_mock_${id}`,
    recipientName: `${firstNames[Math.floor(Math.random() * firstNames.length)]} ${lastNames[Math.floor(Math.random() * lastNames.length)]}`,
    phoneNumber: `555-01${String(Math.floor(Math.random() * 90) + 10).padStart(2, '0')}`,
    street: `${Math.floor(Math.random() * 900) + 100} Mockingbird Ave, Apt ${Math.floor(Math.random() * 20) + 1}`,
    ward: `Ward ${String.fromCharCode(65 + Math.floor(Math.random() * 10))}`,
    district: `District ${Math.floor(Math.random() * 10) + 1}`,
    city: ["MockCity", "Faketown", "Simville"][Math.floor(Math.random() * 3)],
    country: "Mockland",
    isDefaultShipping: isDefault && Math.random() > 0.3,
    isDefaultBilling: isDefault && Math.random() > 0.3,
  };
};

const generateMockCartSummary = () => {
  const subtotal = parseFloat((Math.random() * 200 + 20).toFixed(2));
  const discount = subtotal > 100 ? parseFloat((subtotal * 0.1).toFixed(2)) : 0;
  const shipping = subtotal > 50 ? 0 : 5.00;
  return {
    totalItems: Math.floor(Math.random() * 5) + 1,
    subtotalPrice: subtotal,
    discountAmount: discount,
    shippingCost: shipping,
    finalPrice: parseFloat((subtotal - discount + shipping).toFixed(2)),
    currency: "USD",
    items: Array.from({length: Math.floor(Math.random()*3)+1}, (_,i) => ({
      productId: 100+i,
      productName: `Mock Product ${String.fromCharCode(65+i)}`,
      quantity: Math.floor(Math.random()*2)+1,
      unitPrice: parseFloat((Math.random()*50+10).toFixed(2)),
      imageUrl: `https://picsum.photos/seed/cartProd${i}/50`
    }))
  }
}

const simulateDelay = (dataFn, delayMs = 250 + Math.random() * 350) => {
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

export const getCheckoutInfo = () => {
  return simulateDelay(() => {
    const numAddresses = Math.floor(Math.random() * 3) + 1;
    const savedAddresses = Array.from({ length: numAddresses }, (_, i) => generateMockAddress(i + 1, i === 0));
    if (savedAddresses.length > 0 && !savedAddresses.some(a => a.isDefaultShipping)) {
      savedAddresses[0].isDefaultShipping = true;
    }
    if (savedAddresses.length > 0 && !savedAddresses.some(a => a.isDefaultBilling)) {
      savedAddresses[0].isDefaultBilling = true;
    }

    return {
      savedAddresses: savedAddresses,
      availableShippingMethods: MOCK_SHIPPING_METHODS_CHECKOUT.map(m => ({...m})),
      availablePaymentMethods: MOCK_PAYMENT_METHODS_CHECKOUT.map(p => ({...p})),
      cartSummary: generateMockCartSummary(),
      userTier: ["BRONZE", "SILVER", "GOLD"][Math.floor(Math.random()*3)],
    };
  });
};

export const placeOrder = (orderData) => {
  if (!orderData || !orderData.shippingAddressId || !orderData.paymentMethodId) {
    return Promise.reject(new Error("Shipping address, and payment method are required for mock order."));
  }
  return simulateDelay(() => {
    const orderId = `MOCKORD-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
    const cartInfo = generateMockCartSummary();
    const shippingMethod = MOCK_SHIPPING_METHODS_CHECKOUT.find(sm => sm.id === orderData.shippingMethodId) || MOCK_SHIPPING_METHODS_CHECKOUT[0];
    const paymentMethod = MOCK_PAYMENT_METHODS_CHECKOUT.find(pm => pm.id === orderData.paymentMethodId) || MOCK_PAYMENT_METHODS_CHECKOUT[0];

    let estimatedDeliveryDays = 5;
    if (shippingMethod.name.toLowerCase().includes("express")) estimatedDeliveryDays = 2;
    else if (shippingMethod.name.toLowerCase().includes("standard")) estimatedDeliveryDays = 4;

    const orderDate = new Date();
    const estimatedDeliveryDate = new Date(orderDate);
    estimatedDeliveryDate.setDate(orderDate.getDate() + estimatedDeliveryDays);


    return {
      orderId: orderId,
      orderDate: orderDate.toISOString(),
      status: "PENDING_CONFIRMATION_MOCK",
      totalAmount: cartInfo.finalPrice,
      shippingAddress: orderData.selectedShippingAddress || generateMockAddress("final_ship"),
      billingAddress: orderData.selectedBillingAddress || generateMockAddress("final_bill"),
      shippingMethodName: shippingMethod.name,
      paymentMethodName: paymentMethod.name,
      items: cartInfo.items.map(item => ({
        productId: item.productId,
        productName: item.productName,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        itemTotalPrice: parseFloat((item.quantity * item.unitPrice).toFixed(2))
      })),
      subtotal: cartInfo.subtotalPrice,
      discountApplied: cartInfo.discountAmount,
      shippingFee: cartInfo.shippingCost,
      estimatedDeliveryDate: estimatedDeliveryDate.toISOString(),
      trackingNumber: null,
      customerNotes: orderData.notes || null
    };
  });
};
