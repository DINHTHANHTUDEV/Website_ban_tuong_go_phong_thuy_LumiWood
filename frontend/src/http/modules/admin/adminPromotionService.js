const PROMOTION_TYPES = ["PERCENTAGE", "FIXED_AMOUNT", "FREE_SHIPPING"];
const CUSTOMER_TIERS = ["BRONZE", "SILVER", "GOLD", "PLATINUM"];
let mockPromotionIdCounter = 300;

const generateRandomDateRange = () => {
  const now = Date.now();
  const startOffset = Math.random() * 60 * 24 * 60 * 60 * 1000;
  const endOffset = Math.random() * 90 * 24 * 60 * 60 * 1000;
  const startDate = new Date(now - startOffset);
  const endDate = new Date(startDate.getTime() + endOffset);
  return {
    startDate: startDate.toISOString(),
    endDate: endDate.toISOString(),
  };
};

const generateFakePromotion = (idOverride, promotionDataInput = {}) => {
  const id = idOverride || mockPromotionIdCounter++;
  const dates = generateRandomDateRange();
  const discountType = PROMOTION_TYPES[Math.floor(Math.random() * PROMOTION_TYPES.length)];
  let discountValue;
  if (discountType === "PERCENTAGE") {
    discountValue = Math.floor(Math.random() * 50) + 5;
  } else if (discountType === "FIXED_AMOUNT") {
    discountValue = (Math.random() * 50 + 5).toFixed(2);
  } else {
    discountValue = 0;
  }

  const numTargetTiers = Math.floor(Math.random() * (CUSTOMER_TIERS.length + 1));
  const targetTiersArray = CUSTOMER_TIERS.slice(0, numTargetTiers);


  const promotion = {
    id: id,
    name: promotionDataInput.name || `Mock Promo ${id} - ${discountValue}${discountType === "PERCENTAGE" ? "%" : (discountType === "FIXED_AMOUNT" ? "$" : " Shipping")}`,
    description: promotionDataInput.description || `This is a mock promotion offering a fantastic deal. Applicable to ${targetTiersArray.length > 0 ? targetTiersArray.join(', ') : 'all customers'}.`,
    discountType: promotionDataInput.discountType || discountType,
    discountValue: promotionDataInput.discountValue !== undefined ? parseFloat(promotionDataInput.discountValue) : parseFloat(discountValue),
    startDate: promotionDataInput.startDate || dates.startDate,
    endDate: promotionDataInput.endDate || dates.endDate,
    isActive: promotionDataInput.isActive !== undefined ? promotionDataInput.isActive : Math.random() > 0.2,
    usageLimit: promotionDataInput.usageLimit === null ? null : (promotionDataInput.usageLimit || (Math.random() > 0.5 ? Math.floor(Math.random() * 1000) + 100 : null)),
    code: promotionDataInput.code || `MOCKCODE${id}`,
    minimumSpend: promotionDataInput.minimumSpend === null ? null : (promotionDataInput.minimumSpend || (Math.random() > 0.3 ? (Math.random() * 100 + 20).toFixed(2) : null)),
    targetProducts: promotionDataInput.targetProducts || (Math.random() > 0.7 ? [`P${Math.floor(Math.random()*100)}`, `P${Math.floor(Math.random()*100)}`] : []),
    targetCategories: promotionDataInput.targetCategories || (Math.random() > 0.7 ? [`C${Math.floor(Math.random()*10)}`, `C${Math.floor(Math.random()*10)}`] : []),
    targetTiers: promotionDataInput.targetTiers ? (Array.isArray(promotionDataInput.targetTiers) ? promotionDataInput.targetTiers.join(',') : promotionDataInput.targetTiers) : targetTiersArray.join(','),
    createdAt: new Date(Date.now() - Math.random() * 10000000000).toISOString(),
    updatedAt: new Date().toISOString(),
  };

  if (promotionDataInput.hasOwnProperty('isActive')) {
    promotion.isActive = promotionDataInput.isActive;
  }

  if (promotion.minimumSpend) promotion.minimumSpend = parseFloat(promotion.minimumSpend);
  return { ...promotion, ...promotionDataInput};
};

const simulateDelay = (callback) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(callback());
    }, 300 + Math.random() * 400);
  });
};

export const getAdminPromotions = (params = {}) => {
  return simulateDelay(() => {
    const pageSize = params.size || 10;
    const pageNumber = params.page || 1;
    const totalElements = 28;
    const totalPages = Math.ceil(totalElements / pageSize);
    const promotions = Array.from({ length: Math.min(pageSize, totalElements - (pageNumber - 1) * pageSize) }, (_, i) =>
      generateFakePromotion(mockPromotionIdCounter + i)
    );
    mockPromotionIdCounter += promotions.length;


    return {
      data: {
        content: promotions,
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
        numberOfElements: promotions.length,
        empty: promotions.length === 0,
      }
    };
  });
};

export const getAdminPromotionById = (id) => {
  if (!id) return Promise.reject(new Error("Promotion ID is required"));
  return simulateDelay(() => {
    if (Math.random() < 0.05 && id > 10) {
      return Promise.reject({ response: { status: 404, data: { message: "Mock Promotion not found" } } });
    }
    return { data: generateFakePromotion(id) };
  });
};

export const createAdminPromotion = (promotionData) => {
  return simulateDelay(() => {
    const newId = mockPromotionIdCounter++;
    const newPromotion = generateFakePromotion(newId, { ...promotionData, isActive: promotionData.isActive !== undefined ? promotionData.isActive : true });
    return { data: newPromotion };
  });
};

export const updateAdminPromotion = (id, promotionData) => {
  if (!id) return Promise.reject(new Error("Promotion ID is required"));
  return simulateDelay(() => {
    const updatedPromotion = generateFakePromotion(id, promotionData);
    return { data: updatedPromotion };
  });
};

export const deactivateAdminPromotion = (id, promotionData = {}) => {
  if (!id) return Promise.reject(new Error("Promotion ID is required"));
  return simulateDelay(() => {
    const payload = { ...promotionData, isActive: false };
    const deactivatedPromotion = generateFakePromotion(id, payload);
    return { data: deactivatedPromotion };
  });
};

export const activateAdminPromotion = (id, promotionData = {}) => {
  if (!id) return Promise.reject(new Error("Promotion ID is required"));
  return simulateDelay(() => {
    const payload = { ...promotionData, isActive: true };
    const activatedPromotion = generateFakePromotion(id, payload);
    return { data: activatedPromotion };
  });
};

export const toggleAdminPromotionStatus = (id, isActive) => {
  if (id === undefined || id === null) return Promise.reject(new Error("Promotion ID is required"));
  if (isActive === undefined || isActive === null)
    return Promise.reject(new Error("Active status is required"));

  return simulateDelay(() => {
    const promotion = generateFakePromotion(id, { isActive: isActive });
    promotion.updatedAt = new Date().toISOString();
    return { data: promotion };
  });
};
