package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.AddAddressDTO;
import com.example.websitebantuonggolumiwood.dto.OrderNoteDTO;
import com.example.websitebantuonggolumiwood.entity.Addresses;
import com.example.websitebantuonggolumiwood.entity.Order;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.dto.AddressDTO;
import com.example.websitebantuonggolumiwood.dto.UserListDTO;
import com.example.websitebantuonggolumiwood.repository.AddressesRepository;
import com.example.websitebantuonggolumiwood.repository.OrderRepository;
import com.example.websitebantuonggolumiwood.repository.UserManagementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;

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
    private AddressesRepository addressesRepository;

    @Autowired
    private OrderRepository orderRepository;

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
        List<Addresses> addresses =
                addressesRepository.findAll()
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
        Addresses address = new Addresses();
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
        Addresses savedAddress = addressesRepository.save(address);

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
     * Đặt đơn hàng mới với ghi chú cho user.
     * @param userId ID của user.
     * @param orderNoteDTO Thông tin ghi chú từ request.
     * @return OrderNoteDTO của đơn hàng vừa đặt.
     */
//    public OrderNoteDTO placeOrder(Long userId, OrderNoteDTO orderNoteDTO) {
//        User user = userManagementRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user với ID: " + userId));
//
//        // Tạo đơn hàng mới
//        Order order = new Order();
//        order.setUser(user);
//        order.setOrderNote(orderNoteDTO.getOrderNote());
//
//        // Lưu đơn hàng (giả định created_at và updated_at được tự động set trong entity hoặc database)
//        Order savedOrder = orderRepository.save(order);
//
//        OrderNoteDTO result = new OrderNoteDTO();
//        result.setOrderId(savedOrder.getId());
//        result.setOrderNote(savedOrder.getOrderNote());
//
//        return result;
//    }
}
