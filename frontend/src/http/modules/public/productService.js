import axios from "axios";

export function getProducts(params) {
  return axios.get("http://localhost:8080/api/products/public", {
    params: params,
  });
  
}
export const getAllMaterials = async () => {
  return await axios.get("http://localhost:8080/api/products/public/materials");
};

export function getProductBySlug(slug) {
  return axios.get(`http://localhost:8080/api/products/public/slug/${slug}`);
};

