import {createRouter, createWebHistory} from 'vue-router';
import {useAuthStore} from '@/store/auth';
import HomeView from '../views/public/HomeView.vue';
import ProductListView from '../views/public/ProductListView.vue';
import ProductDetailView from '../views/public/ProductDetailView.vue';
import ContactView from '../views/public/ContactView.vue';
import LoginView from '../views/auth/LoginView.vue';
import RegisterView from '../views/auth/RegisterView.vue';
import ArticleListView from '../views/public/ArticleListView.vue';
import ArticleDetailView from '../views/public/ArticleDetailView.vue';
import CreateArticleView from '../views/user/CreateArticleView.vue';
import ShoppingCartView from '../views/public/ShoppingCartView.vue';
import CheckoutView from '../views/public/CheckoutView.vue';
import OrderSuccessView from '../views/public/OrderSuccessView.vue';
import UserProfileView from "@/views/user/UserProfileView.vue";
import UserOrderDetailView from '../views/user/UserOrderDetailView.vue';
import OrderHistoryView from '../views/user/OrderHistoryView.vue';
import AdminArticleListView from '../views/admin/AdminArticleListView.vue';
import AdminArticleFormView from '../views/admin/AdminArticleFormView.vue';

import AdminDashboardView from '../views/admin/adminDashboardView.vue';
import AdminOrderListView from '../views/admin/AdminOrderListView.vue';
import AdminOrderDetailView from '../views/admin/AdminOrderDetailView.vue';
import AdminProductListView from '../views/admin/AdminProductListView.vue';
import AdminProductFormView from '../views/admin/AdminProductFormView.vue';
import AdminUserListView from '../views/admin/AdminUserListView.vue';
import AdminUserDetailView from '../views/admin/AdminUserDetailView.vue';
import AdminShippingMethodListView from '../views/admin/AdminShippingMethodListView.vue';
import AdminShippingMethodFormView from '../views/admin/AdminShippingMethodFormView.vue';
import AdminPromotionListView from '../views/admin/AdminPromotionListView.vue';
import AdminPromotionFormView from '../views/admin/AdminPromotionFormView.vue';
import AdminLayout from '../components/layout/AdminLayout.vue';

const routes = [
  {path: '/', name: 'home', component: HomeView},
  {path: '/products', name: 'productList', component: ProductListView},
  {
    path: '/category/:categorySlug',
    name: 'productListByCategory',
    component: ProductListView,
    props: true
  },
  {path: '/product/:slug', name: 'productDetail', component: ProductDetailView, props: true},
  {path: '/contact', name: 'contact', component: ContactView},
  {path: '/articles', name: 'articleList', component: ArticleListView},
  {path: '/article/:slug', name: 'articleDetail', component: ArticleDetailView, props: true},
  {path: '/cart', name: 'shoppingCart', component: ShoppingCartView},

  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {requiresGuest: true}
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {requiresGuest: true}
  },

  {
    path: '/articles/create',
    name: 'createArticle',
    component: CreateArticleView,
    meta: {requiresAuth: true}
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: CheckoutView,
    meta: {requiresAuth: true}
  },
  {
    path: '/order-success/:orderId',
    name: 'orderSuccess',
    component: OrderSuccessView,
    props: true,
    meta: {requiresAuth: true}
  },
  {
    path: '/my-profile',
    name: 'userProfile',
    component: UserProfileView,
    meta: {requiresAuth: true}
  },
  {
    path: '/my-orders',
    name: 'orderHistory',
    component: OrderHistoryView,
    meta: {requiresAuth: true}
  },
  {
    path: '/my-orders/:orderId',
    name: 'userOrderDetail',
    component: UserOrderDetailView,
    props: true,
    meta: {requiresAuth: true}
  },

  {
    path: '/admin',
    component: AdminLayout,
    meta: {requiresAuth: true, requiresAdmin: true},
    children: [
      {path: '', redirect: {name: 'adminDashboard'}},
      {
        path: 'dashboard',
        name: 'adminDashboard',
        component: AdminDashboardView,
      },
      {
        path: 'orders',
        name: 'adminOrderList',
        component: AdminOrderListView,
      },
      {
        path: 'orders/:orderId',
        name: 'adminOrderDetail',
        component: AdminOrderDetailView,
        props: true,
      },
      {
        path: 'products',
        name: 'adminProductList',
        component: AdminProductListView,
      },
      {
        path: 'products/new',
        name: 'adminProductNew',
        component: AdminProductFormView,
      },
      {
        path: 'products/:productId/edit',
        name: 'adminProductEdit',
        component: AdminProductFormView,
        props: true,
      },
      {
        path: 'users',
        name: 'adminUserList',
        component: AdminUserListView,
      },
      {
        path: 'users/:userId',
        name: 'adminUserDetail',
        component: AdminUserDetailView,
        props: true,
      },
      {
        path: 'shipping-methods',
        name: 'adminShippingMethodList',
        component: AdminShippingMethodListView,
      },
      {
        path: 'shipping-methods/new',
        name: 'adminShippingMethodNew',
        component: AdminShippingMethodFormView,
      },
      {
        path: 'shipping-methods/:id/edit',
        name: 'adminShippingMethodEdit',
        component: AdminShippingMethodFormView,
        props: true,
      },
      {
        path: 'promotions',
        name: 'adminPromotionList',
        component: AdminPromotionListView,
        meta: {requiresAuth: true, requiresAdmin: true}
      },
      {
        path: 'promotions/new',
        name: 'adminPromotionNew',
        component: AdminPromotionFormView,
        meta: {requiresAuth: true, requiresAdmin: true}
      },
      {
        path: 'promotions/:id/edit',
        name: 'adminPromotionEdit',
        component: AdminPromotionFormView,
        props: true,
        meta: {requiresAuth: true, requiresAdmin: true}
      },
      {
        path: 'articles',
        name: 'adminArticleList',
        component: AdminArticleListView,
        meta: {requiresAuth: true, requiresAdmin: true}
      },
      {
        path: 'articles/new',
        name: 'adminArticleNew',
        component: AdminArticleFormView,
        meta: {requiresAuth: true, requiresAdmin: true}
      },
      {
        path: 'articles/:id/edit',
        name: 'adminArticleEdit',
        component: AdminArticleFormView,
        props: true,
        meta: {requiresAuth: true, requiresAdmin: true}
      },
    ]
  },

  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    component: () => import('../views/public/NotFoundView.vue')
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || {top: 0};
  }
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);

  if (requiresAuth && !authStore.isAuthenticated) {
    authStore.setReturnUrl(to.fullPath);
    return next({name: 'login'});
  }

  if (requiresAdmin && !authStore.isAdmin) {
    if (!authStore.isAuthenticated) {
      authStore.setReturnUrl(to.fullPath);
      return next({name: 'login'});
    } else {
      return next({name: 'home'});
    }
  }

  if (requiresGuest && authStore.isAuthenticated) {
    return next({name: authStore.isAdmin ? 'adminDashboard' : 'home'});
  }

  next();
});

export default router;
