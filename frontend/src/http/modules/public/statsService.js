const MOCK_PHONGTHUY_PRODUCTS = [
  { id: "PT001", name: "Tượng Phật Di Lặc Gỗ Hương An Nhiên", basePrice: 1800000 },
  { id: "PT002", name: "Tượng Quan Công Cưỡi Ngựa Gỗ Mun Đỏ Uy Dũng", basePrice: 3500000 },
  { id: "PT003", name: "Tượng Tỳ Hưu Chiêu Tài Lộc Gỗ Trắc Đen", basePrice: 2500000 },
  { id: "PT004", name: "Tượng Thiềm Thừ (Cóc Ba Chân) Gỗ Bách Xanh", basePrice: 1200000 },
  { id: "PT005", name: "Bộ Tượng Tam Đa Phúc Lộc Thọ Gỗ Pơ Mu", basePrice: 4500000 },
  { id: "PT006", name: "Tượng Thần Tài Thổ Địa Gỗ Cẩm Thịnh Vượng", basePrice: 2800000 },
  { id: "PT007", name: "Tượng Long Quy Cõng Tháp Văn Xương Gỗ Nu", basePrice: 3200000 },
  { id: "PT008", name: "Tượng Đạt Ma Sư Tổ Gỗ Trầm Hương", basePrice: 5500000 },
];

const formatDate = (date) => {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = (`0${d.getMonth() + 1}`).slice(-2);
  const day = (`0${d.getDate()}`).slice(-2);
  return `${year}-${month}-${day}`;
};

const generateRandomStatsForPeriod = (days, endDateStr) => {
  let totalRevenue = 0;
  let totalOrders = 0;
  let newCustomers = 0;
  const productSales = {};

  MOCK_PHONGTHUY_PRODUCTS.forEach(p => {
    productSales[p.id] = { name: p.name, quantitySold: 0, revenueGenerated: 0 };
  });

  for (let i = 0; i < days; i++) {
    const dailyOrders = Math.floor(Math.random() * 8) + 1; // 1-8 orders/day
    totalOrders += dailyOrders;
    newCustomers += Math.floor(Math.random() * (dailyOrders / 2 + 1)); // 0 to half of orders are new

    for (let j = 0; j < dailyOrders; j++) {
      const numItemsInOrder = Math.floor(Math.random() * 2) + 1; // 1-2 items per order
      let orderValue = 0;
      for (let k = 0; k < numItemsInOrder; k++) {
        const product = MOCK_PHONGTHUY_PRODUCTS[Math.floor(Math.random() * MOCK_PHONGTHUY_PRODUCTS.length)];
        const quantity = 1; // Assume 1 of each item for simplicity here
        const itemPrice = product.basePrice * (Math.random() * 0.2 + 0.9); // +/- 10% price variation
        orderValue += itemPrice;
        productSales[product.id].quantitySold += quantity;
        productSales[product.id].revenueGenerated += itemPrice;
      }
      totalRevenue += orderValue;
    }
  }

  totalRevenue = Math.round(totalRevenue);
  Object.values(productSales).forEach(p => p.revenueGenerated = Math.round(p.revenueGenerated));

  const topSellingProducts = Object.values(productSales)
    .sort((a, b) => b.revenueGenerated - a.revenueGenerated)
    .slice(0, 5)
    .map(p => ({ productName: p.name, quantitySold: p.quantitySold, revenueGenerated: p.revenueGenerated }));

  const averageOrderValue = totalOrders > 0 ? Math.round(totalRevenue / totalOrders) : 0;

  const endDate = new Date(endDateStr);
  const startDate = new Date(endDate);
  startDate.setDate(endDate.getDate() - days + 1);

  return {
    totalRevenue: totalRevenue,
    totalOrders: totalOrders,
    newCustomers: Math.min(newCustomers, totalOrders),
    averageOrderValue: averageOrderValue,
    topSellingProducts: topSellingProducts,
    periodStartDate: formatDate(startDate),
    periodEndDate: formatDate(endDate),
  };
};

const simulateDelay = (dataFn, delayMs = 250 + Math.random() * 250) => {
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

export const getTodayStats = () => {
  return simulateDelay(() => {
    const todayStr = formatDate(new Date());
    return generateRandomStatsForPeriod(1, todayStr);
  });
};

export const getLast7DaysStats = () => {
  return simulateDelay(() => {
    const todayStr = formatDate(new Date());
    return generateRandomStatsForPeriod(7, todayStr);
  });
};

export const getCurrentMonthStats = () => {
  return simulateDelay(() => {
    const today = new Date();
    const daysInMonthSoFar = today.getDate();
    const todayStr = formatDate(today);
    return generateRandomStatsForPeriod(daysInMonthSoFar, todayStr);
  });
};

export const getBasicStats = (startDateStr, endDateStr) => {
  if (!startDateStr || !endDateStr) {
    return Promise.reject(new Error("Start date and end date are required for custom stats."));
  }
  return simulateDelay(() => {
    const start = new Date(startDateStr);
    const end = new Date(endDateStr);
    if (isNaN(start.getTime()) || isNaN(end.getTime()) || start > end) {
      throw new Error("Invalid date range provided for custom stats.");
    }
    const diffTime = Math.abs(end - start);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    return generateRandomStatsForPeriod(diffDays, endDateStr);
  });
};

export const getRevenueOverTime = (startDateStr, endDateStr) => {
  if (!startDateStr || !endDateStr) {
    return Promise.reject(new Error("Start date and end date are required for revenue over time."));
  }
  return simulateDelay(() => {
    const start = new Date(startDateStr);
    const end = new Date(endDateStr);
    if (isNaN(start.getTime()) || isNaN(end.getTime()) || start > end) {
      throw new Error("Invalid date range provided for revenue over time.");
    }

    const revenueData = [];
    let currentDate = new Date(start);

    while (currentDate <= end) {
      const dailyOrders = Math.floor(Math.random() * 8) + 1;
      let dailyRevenue = 0;
      for (let j = 0; j < dailyOrders; j++) {
        const numItemsInOrder = Math.floor(Math.random() * 2) + 1;
        for (let k = 0; k < numItemsInOrder; k++) {
          const product = MOCK_PHONGTHUY_PRODUCTS[Math.floor(Math.random() * MOCK_PHONGTHUY_PRODUCTS.length)];
          dailyRevenue += product.basePrice * (Math.random() * 0.2 + 0.9);
        }
      }
      revenueData.push({
        date: formatDate(currentDate),
        revenue: Math.round(dailyRevenue),
      });
      currentDate.setDate(currentDate.getDate() + 1);
    }
    return revenueData;
  });
};
