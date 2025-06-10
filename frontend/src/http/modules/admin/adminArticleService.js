// let mockArticleIdCounter = 100;

// const generateFakeArticleSummary = (idOverride) => {
//   const id = idOverride || mockArticleIdCounter++;
//   return {
//     id: id,
//     title: `Mocked Article Title ${id}`,
//     isPublished: Math.random() > 0.5,
//     author: "Mock User",
//     createdAt: new Date(Date.now() - Math.random() * 1000000000).toISOString(),
//     updatedAt: new Date().toISOString(),
//   };
// };

// const generateFakeArticleDetail = (id, articleData = {}) => {
//   return {
//     id: id,
//     title: articleData.title || `Detailed Mock Article Title ${id}`,
//     content: articleData.content || `This is the full mock content for article ${id}. It contains more details than the summary.`,
//     isPublished: articleData.isPublished !== undefined ? articleData.isPublished : Math.random() > 0.5,
//     author: "Mock User",
//     tags: ["mock", "sample", "data"],
//     category: "Mock Category",
//     createdAt: new Date(Date.now() - Math.random() * 1000000000).toISOString(),
//     updatedAt: new Date().toISOString(),
//     ...articleData
//   };
// };

// const simulateDelay = (callback) => {
//   return new Promise((resolve) => {
//     setTimeout(() => {
//       resolve(callback());
//     }, 300 + Math.random() * 400);
//   });
// };

// export const getAdminArticles = (params = {}) => {
//   return simulateDelay(() => {
//     const pageSize = params.size || 10;
//     const pageNumber = params.page || 1;
//     const totalElements = 25;
//     const totalPages = Math.ceil(totalElements / pageSize);
//     const articles = Array.from({ length: Math.min(pageSize, totalElements - (pageNumber-1)*pageSize) }, (_, i) =>
//       generateFakeArticleSummary((pageNumber-1)*pageSize + i +1)
//     );

//     return {
//       data: {
//         content: articles,
//         pageable: {
//           pageNumber: pageNumber,
//           pageSize: pageSize,
//           sort: {
//             sorted: params.sort ? true : false,
//             unsorted: params.sort ? false : true,
//             empty: params.sort ? false : true,
//           },
//           offset: (pageNumber - 1) * pageSize,
//           paged: true,
//           unpaged: false,
//         },
//         last: pageNumber >= totalPages,
//         totalPages: totalPages,
//         totalElements: totalElements,
//         size: pageSize,
//         number: pageNumber -1,
//         sort: {
//           sorted: params.sort ? true : false,
//           unsorted: params.sort ? false : true,
//           empty: params.sort ? false : true,
//         },
//         first: pageNumber === 1,
//         numberOfElements: articles.length,
//         empty: articles.length === 0,
//       }
//     };
//   });
// };

// export const getAdminArticleById = (id) => {
//   if (!id) return Promise.reject(new Error("Article ID is required"));
//   return simulateDelay(() => {
//     if (Math.random() < 0.05) { // Simulate not found
//       return Promise.reject({ response: { status: 404, data: { message: "Mock Article not found" } } });
//     }
//     return { data: generateFakeArticleDetail(id) };
//   });
// };

// export const createAdminArticle = (articleData) => {
//   return simulateDelay(() => {
//     const newId = mockArticleIdCounter++;
//     const newArticle = generateFakeArticleDetail(newId, articleData);
//     newArticle.isPublished = articleData.isPublished || false;
//     return { data: newArticle };
//   });
// };

// export const updateAdminArticle = (id, articleData) => {
//   if (!id) return Promise.reject(new Error("Article ID is required"));
//   return simulateDelay(() => {
//     const updatedArticle = generateFakeArticleDetail(id, articleData);
//     return { data: updatedArticle };
//   });
// };

// export const deleteAdminArticle = (id) => {
//   if (!id) return Promise.reject(new Error("Article ID is required"));
//   return simulateDelay(() => {
//     return { data: {} };
//   });
// };

// export const publishAdminArticle = (id) => {
//   if (!id) return Promise.reject(new Error("Article ID is required"));
//   return simulateDelay(() => {
//     const article = generateFakeArticleDetail(id);
//     article.isPublished = true;
//     return { data: article };
//   });
// };

// export const unpublishAdminArticle = (id) => {
//   if (!id) return Promise.reject(new Error("Article ID is required"));
//   return simulateDelay(() => {
//     const article = generateFakeArticleDetail(id);
//     article.isPublished = false;
//     return { data: article };
//   });
// };
import apiClient from "@/http/axios.js";

export const getAdminArticles = (params) => {
  return apiClient.get("api/admin/articles", { params });
};

export const getAdminArticleById = (id) => {
  if (!id) return Promise.reject(new Error("Article ID is required"));
  return apiClient.get(`api/admin/articles/${id}`);
};

export const createAdminArticle = (articleData) => {
  return apiClient.post("api/admin/articles", articleData);
};

export const updateAdminArticle = (id, articleData) => {
  if (!id) return Promise.reject(new Error("Article ID is required"));
  return apiClient.put(`api/admin/articles/${id}`, articleData);
};

export const deleteAdminArticle = (id) => {
  if (!id) return Promise.reject(new Error("Article ID is required"));
  return apiClient.delete(`api/admin/articles/${id}`);
};

export const publishAdminArticle = (id) => {
  if (!id) return Promise.reject(new Error("Article ID is required"));
  return apiClient.patch(`api/admin/articles/${id}/publish`);
};

export const unpublishAdminArticle = (id) => {
  if (!id) return Promise.reject(new Error("Article ID is required"));
  return apiClient.patch(`api/admin/articles/${id}/unpublish`);
};

