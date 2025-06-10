import axios from "axios";

export function getAllProductsAdmin(params) {
  return axios.get("http://localhost:8080/api/products/admin", {
    params: params,
  });
  
}
export function createProduct(data) {
  return axios.post("http://localhost:8080/api/products/admin/addProduct", data);
}
export function getProductByIdAdmin(id) {
  return axios.get(`http://localhost:8080/api/products/admin/getProductById/${id}`);
}

export function updateProduct(id, product) {
  return axios.put(`http://localhost:8080/api/products/admin/UpdateProducts/${id}`, product, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}
