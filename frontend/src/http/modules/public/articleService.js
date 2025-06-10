let mockClientArticleIdCounter = 5000;

const generateSlug = (title) => {
  return title
    .toLowerCase()
    .replace(/\s+/g, "-")
    .replace(/[^\w-]+/g, "");
};

const generateFakeClientArticleSummary = (idOverride) => {
  const id = idOverride || mockClientArticleIdCounter++;
  const title = `Public Mock Article ${id}`;
  const excerptWords = [
    "Lorem",
    "ipsum",
    "dolor",
    "sit",
    "amet",
    "consectetur",
    "adipiscing",
    "elit",
    "sed",
    "do",
    "eiusmod",
    "tempor",
    "incididunt",
  ];
  let excerpt = "";
  for (let i = 0; i < 15 + Math.floor(Math.random() * 10); i++) {
    excerpt += excerptWords[Math.floor(Math.random() * excerptWords.length)] + " ";
  }

  return {
    id: id,
    title: title,
    slug: generateSlug(title),
    excerpt: excerpt.trim() + "...",
    featuredImageUrl: `https://picsum.photos/seed/public${id}/600/400`,
    publishedAt: new Date(Date.now() - Math.random() * 20000000000).toISOString(), // Random date in the past
    authorName: ["Jane Doe", "John Public", "Alice Writer"][Math.floor(Math.random() * 3)],
    categoryName: ["Tech", "News", "Lifestyle", "Tutorials"][Math.floor(Math.random() * 4)],
  };
};

const generateFakeClientArticleDetail = (slug, inputData = {}) => {
  const baseIdMatch = slug ? slug.match(/\d+$/) : null;
  const baseId = baseIdMatch ? parseInt(baseIdMatch[0]) : mockClientArticleIdCounter++;
  const title = inputData.title || `Public Mock Article Detail for ${slug || baseId}`;

  const contentParagraphs = [
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
    "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.",
    "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.",
    "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.",
  ];
  let fullContent = "";
  for (let i = 0; i < 3 + Math.floor(Math.random() * 3); i++) {
    fullContent += `<p>${
      contentParagraphs[Math.floor(Math.random() * contentParagraphs.length)]
    }</p>\n`;
  }

  return {
    id: inputData.id || baseId,
    title: title,
    slug: inputData.slug || generateSlug(title),
    content: inputData.content || fullContent,
    excerpt: inputData.excerpt || contentParagraphs[0].substring(0, 150) + "...",
    featuredImageUrl:
      inputData.featuredImageUrl || `https://picsum.photos/seed/publicdetail${baseId}/800/500`,
    publishedAt:
      inputData.publishedAt || new Date(Date.now() - Math.random() * 20000000000).toISOString(),
    updatedAt: new Date().toISOString(),
    author: {
      name: "Mock Author Name",
      avatarUrl: `https://i.pravatar.cc/150?u=author${baseId}`,
    },
    tags: ["mock", "public", "sample", "generated"],
    category: {
      id: Math.floor(Math.random() * 10) + 1,
      name: ["Technology", "Science", "World News", "Opinion"][Math.floor(Math.random() * 4)],
    },
    relatedArticles: Array.from({ length: Math.floor(Math.random() * 3) + 2 }, () =>
      generateFakeClientArticleSummary()
    ),
    ...inputData,
  };
};

const simulateDelay = (callback, delayMs = 300 + Math.random() * 400) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(callback());
    }, delayMs);
  });
};

// export const getArticles = (params = {}) => {
//   return simulateDelay(() => {
//     const pageSize = params.size || 9;
//     const pageNumber = params.page || 1;
//     const totalElements = 42;
//     const totalPages = Math.ceil(totalElements / pageSize);
//     const articles = Array.from({ length: Math.min(pageSize, totalElements - (pageNumber - 1) * pageSize) }, (_, i) =>
//       generateFakeClientArticleSummary(mockClientArticleIdCounter + i)
//     );
//     mockClientArticleIdCounter += articles.length;

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
//         number: pageNumber - 1,
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
import axios from "axios";

export const getArticles = (params = {}) => {
  return axios.get("/api/public/articles/getListArticles", { params });
};

// export const getArticleBySlug = (slug) => {
//   if (!slug) return Promise.reject(new Error("Article slug is required"));
//   return simulateDelay(() => {
//     if (Math.random() < 0.05 && slug !== "a-test-slug") {
//       return Promise.reject({ response: { status: 404, data: { message: "Mock Public Article not found" } } });
//     }
//     return { data: generateFakeClientArticleDetail(slug) };
//   });
// };

// export const getArticleBySlug = (slug) => {
//   return axios.get(`/api/public/articles/${slug}`);
// };

import instance from "@/http/axios";
export async function getArticleBySlug(slug) {
  const cleanSlug = slug.trim();
  console.log("Fetching article with slug:", cleanSlug); // ✅ log debug
  return await instance.get(`/api/public/articles/${cleanSlug}`);
}

export const createArticle = (articleData) => {
  return simulateDelay(() => {
    const newId = mockClientArticleIdCounter++;
    const newArticle = generateFakeClientArticleDetail(null, {
      id: newId,
      title: articleData.title,
      content: articleData.content,
      excerpt: articleData.excerpt,
      featuredImageUrl: articleData.featuredImageUrl,
      publishedAt: new Date().toISOString(),
      slug: generateSlug(articleData.title || `new-article-${newId}`),
    });
    newArticle.status = "PENDING_APPROVAL_MOCK";
    return { data: newArticle };
  });
};
