package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.AddAddressDTO;
import com.example.websitebantuonggolumiwood.entity.Address;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.dto.AddressDTO;
import com.example.websitebantuonggolumiwood.dto.UserListDTO;
import com.example.websitebantuonggolumiwood.repository.AddressRepository;
import com.example.websitebantuonggolumiwood.repository.OrderRepository;
import com.example.websitebantuonggolumiwood.repository.UserManagementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);


    // --- Ngưỡng chi tiêu cho các bậc ---
    private static final BigDecimal BRONZE_TIER_THRESHOLD = BigDecimal.ZERO;                  // 0
    private static final BigDecimal SILVER_TIER_THRESHOLD = new BigDecimal("10000000");       // 10 triệu
    private static final BigDecimal GOLD_TIER_THRESHOLD = new BigDecimal("30000000");         // 30 triệu
    private static final BigDecimal DIAMOND_TIER_THRESHOLD = new BigDecimal("100000000");     // 100 triệu


    /**
     * Lấy danh sách user với phân trang, filter theo tier, trạng thái isActive, tìm kiếm username hoặc fullName
     */
    public Page<UserListDTO> getUsers(String tier, String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc user có role = "CUSTOMER"
            predicates.add(cb.equal(root.get("role"), "CUSTOMER"));

            // Lọc theo tier nếu không phải all
            if (!"all".equalsIgnoreCase(tier)) {
                predicates.add(cb.equal(root.get("tier"), tier.toUpperCase()));
            }

            // Lọc theo status
            if (!"all".equalsIgnoreCase(status)) {
                boolean active = status.equalsIgnoreCase("active");
                predicates.add(cb.equal(root.get("isActive"), active));
            }

            // Tìm kiếm theo username hoặc fullName
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate usernamePredicate = cb.like(cb.lower(root.get("username")), pattern);
                Predicate fullNamePredicate = cb.like(cb.lower(root.get("fullName")), pattern);
                predicates.add(cb.or(usernamePredicate, fullNamePredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userManagementRepository.findAll(spec, pageable);

        // Map User entity -> UserListDTO
        List<UserListDTO> dtoList = userPage.stream().map(user -> {
            UserListDTO dto = new UserListDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setFullName(user.getFullName());
            dto.setTier(user.getTier());
            dto.setTotalSpent(user.getTotalSpent());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setIsActive(user.getIsActive());
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }


    /**
     * Lấy chi tiết user theo userId (không gồm địa chỉ hoặc đơn hàng)
     */
    public UserListDTO getUserProfile(Long userId) {
        Optional<User> userOpt = userManagementRepository.findById(userId);
        if (userOpt.isEmpty()) return null;

        User user = userOpt.get();

        UserListDTO dto = new UserListDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setTier(user.getTier());
        dto.setTotalSpent(user.getTotalSpent());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setIsActive(user.getIsActive());

        return dto;
    }

    /**
     * Cập nhật trạng thái tài khoản user
     */
    public boolean updateUserStatus(Long userId, Boolean isActive) {
        Optional<User> userOpt = userManagementRepository.findById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setIsActive(isActive);
        userManagementRepository.save(user);
        return true;
    }

    /**
     * Lấy danh sách địa chỉ giao hàng user
     */
    public List<AddressDTO> getAddresses(Long userId) {
        List<Address> addresses =
                addressRepository.findAll()
                        .stream()
                        .filter(a -> a.getUser() != null && a.getUser().getUserId().equals(userId))
                        .collect(Collectors.toList());

        return addresses.stream().map(a -> {
            AddressDTO dto = new AddressDTO();
            dto.setId(a.getId());
            dto.setRecipientName(a.getRecipientName());
            dto.setRecipientPhone(a.getRecipientPhone());
            dto.setStreetAddress(a.getStreetAddress());
            dto.setWard(a.getWard());
            dto.setDistrict(a.getDistrict());
            dto.setCity(a.getCity());
            dto.setIsDefaultShipping(a.getIsDefaultShipping());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Cập nhật bậc khách hàng (tier) cho user theo userId
     * @param userId ID của user cần cập nhật
     * @param newTier Bậc khách hàng mới (BRONZE, SILVER, GOLD, DIAMOND)
     * @return true nếu cập nhật thành công, false nếu không tìm thấy user
     */
    public boolean updateUserTier(Long userId, String newTier) {
        Optional<User> optionalUser = userManagementRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return false;  // không tìm thấy user
        }

        User user = optionalUser.get();
        user.setTier(newTier); // set bậc khách hàng mới
        userManagementRepository.save(user); // lưu lại database

        return true;
    }
    /**
     * Thêm địa chỉ giao hàng cho user
     * @param userId ID của user
     * @param addressDTO Thông tin địa chỉ giao hàng
     * @return AddressDTO của địa chỉ vừa thêm
     */
    public AddressDTO addAddress(Long userId, AddAddressDTO addressDTO) {
        // Kiểm tra user tồn tại
        User user = userManagementRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user với ID: " + userId));

        // Tạo địa chỉ mới
        Address address = new Address();
        address.setUser(user);
        address.setRecipientName(addressDTO.getRecipientName());
        address.setRecipientPhone(addressDTO.getRecipientPhone());
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setWard(addressDTO.getWard());
        address.setDistrict(addressDTO.getDistrict());
        address.setCity(addressDTO.getCity());
        address.setCountry("VN"); // Set giá trị mặc định cho country
        address.setCreatedAt(LocalDateTime.now()); // Thiết lập thời gian tạo
        address.setUpdatedAt(LocalDateTime.now()); // Thiết lập thời gian cập nhật

        // Lưu địa chỉ
        Address savedAddress = addressRepository.save(address);

        // Chuyển đổi sang DTO để trả về
        AddressDTO result = new AddressDTO();
        result.setId(savedAddress.getId());
        result.setRecipientName(savedAddress.getRecipientName());
        result.setRecipientPhone(savedAddress.getRecipientPhone());
        result.setStreetAddress(savedAddress.getStreetAddress());
        result.setWard(savedAddress.getWard());
        result.setDistrict(savedAddress.getDistrict());
        result.setCity(savedAddress.getCity());

        return result;
    }

    /**
     * Cập nhật tổng chi tiêu và bậc khách hàng (gọi khi đơn hàng hoàn tất thanh toán).
     *
     * @param userId      ID của khách hàng
     * @param orderAmount Số tiền đơn hàng mới
     */
    @Transactional
    public void updateTotalSpentAndTier(Long userId, BigDecimal orderAmount) {
        if (userId == null || orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Dữ liệu đầu vào không hợp lệ khi cập nhật tổng chi tiêu. userId: {}, amount: {}", userId, orderAmount);
            return;
        }

        Optional<User> optionalUser = userManagementRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            logger.warn("Không tìm thấy user với ID: {}", userId);
            return;
        }

        User user = optionalUser.get();

        // Chỉ cập nhật cho user có vai trò là CUSTOMER
        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            logger.warn("User ID {} không phải là CUSTOMER (role = {}), không cập nhật.", userId, user.getRole());
            return;
        }

        // Cộng thêm số tiền của đơn hàng mới vào tổng chi tiêu hiện tại
        BigDecimal currentTotal = user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO;
        BigDecimal newTotalSpent = currentTotal.add(orderAmount);
        user.setTotalSpent(newTotalSpent);

        logger.info("User ID {}: Tổng chi tiêu tăng từ {} → {}", userId, currentTotal, newTotalSpent);

        // Tính toán bậc mới dựa trên tổng chi tiêu mới
        String newTier = calculateCustomerTier(newTotalSpent);

        // Nếu bậc mới khác bậc hiện tại thì cập nhật
        if (!newTier.equalsIgnoreCase(user.getTier())) {
            logger.info("User ID " + userId + " chuyển bậc từ " + user.getTier() + " sang " + newTier);
            user.setTier(newTier);
        }
        else {
            logger.debug("User ID {} vẫn giữ nguyên bậc {} sau khi cập nhật.", userId, newTier);
        }

        // Lưu lại thông tin user
        userManagementRepository.save(user);
        logger.debug("User ID {} đã được lưu với tổng chi tiêu mới: {} và bậc: {}", userId, newTotalSpent, user.getTier());
    }

    /**
     * Tính toán bậc khách hàng dựa trên tổng chi tiêu.
     * @param totalSpent Tổng số tiền đã chi tiêu.
     * @return Mã bậc khách hàng (String).
     */
    private String calculateCustomerTier(BigDecimal totalSpent) {
        if (totalSpent == null) {
            return "BRONZE";
        }

        if (totalSpent.compareTo(DIAMOND_TIER_THRESHOLD) >= 0) {
            return "DIAMOND";
        } else if (totalSpent.compareTo(GOLD_TIER_THRESHOLD) >= 0) {
            return "GOLD";
        } else if (totalSpent.compareTo(SILVER_TIER_THRESHOLD) >= 0) {
            return "SILVER";
        } else {
            return "BRONZE";
        }
    }
}
