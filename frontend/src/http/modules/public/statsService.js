import apiClient from "../../axios";

// Thống kê hôm nay
export const getTodayStats = async () => {
  console.log("Sending /today without token"); // Debug
  try {
    const res = await apiClient.get("/api/statistics/today");
    return res.data;
  } catch (error) {
    console.error("Error in getTodayStats:", error.response?.data || error.message);
    throw error;
  }
};

// Thống kê 7 ngày gần đây
export const getLast7DaysStats = async () => {
  console.log("Sending /last-7-days without token"); // Debug
  try {
    const res = await apiClient.get("/api/statistics/last-7-days");
    return res.data;
  } catch (error) {
    console.error("Error in getLast7DaysStats:", error.response?.data || error.message);
    throw error;
  }
};

// Thống kê tháng hiện tại
export const getCurrentMonthStats = async () => {
  console.log("Sending /current-month without token"); // Debug
  try {
    const res = await apiClient.get("/api/statistics/current-month");
    return res.data;
  } catch (error) {
    console.error("Error in getCurrentMonthStats:", error.response?.data || error.message);
    throw error;
  }
};

// Thống kê cơ bản (không cần tham số ngày)
export const getBasicStats = async () => {
  console.log("Sending /basic-stats without token"); // Debug
  try {
    const res = await apiClient.get("/api/statistics/basic-stats");
    return res.data;
  } catch (error) {
    console.error("Error in getBasicStats:", error.response?.data || error.message);
    throw error;
  }
};

// Doanh thu biểu đồ line chart theo thời gian
export const getRevenueOverTime = async (startDateStr, endDateStr) => {
  if (!startDateStr || !endDateStr) {
    throw new Error("Start date and end date are required");
  }
  console.log("Sending /revenueovertime without token"); // Debug
  try {
    const res = await apiClient.get("/api/statistics/revenueovertime", {
      params: {
        start: startDateStr,
        end: endDateStr,
      },
    });
    return res.data;
  } catch (error) {
    console.error("Error in getRevenueOverTime:", error.response?.data || error.message);
    throw error;
  }
};
