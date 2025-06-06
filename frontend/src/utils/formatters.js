export const formatCurrency = (value) => {
  const number = Number(value);
  if (isNaN(number)) {
    return '0 ₫';
  }
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(number);
};

export const formatDate = (dateInput) => {
  if (!dateInput) return '';
  try {
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) {
      return '';
    }
    return date.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  } catch (e) {
    return '';
  }
};

export const formatDateTime = (dateInput) => {
  if (!dateInput) return '';
  try {
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) {
      return '';
    }
    return date.toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return '';
  }
};

export const formatDateRelative = (dateInput) => {
  if (!dateInput) return '';
  try {
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) return '';

    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    const diffInDays = Math.floor(diffInSeconds / (60 * 60 * 24));

    if (diffInSeconds < 0) return formatDate(date);
    if (diffInDays === 0) {
      if (diffInSeconds < 60) return `vài giây trước`;
      const diffInMinutes = Math.floor(diffInSeconds / 60);
      if (diffInMinutes < 60) return `${diffInMinutes} phút trước`;
      const diffInHours = Math.floor(diffInSeconds / (60 * 60));
      return `${diffInHours} giờ trước`;
    }
    if (diffInDays === 1) return 'Hôm qua';
    if (diffInDays < 7) return `${diffInDays} ngày trước`;
    return formatDate(date);
  } catch (e) {
    return '';
  }
};

export const formatStatus = (status) => {
  if (typeof status !== 'string' || !status) {
    return 'Không rõ';
  }
  const statusMap = {
    'PENDING': 'Chờ xác nhận',
    'PROCESSING': 'Đang xử lý',
    'SHIPPING': 'Đang giao hàng',
    'COMPLETED': 'Đã hoàn thành',
    'CANCELLED': 'Đã hủy',
    'PAYMENT_PENDING': 'Chờ thanh toán',
    'DELIVERED': 'Đã giao hàng',
    'RETURNED': 'Hoàn trả',
    'AWAITING_CONFIRMATION': 'Chờ xác nhận (Mock)',
    'PENDING_PAYMENT': 'Chờ thanh toán (Mock)',
    'CANCELLED_BY_USER': 'Đã hủy bởi người dùng (Mock)',
  };
  return statusMap[status.toUpperCase()] || status;
};

export const getStatusClass = (status) => {
  if (!status) return 'text-bg-light text-dark border';
  const classMap = {
    'PENDING': 'text-bg-secondary',
    'PROCESSING': 'text-bg-info',
    'SHIPPING': 'text-bg-primary',
    'COMPLETED': 'text-bg-success',
    'CANCELLED': 'text-bg-danger',
    'PAYMENT_PENDING': 'text-bg-warning text-dark',
    'DELIVERED': 'text-bg-success bg-opacity-75',
    'RETURNED': 'text-bg-dark',
    'AWAITING_CONFIRMATION': 'text-bg-warning text-dark',
    'PENDING_PAYMENT': 'text-bg-warning text-dark',
    'CANCELLED_BY_USER': 'text-bg-danger bg-opacity-75',
  };
  return classMap[status.toUpperCase()] || 'text-bg-light text-dark border';
};

export const allStatuses = [
  'PENDING',
  'PROCESSING',
  'SHIPPING',
  'COMPLETED',
  'CANCELLED',
  'PAYMENT_PENDING',
  'DELIVERED',
  'RETURNED',
  'AWAITING_CONFIRMATION',
  'PENDING_PAYMENT',
  'CANCELLED_BY_USER',
];

export const validTransitions = {
  'PENDING': ['PROCESSING', 'CANCELLED'],
  'AWAITING_CONFIRMATION': ['PROCESSING', 'CANCELLED_BY_USER'],
  'PENDING_PAYMENT': ['PROCESSING', 'CANCELLED_BY_USER'],
  'PROCESSING': ['SHIPPING', 'CANCELLED'],
  'SHIPPING': ['DELIVERED', 'CANCELLED'],
  'DELIVERED': ['COMPLETED', 'RETURNED'],
  'COMPLETED': [],
  'CANCELLED': [],
  'CANCELLED_BY_USER': [],
  'RETURNED': [],
};

export const formatTier = (tier) => {
  if (!tier) return 'Chưa xếp hạng';
  const tierMap = {
    'BRONZE': 'Đồng',
    'SILVER': 'Bạc',
    'GOLD': 'Vàng',
    'PLATINUM': 'Bạch Kim',
    'DIAMOND': 'Kim Cương',
    'UNRANKED': 'Chưa xếp hạng',
    'BẠCH KIM': 'Bạch Kim',
  };
  return tierMap[tier.toUpperCase()] || tier;
};

export const getTierClass = (tier) => {
  if (!tier) return 'bg-light text-dark border';
  const tierMap = {
    'BRONZE': 'bg-bronze text-white',
    'SILVER': 'bg-silver text-dark',
    'GOLD': 'bg-warning text-dark',
    'PLATINUM': 'bg-platinum text-dark',
    'DIAMOND': 'bg-diamond text-white',
    'UNRANKED': 'bg-light text-dark border',
    'BẠCH KIM': 'bg-platinum text-dark',
  };
  return tierMap[tier.toUpperCase()] || 'bg-light text-dark border';
};

export const allTiers = [
  {code: 'UNRANKED', name: 'Chưa xếp hạng'},
  {code: 'BRONZE', name: 'Đồng'},
  {code: 'SILVER', name: 'Bạc'},
  {code: 'GOLD', name: 'Vàng'},
  {code: 'PLATINUM', name: 'Bạch Kim'},
  {code: 'DIAMOND', name: 'Kim Cương'}
];
