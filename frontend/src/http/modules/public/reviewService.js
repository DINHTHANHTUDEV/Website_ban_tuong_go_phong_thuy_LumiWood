// const MOCK_REVIEWER_NAMES = [
//   "Nguyễn Văn An", "Trần Thị Bình", "Lê Minh Cường", "Phạm Thị Dung", "Hoàng Văn Em",
//   "Vũ Thị Phúc", "Đỗ Gia Hưng", "Bùi Thanh Mai", "Đặng Quốc Tuấn", "Hồ Ngọc Lan"
// ];

// const MOCK_COMMENTS_PHONGTHUY = [
//   "Tượng rất đẹp, đường nét chạm khắc tinh xảo, nhìn rất có hồn. Giao hàng nhanh, đóng gói cẩn thận.",
//   "Chất liệu gỗ tốt, mùi thơm nhẹ nhàng tự nhiên. Tôi rất hài lòng với sản phẩm này, sẽ ủng hộ shop dài dài.",
//   "Mang lại cảm giác rất an yên khi đặt trong nhà. Tượng Phật Di Lặc đúng như mô tả, khuôn mặt phúc hậu.",
//   "Tượng Tỳ Hưu oai phong, chi tiết sắc nét. Hy vọng sẽ mang lại nhiều may mắn và tài lộc cho gia đình.",
//   "Kích thước hơi nhỏ hơn so với tưởng tượng của tôi một chút, nhưng tổng thể vẫn rất ổn, gỗ đẹp.",
//   "Shop tư vấn nhiệt tình, sản phẩm chất lượng. Tượng Quan Công nhìn rất uy nghiêm, hợp với không gian phòng làm việc của tôi.",
//   "Đã nhận được tượng Thiềm Thừ, nhìn rất sinh động. Mong là công việc làm ăn sẽ thuận lợi hơn.",
//   "Màu gỗ tự nhiên rất đẹp, vân gỗ rõ ràng. Tượng Tam Đa Phúc Lộc Thọ mang ý nghĩa tốt lành.",
//   "Đóng gói siêu kỹ, tượng không bị trầy xước gì. Chất lượng xứng đáng với giá tiền.",
//   "Hài lòng với trải nghiệm mua hàng tại shop. Tượng gỗ trầm hương thơm dịu, giúp thư giãn tinh thần."
// ];

// let mockReviewIdCounter = 9000;

// const generateFakeReview = (productId, reviewDataInput = {}) => {
//   const id = mockReviewIdCounter++;
//   const rating = reviewDataInput.rating || (Math.floor(Math.random() * 3) + 3); // 3 to 5 stars
//   const commentIndex = Math.floor(Math.random() * MOCK_COMMENTS_PHONGTHUY.length);
//   let comment = reviewDataInput.comment || MOCK_COMMENTS_PHONGTHUY[commentIndex];
//   if (rating < 4 && (!reviewDataInput.comment || reviewDataInput.comment.length < 10)) {
//     comment = ["Chất lượng cần cải thiện thêm.", "Hơi thất vọng một chút.", "Không như kỳ vọng lắm."][Math.floor(Math.random()*3)];
//   }


//   return {
//     id: id,
//     productId: productId,
//     rating: rating,
//     comment: comment,
//     reviewerName: reviewDataInput.reviewerName || MOCK_REVIEWER_NAMES[Math.floor(Math.random() * MOCK_REVIEWER_NAMES.length)],
//     reviewDate: reviewDataInput.reviewDate || new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(), // within last 30 days
//     isVerifiedPurchase: Math.random() > 0.3,
//     // adminReply: Math.random() > 0.7 ? { replyText: "Cảm ơn bạn đã đánh giá sản phẩm!", replyDate: new Date().toISOString() } : null,
//   };
// };

// const simulateDelay = (dataFn, delayMs = 200 + Math.random() * 300) => {
//   return new Promise((resolve, reject) => {
//     setTimeout(() => {
//       try {
//         const result = dataFn();
//         resolve({ data: result });
//       } catch (error) {
//         reject(error);
//       }
//     }, delayMs);
//   });
// };


// export const getProductReviews = (productId, params = {}) => {
//   if (!productId) return Promise.reject(new Error("Product ID is required"));

//   return simulateDelay(() => {
//     const pageSize = parseInt(params.size, 10) || 5;
//     const pageNumber = parseInt(params.page, 10) || 1;
//     const totalElements = Math.floor(Math.random() * 15) + 5; // 5 to 20 reviews per product
//     const totalPages = Math.ceil(totalElements / pageSize);

//     const startIndex = (pageNumber - 1) * pageSize;
//     const reviews = [];
//     for(let i=0; i < totalElements; i++){
//       if(i >= startIndex && reviews.length < pageSize){
//         reviews.push(generateFakeReview(productId));
//       }
//     }

//     return {
//       content: reviews,
//       pageable: {
//         pageNumber: pageNumber,
//         pageSize: pageSize,
//         sort: params.sort ? { sorted: true, unsorted: false, empty: false } : { sorted: false, unsorted: true, empty: true },
//         offset: startIndex,
//         paged: true,
//         unpaged: false,
//       },
//       last: pageNumber >= totalPages,
//       totalPages: totalPages,
//       totalElements: totalElements,
//       size: pageSize,
//       number: pageNumber -1,
//       sort: params.sort ? { sorted: true, unsorted: false, empty: false, orders: [{property: params.sort.split(',')[0], direction: params.sort.split(',')[1].toUpperCase() }] } : { sorted: false, unsorted: true, empty: true },
//       first: pageNumber === 1,
//       numberOfElements: reviews.length,
//       empty: reviews.length === 0,
//     };
//   });
// };

// export const createProductReview = (productId, reviewData) => {
//   if (!productId) return Promise.reject(new Error("Product ID is required"));
//   if (!reviewData || reviewData.rating === undefined || reviewData.rating === null) {
//     return Promise.reject(new Error("Rating is required to submit a review."));
//   }
//   if (reviewData.rating < 1 || reviewData.rating > 5) {
//     return Promise.reject(new Error("Rating must be between 1 and 5."));
//   }

//   return simulateDelay(() => {
//     const newReview = generateFakeReview(productId, {
//       ...reviewData,
//       reviewerName: "Khách Hàng (Bạn)",
//       reviewDate: new Date().toISOString(),
//     });
//     newReview.isPendingApproval = true; // Simulate review pending approval
//     return newReview;
//   });
// };
import axios from "axios";

export function getProductReviews(productId, params = { page: 0, size: 5 }) {
  return axios.get(`http://localhost:8080/api/productReview/${productId}`, {
    params: params,
  });
}


// export function createProductReview(productId, review) {
//   return axios.post(`http://localhost:8080/api/productReview/add/${productId}`, review);
//   withCredentials: true
// }
export function createProductReview(productId, review) {
  const token = localStorage.getItem('token'); // hoặc từ Pinia/Vuex nếu bạn dùng

  return axios.post(
    `http://localhost:8080/api/productReview/add/${productId}`,
    review,
    {
      withCredentials: true,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
}



