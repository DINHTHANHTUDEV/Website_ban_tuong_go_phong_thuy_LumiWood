let mockPhongThuyProductIdCounter = 9000;

const PHONGTHUY_CATEGORIES = [
  { id: 1, name: "Tượng Phật - Bồ Tát", slug: "tuong-phat-bo-tat", children: [
      { id: 101, name: "Tượng Phật Di Lặc", slug: "tuong-phat-di-lac" },
      { id: 102, name: "Tượng Phật Quan Âm", slug: "tuong-phat-quan-am" },
      { id: 103, name: "Tượng Phật A Di Đà", slug: "tuong-phat-a-di-da" },
      { id: 104, name: "Tượng Đạt Ma Sư Tổ", slug: "tuong-dat-ma-su-to" },
    ]},
  { id: 2, name: "Tượng Linh Vật Chiêu Tài", slug: "tuong-linh-vat-chieu-tai", children: [
      { id: 201, name: "Tượng Tỳ Hưu", slug: "tuong-ty-huu" },
      { id: 202, name: "Tượng Thiềm Thừ (Cóc Ba Chân)", slug: "tuong-thiem-thu-coc-ba-chan" },
      { id: 203, name: "Tượng Cá Chép Hóa Rồng", slug: "tuong-ca-chep-hoa-rong" },
      { id: 204, name: "Tượng Long Quy", slug: "tuong-long-quy" },
    ]},
  { id: 3, name: "Tượng Thánh Thần - Danh Nhân", slug: "tuong-thanh-than-danh-nhan", children: [
      { id: 301, name: "Tượng Quan Công (Quan Vân Trường)", slug: "tuong-quan-cong" },
      { id: 302, name: "Tượng Trần Hưng Đạo", slug: "tuong-tran-hung-dao" },
      { id: 303, name: "Tượng Khổng Minh (Gia Cát Lượng)", slug: "tuong-khong-minh" },
      { id: 304, name: "Bộ Tượng Tam Đa (Phúc Lộc Thọ)", slug: "bo-tuong-tam-da" },
    ]},
  { id: 4, name: "Tượng 12 Con Giáp", slug: "tuong-12-con-giap" },
  { id: 5, name: "Vật Phẩm Phong Thủy Khác", slug: "vat-pham-phong-thuy-khac", children: [
      { id: 501, name: "Quả Cầu Phong Thủy", slug: "qua-cau-phong-thuy" },
      { id: 502, name: "Tranh Gỗ Phong Thủy", slug: "tranh-go-phong-thuy" },
      { id: 503, name: "Gậy Như Ý", slug: "gay-nhu-y" },
      { id: 504, name: "Ấn Rồng", slug: "an-rong" },
    ]},
  { id: 6, name: "Gỗ Quý Hiếm", slug: "go-quy-hiem"},
];

const PHONGTHUY_WOOD_TYPES = [
  { name: "Gỗ Hương", properties: ["Vân đẹp", "Mùi thơm dịu", "Độ bền cao"] },
  { name: "Gỗ Mun (Mun Sừng, Mun Hoa)", properties: ["Màu đen tuyền", "Bóng mịn", "Rất cứng"] },
  { name: "Gỗ Trắc (Trắc Đỏ, Trắc Đen)", properties: ["Nặng", "Chịu lực tốt", "Màu sắc đẹp"] },
  { name: "Gỗ Sưa (Sưa Đỏ)", properties: ["Cực hiếm", "Giá trị cao", "Vân tứ diện"] },
  { name: "Gỗ Hoàng Đàn", properties: ["Mùi thơm đặc trưng", "Có tuyết dầu", "Quý hiếm"] },
  { name: "Gỗ Bách Xanh", properties: ["Màu xanh rêu", "Chống mối mọt", "Thơm nhẹ"] },
  { name: "Gỗ Cẩm Lai", properties: ["Vân gỗ sắc nét", "Thớ mịn", "Độ cứng tốt"] },
  { name: "Gỗ Nu (Nu Nghiến, Nu Hương)", properties: ["Vân độc đáo", "Hiếm có", "Giá trị thẩm mỹ cao"] },
];

const PHONGTHUY_ARTISANS_WORKSHOPS = [
  "Nghệ Nhân Văn Thành", "Xưởng Mộc An Nhiên", "Đồ Gỗ Mỹ Nghệ Đồng Kỵ", "Làng Nghề Sơn Đồng",
  "Tượng Gỗ Gia Hưng", "Mỹ Nghệ Phúc Lộc", "Xưởng Điêu Khắc Tinh Hoa Việt"
];

const PHONGTHUY_MEANINGS = [
  "Chiêu tài hút lộc", "Trấn宅 trừ tà", "Cầu bình an may mắn", "Công danh thăng tiến", "Hóa giải sát khí",
  "Gia đạo hòa thuận", "Sức khỏe dồi dào", "Khai thông trí tuệ"
];

const generatePhongThuySlug = (name) => {
  if (!name) return `tuong-go-${Date.now()}`;
  return name
    .toLowerCase()
    .normalize("NFD").replace(/[\u0300-\u036f]/g, "") // remove diacritics
    .replace(/đ/g, "d") // handle đ
    .replace(/\s+/g, '-')
    .replace(/[^\w-]+/g, '');
};

const getRandomElement = (arr) => arr[Math.floor(Math.random() * arr.length)];
const getRandomSubset = (arr, min = 1, max = 3) => {
  const shuffled = [...arr].sort(() => 0.5 - Math.random());
  return shuffled.slice(0, Math.floor(Math.random() * (max - min + 1)) + min);
};

const MOCK_PHONGTHUY_PRODUCTS_DB = [];

const generateFakePhongThuyProductDetail = (idInput, inputData = {}) => {
  const id = idInput || mockPhongThuyProductIdCounter++;
  const selectedWood = getRandomElement(PHONGTHUY_WOOD_TYPES);
  const mainCategory = getRandomElement(PHONGTHUY_CATEGORIES);
  const subCategory = mainCategory.children ? getRandomElement(mainCategory.children) : mainCategory;

  let baseName = inputData.name;
  if (!baseName) {
    const namePrefixes = ["Cao Cấp", "Tinh Xảo", "Nghệ Thuật", "Mini Để Bàn", "Cỡ Lớn"];
    baseName = `${subCategory.name} ${selectedWood.name} ${getRandomElement(namePrefixes)}`;
  }

  const basePrice = inputData.price || (Math.floor(Math.random() * 200) + 20) * 50000; // Giá từ 1tr - 10tr, bước 50k
  const hasDiscount = Math.random() > 0.65;
  const discountPrice = hasDiscount ? Math.round(basePrice * (1 - (Math.random() * 0.2 + 0.05)) / 10000) * 10000 : null; // Giảm 5-25%, làm tròn chục nghìn

  const dimensions = {
    cao: Math.floor(Math.random() * 70) + 10, // 10-80cm
    rong: Math.floor(Math.random() * 40) + 5,  // 5-45cm
    sau: Math.floor(Math.random() * 30) + 5    // 5-35cm
  };

  const product = {
    id: id,
    name: baseName,
    slug: inputData.slug || generatePhongThuySlug(baseName),
    description: inputData.description || `Tuyệt phẩm ${baseName}, chế tác từ ${selectedWood.name} nguyên khối bởi ${getRandomElement(PHONGTHUY_ARTISANS_WORKSHOPS)}. Tượng mang ý nghĩa ${getRandomElement(PHONGTHUY_MEANINGS)}, phù hợp trưng bày phòng khách, phòng làm việc hoặc làm quà tặng ý nghĩa. Kích thước: Cao ${dimensions.cao}cm x Rộng ${dimensions.rong}cm x Sâu ${dimensions.sau}cm.`,
    price: basePrice,
    discountPrice: discountPrice,
    stockQuantity: inputData.stockQuantity === 0 ? 0 : (inputData.stockQuantity || Math.floor(Math.random() * 5) + (Math.random() > 0.05 ? 1 : 0)), // Ít hàng, có thể hết
    category: {
      id: subCategory.id,
      name: subCategory.name,
      slug: subCategory.slug,
      parentId: mainCategory.id !== subCategory.id ? mainCategory.id : null,
      parentName: mainCategory.id !== subCategory.id ? mainCategory.name : null,
      parentSlug: mainCategory.id !== subCategory.id ? mainCategory.slug : null,
    },
    artisanWorkshop: inputData.artisanWorkshop || getRandomElement(PHONGTHUY_ARTISANS_WORKSHOPS),
    images: inputData.images || [
      { url: `https://picsum.photos/seed/tuonggo${id}_1/600/500`, altText: `${baseName} - Chính diện`, isThumbnail: true },
      { url: `https://picsum.photos/seed/tuonggo${id}_2/600/500`, altText: `${baseName} - Góc nghiêng` },
      { url: `https://picsum.photos/seed/tuonggo${id}_3/600/500`, altText: `${baseName} - Chi tiết` },
      { url: `https://picsum.photos/seed/tuonggo${id}_4/600/500`, altText: `${baseName} - Mặt sau` },
    ],
    thumbnailUrl: inputData.thumbnailUrl || `https://picsum.photos/seed/tuonggo${id}_thumb/300/250`,
    averageRating: inputData.averageRating || parseFloat((Math.random() * 1 + 4).toFixed(1)), // Rating 4.0 - 5.0
    reviewCount: inputData.reviewCount || Math.floor(Math.random() * 50),
    specifications: inputData.specifications || [
      { key: "Chất liệu", value: selectedWood.name },
      { key: "Kích thước (Cao x Rộng x Sâu)", value: `${dimensions.cao}cm x ${dimensions.rong}cm x ${dimensions.sau}cm` },
      { key: "Nguồn gốc", value: getRandomElement(["Làng nghề Đồng Kỵ", "Làng nghề Thiết Úng", "Nghệ nhân Huế", "Tây Nguyên"]) },
      { key: "Ý nghĩa phong thủy", value: getRandomSubset(PHONGTHUY_MEANINGS, 1, 2).join(', ') },
      { key: "Đặc điểm gỗ", value: getRandomSubset(selectedWood.properties, 1, 2).join(', ') },
    ],
    tags: inputData.tags || [subCategory.slug, generatePhongThuySlug(selectedWood.name), ...getRandomSubset(PHONGTHUY_MEANINGS, 1, 2).map(m => generatePhongThuySlug(m))],
    isNew: inputData.isNew !== undefined ? inputData.isNew : Math.random() > 0.6,
    isHot: inputData.isHot !== undefined ? inputData.isHot : Math.random() > 0.5, // Hot seller
    isFeatured: inputData.isFeatured !== undefined ? inputData.isFeatured : Math.random() > 0.7, // Nổi bật
    createdAt: new Date(Date.now() - Math.random() * 60 * 24 * 60 * 60 * 1000).toISOString(), // Trong vòng 2 tháng
    updatedAt: new Date().toISOString(),
    currency: "VNĐ",
    ...inputData
  };
  return product;
};

if (MOCK_PHONGTHUY_PRODUCTS_DB.length === 0) {
  for (let i = 0; i < 80; i++) { // Tăng số lượng mock sản phẩm
    MOCK_PHONGTHUY_PRODUCTS_DB.push(generateFakePhongThuyProductDetail(9000 + i));
  }
}

const simulateDelay = (dataFn, delayMs = 200 + Math.random() * 300) => {
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

export const getProducts = (params = {}) => {
  return simulateDelay(() => {
    let filteredProducts = [...MOCK_PHONGTHUY_PRODUCTS_DB];

    if (params.categoryId) {
      const categoryIdNum = parseInt(params.categoryId, 10);
      filteredProducts = filteredProducts.filter(p => p.category.id === categoryIdNum || p.category.parentId === categoryIdNum);
    }
    if (params.categorySlug) {
      filteredProducts = filteredProducts.filter(p => p.category.slug === params.categorySlug || p.category.parentSlug === params.categorySlug);
    }
    if (params.keyword) {
      const keywordLower = params.keyword.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
      filteredProducts = filteredProducts.filter(p => {
        const nameNorm = p.name.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        const descNorm = p.description.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        const artisanNorm = p.artisanWorkshop.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        const tagsNorm = p.tags.join(' ').toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        return nameNorm.includes(keywordLower) || descNorm.includes(keywordLower) || artisanNorm.includes(keywordLower) || tagsNorm.includes(keywordLower);
      });
    }
    if (params.isFeatured) {
      filteredProducts = filteredProducts.filter(p => p.isFeatured === true);
    }
    if (params.isHot) {
      filteredProducts = filteredProducts.filter(p => p.isHot === true);
    }
    if (params.isNew) {
      filteredProducts = filteredProducts.filter(p => p.isNew === true);
    }


    if (params.sort) {
      const [field, direction] = params.sort.split(',');
      filteredProducts.sort((a, b) => {
        let valA = a[field];
        let valB = b[field];
        if (field === 'price') {
          valA = a.discountPrice !== null ? a.discountPrice : a.price;
          valB = b.discountPrice !== null ? b.discountPrice : b.price;
        } else if (field === 'name') {
          valA = (a[field] || "").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
          valB = (b[field] || "").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        } else if (field === 'createdAt' || field === 'updatedAt') {
          valA = new Date(a[field]).getTime();
          valB = new Date(b[field]).getTime();
        }


        if (valA < valB) return direction === 'asc' ? -1 : 1;
        if (valA > valB) return direction === 'asc' ? 1 : -1;
        return 0;
      });
    }


    const pageSize = parseInt(params.size, 10) || 12;
    const pageNumber = parseInt(params.page, 10) || 1;
    const totalElements = filteredProducts.length;
    const totalPages = Math.ceil(totalElements / pageSize);
    const startIndex = (pageNumber - 1) * pageSize;
    const paginatedContent = filteredProducts.slice(startIndex, startIndex + pageSize);

    const productSummaries = paginatedContent.map(p => ({
      id: p.id,
      name: p.name,
      slug: p.slug,
      price: p.price,
      discountPrice: p.discountPrice,
      thumbnailUrl: p.thumbnailUrl,
      categoryName: p.category.name,
      averageRating: p.averageRating,
      isNew: p.isNew,
      isHot: p.isHot,
      currency: p.currency,
      stockQuantity: p.stockQuantity,
    }));


    return {
      content: productSummaries,
      pageable: {
        pageNumber: pageNumber,
        pageSize: pageSize,
        sort: params.sort ? { sorted: true, unsorted: false, empty: false } : { sorted: false, unsorted: true, empty: true },
        offset: startIndex,
        paged: true,
        unpaged: false,
      },
      last: pageNumber >= totalPages,
      totalPages: totalPages,
      totalElements: totalElements,
      size: pageSize,
      number: pageNumber -1,
      sort: params.sort ? { sorted: true, unsorted: false, empty: false, orders: [{property: params.sort.split(',')[0], direction: params.sort.split(',')[1].toUpperCase() }] } : { sorted: false, unsorted: true, empty: true },
      first: pageNumber === 1,
      numberOfElements: productSummaries.length,
      empty: productSummaries.length === 0,
    };
  });
};

export const getProductBySlug = (slug) => {
  if (!slug) return Promise.reject(new Error("Slug sản phẩm là bắt buộc"));
  return simulateDelay(() => {
    const product = MOCK_PHONGTHUY_PRODUCTS_DB.find(p => p.slug === slug);
    if (product) {
      const relatedProducts = MOCK_PHONGTHUY_PRODUCTS_DB
        .filter(rp => (rp.category.id === product.category.id || rp.category.parentId === product.category.parentId ) && rp.id !== product.id && rp.stockQuantity > 0)
        .sort(() => 0.5 - Math.random()) // Trộn ngẫu nhiên
        .slice(0, 5) // Lấy 5 sản phẩm
        .map(rp => ({
          id: rp.id, name: rp.name, slug: rp.slug, price: rp.price, discountPrice: rp.discountPrice, thumbnailUrl: rp.thumbnailUrl, currency: rp.currency, averageRating: rp.averageRating
        }));
      return { ...product, relatedProducts: relatedProducts };
    }

    if (Math.random() < 0.1) { // Tăng tỉ lệ trả về lỗi nếu không tìm thấy slug thật
      const error = new Error(`Không tìm thấy sản phẩm tượng gỗ với slug '${slug}'.`);
      error.response = { status: 404, data: { message: `Không tìm thấy sản phẩm tượng gỗ với slug '${slug}'.` }};
      throw error;
    }
    const nameFromSlug = slug.split('-').map(s => s.charAt(0).toUpperCase() + s.slice(1)).join(' ');
    return generateFakePhongThuyProductDetail(null, { name: `${nameFromSlug} (Sản phẩm mẫu)`, slug: slug });
  });
};
