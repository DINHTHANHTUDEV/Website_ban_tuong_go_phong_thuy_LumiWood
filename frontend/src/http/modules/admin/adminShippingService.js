import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/admin/shipping-methods';

export const getAdminShippingMethods = (params = {}) => {
  return axios.get(API_BASE_URL, { params });
};

export const getAdminShippingMethodById = (id) => {
  if (!id) return Promise.reject(new Error("Shipping Method ID is required"));
  return axios.get(`${API_BASE_URL}/${id}`);
};

export const createAdminShippingMethod = (methodData) => {
  return axios.post(API_BASE_URL, methodData);
};

export const updateAdminShippingMethod = (id, methodData) => {
  if (!id) return Promise.reject(new Error("Shipping Method ID is required"));
  return axios.put(`${API_BASE_URL}/${id}`, methodData);
};

export const deleteAdminShippingMethod = (id) => {
  if (!id) return Promise.reject(new Error("Shipping Method ID is required"));
  return axios.delete(`${API_BASE_URL}/${id}`);
};
