const MOCK_CATEGORIES_DB = [
  { id: 1, name: "Electronics", slug: "electronics", description: "All kinds of electronic gadgets and devices.", imageUrl: "https://picsum.photos/seed/cat1/300/200", productCount: 120 },
  { id: 2, name: "Books", slug: "books", description: "Fiction, non-fiction, educational, and more.", imageUrl: "https://picsum.photos/seed/cat2/300/200", productCount: 350 },
  { id: 3, name: "Clothing & Apparel", slug: "clothing-apparel", description: "Fashion for men, women, and children.", imageUrl: "https://picsum.photos/seed/cat3/300/200", productCount: 210 },
  { id: 4, name: "Home & Kitchen", slug: "home-kitchen", description: "Appliances, decor, and essentials for your home.", imageUrl: "https://picsum.photos/seed/cat4/300/200", productCount: 180 },
  { id: 5, name: "Sports & Outdoors", slug: "sports-outdoors", description: "Gear for your favorite activities.", imageUrl: "https://picsum.photos/seed/cat5/300/200", productCount: 95 },
  { id: 6, name: "Computers", slug: "computers", parentId: 1, description: "Laptops, desktops, and accessories.", imageUrl: "https://picsum.photos/seed/cat6/300/200", productCount: 45 },
  { id: 7, name: "Mobile Phones", slug: "mobile-phones", parentId: 1, description: "Smartphones and basic phones.", imageUrl: "https://picsum.photos/seed/cat7/300/200", productCount: 35 },
];

const generateSlugFromString = (text) => {
  if (!text) return `random-slug-${Date.now()}`;
  return text
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^\w-]+/g, '');
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
    }, 150 + Math.random() * 250);
  });
};

export const getAllCategories = () => {
  return simulateDelay(() => {
    return MOCK_CATEGORIES_DB.map(category => ({ ...category }));
  });
};

export const getCategoryBySlug = (slug) => {
  if (!slug) return Promise.reject(new Error("Category slug is required"));
  return simulateDelay(() => {
    const foundCategory = MOCK_CATEGORIES_DB.find(cat => cat.slug === slug);
    if (foundCategory) {
      return { ...foundCategory };
    }
    if (Math.random() < 0.1) {
      throw new Error(`Mock category with slug '${slug}' not found.`);
    }
    const newId = Math.max(...MOCK_CATEGORIES_DB.map(c => c.id), 0) + 1 + Math.floor(Math.random()*100);
    return {
      id: newId,
      name: slug.split('-').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ') + " (Auto-Generated)",
      slug: slug,
      description: `This is an auto-generated mock category for slug: ${slug}.`,
      imageUrl: `https://picsum.photos/seed/${slug}/300/200`,
      productCount: Math.floor(Math.random() * 50)
    };
  });
};
