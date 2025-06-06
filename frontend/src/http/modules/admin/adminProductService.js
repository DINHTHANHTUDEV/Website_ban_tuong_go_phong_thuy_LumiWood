let mockProductIdCounter = 200;
const MOCK_CATEGORIES = [
  { id: 1, name: "Electronics" },
  { id: 2, name: "Books" },
  { id: 3, name: "Clothing" },
  { id: 4, name: "Home & Kitchen" },
];
const MOCK_BRANDS = [
  { id: 1, name: "BrandA" },
  { id: 2, name: "BrandX" },
  { id: 3, name: "GenericCorp" },
];

const getRandomElement = (arr) => arr[Math.floor(Math.random() * arr.length)];

const generateFakeProductDetail = (idOverride, productDataInput = {}) => {
  const id = idOverride || mockProductIdCounter++;
  const basePrice = parseFloat((Math.random() * 200 + 10).toFixed(2));
  const category = getRandomElement(MOCK_CATEGORIES);
  const brand = getRandomElement(MOCK_BRANDS);

  const product = {
    id: id,
    name: productDataInput.name || `Mock Product ${id}`,
    description: productDataInput.description || `This is a detailed mock description for product ${id}. It has many features and benefits.`,
    price: productDataInput.price !== undefined ? productDataInput.price : basePrice,
    stockQuantity: productDataInput.stockQuantity !== undefined ? productDataInput.stockQuantity : Math.floor(Math.random() * 100),
    category: productDataInput.category || category,
    brand: productDataInput.brand || brand,
    images: productDataInput.images || [
      { url: `https://picsum.photos/seed/${id}_1/400/300`, isThumbnail: true },
      { url: `https://picsum.photos/seed/${id}_2/400/300`, isThumbnail: false },
    ],
    specifications: productDataInput.specifications || [
      { key: "Color", value: "MockColor" },
      { key: "Material", value: "MockMaterial" },
    ],
    isActive: productDataInput.isActive !== undefined ? productDataInput.isActive : Math.random() > 0.2,
    createdAt: new Date(Date.now() - Math.random() * 10000000000).toISOString(),
    updatedAt: new Date().toISOString(),
    sku: productDataInput.sku || `SKU-MOCK-${id}`,
    weight: productDataInput.weight || parseFloat((Math.random() * 5).toFixed(2)),
    dimensions: productDataInput.dimensions || {
      length: parseFloat((Math.random() * 50).toFixed(1)),
      width: parseFloat((Math.random() * 30).toFixed(1)),
      height: parseFloat((Math.random() * 20).toFixed(1)),
    },
    ...productDataInput
  };
  return product;
};

const simulateDelay = (callback) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(callback());
    }, 300 + Math.random() * 400);
  });
};

export const getAllProductsAdmin = (params = {}) => {
  return simulateDelay(() => {
    const pageSize = params.size || 10;
    const pageNumber = params.page || 1;
    const totalElements = 45;
    const totalPages = Math.ceil(totalElements / pageSize);
    const products = Array.from({ length: Math.min(pageSize, totalElements - (pageNumber - 1) * pageSize) }, (_, i) =>
      generateFakeProductDetail(mockProductIdCounter + i)
    );
    mockProductIdCounter += products.length;


    return {
      data: {
        content: products,
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
        numberOfElements: products.length,
        empty: products.length === 0,
      }
    };
  });
};

export const getProductByIdAdmin = (productId) => {
  if (!productId) return Promise.reject(new Error("Product ID is required"));
  return simulateDelay(() => {
    if (Math.random() < 0.05 && productId > 10) {
      return Promise.reject({ response: { status: 404, data: { message: "Mock Product not found" } } });
    }
    return { data: generateFakeProductDetail(productId) };
  });
};

export const createProduct = (productData) => {
  return simulateDelay(() => {
    const newId = mockProductIdCounter++;
    const newProduct = generateFakeProductDetail(newId, productData);
    newProduct.isActive = productData.isActive !== undefined ? productData.isActive : true;
    return { data: newProduct };
  });
};

export const updateProduct = (productId, productData) => {
  if (!productId) return Promise.reject(new Error("Product ID is required for update"));
  return simulateDelay(() => {
    const updatedProduct = generateFakeProductDetail(productId, productData);
    return { data: updatedProduct };
  });
};

export const toggleAdminProductStatus = (productId, isActive) => {
  if (productId === undefined || productId === null)
    return Promise.reject(new Error("Product ID is required"));
  if (isActive === undefined || isActive === null)
    return Promise.reject(new Error("Active status is required"));

  return simulateDelay(() => {
    const product = generateFakeProductDetail(productId);
    product.isActive = isActive;
    product.updatedAt = new Date().toISOString();
    return { data: product };
  });
};

export const deleteProduct = (productId) => {
  if (!productId) return Promise.reject(new Error("Product ID is required for deletion"));
  console.warn(
    "Using deleteProduct service function to deactivate. Consider using toggleAdminProductStatus directly."
  );
  return toggleAdminProductStatus(productId, false);
};
