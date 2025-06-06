let mockShippingMethodIdCounter = 400;
const SHIPPING_CARRIERS = ["GiaoHangNhanh", "ViettelPost", "VNPost", "J&T Express", "GrabExpress"];
const ESTIMATED_TIMES = ["1-2 business days", "2-4 business days", "3-5 business days", "5-7 business days", "Same-day (if applicable)"];

const generateFakeShippingMethod = (idOverride, methodDataInput = {}) => {
  const id = idOverride || mockShippingMethodIdCounter++;
  const carrier = SHIPPING_CARRIERS[Math.floor(Math.random() * SHIPPING_CARRIERS.length)];

  const method = {
    id: id,
    name: methodDataInput.name || `${carrier} - Standard ${id}`,
    description: methodDataInput.description || `Reliable shipping via ${carrier}.`,
    cost: methodDataInput.cost !== undefined ? parseFloat(methodDataInput.cost) : parseFloat((Math.random() * 15 + 2).toFixed(2)), // Cost in some currency unit
    estimatedDeliveryTime: methodDataInput.estimatedDeliveryTime || ESTIMATED_TIMES[Math.floor(Math.random() * ESTIMATED_TIMES.length)],
    isActive: methodDataInput.isActive !== undefined ? methodDataInput.isActive : Math.random() > 0.15,
    carrier: methodDataInput.carrier || carrier,
    trackingUrlTemplate: methodDataInput.trackingUrlTemplate || `https://track.${carrier.toLowerCase().replace(/\s+/g, '')}.com/tracking?id={tracking_id}`,
    createdAt: new Date(Date.now() - Math.random() * 5000000000).toISOString(),
    updatedAt: new Date().toISOString(),
  };
  return { ...method, ...methodDataInput };
};

const simulateDelay = (callback, delayMs = 300 + Math.random() * 400) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(callback());
    }, delayMs);
  });
};

export const getAdminShippingMethods = (params = {}) => {
  return simulateDelay(() => {
    const pageSize = params.size || 10;
    const pageNumber = params.page || 1;
    const totalElements = 15;
    const totalPages = Math.ceil(totalElements / pageSize);
    const methods = Array.from({ length: Math.min(pageSize, totalElements - (pageNumber - 1) * pageSize) }, (_, i) =>
      generateFakeShippingMethod(mockShippingMethodIdCounter + i)
    );
    mockShippingMethodIdCounter += methods.length;


    return {
      data: {
        content: methods,
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
        numberOfElements: methods.length,
        empty: methods.length === 0,
      }
    };
  });
};

export const getAdminShippingMethodById = (id) => {
  if (!id) return Promise.reject(new Error("Shipping Method ID is required"));
  return simulateDelay(() => {
    if (Math.random() < 0.05 && id > 5) {
      return Promise.reject({ response: { status: 404, data: { message: "Mock Shipping Method not found" } } });
    }
    return { data: generateFakeShippingMethod(id) };
  });
};

export const createAdminShippingMethod = (methodData) => {
  return simulateDelay(() => {
    const newId = mockShippingMethodIdCounter++;
    const newMethod = generateFakeShippingMethod(newId, { ...methodData, isActive: methodData.isActive !== undefined ? methodData.isActive : true });
    return { data: newMethod };
  });
};

export const updateAdminShippingMethod = (id, methodData) => {
  if (!id) return Promise.reject(new Error("Shipping Method ID is required"));
  return simulateDelay(() => {
    const updatedMethod = generateFakeShippingMethod(id, methodData);
    return { data: updatedMethod };
  });
};

export const deleteAdminShippingMethod = (id) => {
  if (!id) return Promise.reject(new Error("Shipping Method ID is required"));
  return simulateDelay(() => {
    return { data: {} };
  }, 200);
};
