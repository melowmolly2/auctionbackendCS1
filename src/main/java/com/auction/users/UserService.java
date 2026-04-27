package com.auction.users;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auction.bids.Bid;
import com.auction.bids.BidService;
import com.auction.common.BaseObjectResponse;
import com.auction.items.Item;
import com.auction.items.ItemService;
import com.auction.itemstatus.ItemStatusService;
import com.auction.security.JwtUtil;
import com.auction.users.dto.AuthResponse;
import com.auction.users.dto.BalanceResponse;
import com.auction.users.dto.LoginRequest;
import com.auction.users.dto.RegisterRequest;
import com.auction.users.dto.UserResponse;
import com.auction.users.exceptions.UserException;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ItemService itemService;
    private final ItemStatusService itemStatusService;
    private final BidService bidService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            ItemService itemService, ItemStatusService itemStatusService, BidService bidService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.itemService = itemService;
        this.itemStatusService = itemStatusService;
        this.bidService = bidService;
    }

    @Transactional
    public UserResponse userRegister(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());
        if (userRepository.existsByUsername(request.username())) {
            throw new UserException(false, "Username has already been taken");
        }
        User user = new User(request.username(), request.displayName(), hashedPassword, 0.0);
        user = userRepository.save(user);
        return user.toResponse();
    }

    @Transactional
    public AuthResponse userLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserException(false, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getHashedPassword())) {
            throw new UserException(false, "Invalid username or password");
        }

        return new AuthResponse(true, "Login successful", jwtUtil.generateToken(user.getUsername()));
    }

    @Transactional
    public User getUserReferenceByUsername(String username) {
        User userRef = userRepository.getReferenceById(username);
        return userRef;
    }

    @Transactional
    public User getUserByUsername(String username) {
        User request = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(false, "Invalid username"));
        return request;
    }

    @Transactional
    public BalanceResponse depositCredit(String username, Double creditAmount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(false, "Invalid username"));
        user.setBalance(user.getBalance() + creditAmount);
        user = userRepository.save(user);
        return new BalanceResponse(true,
                "Succesfully deposited credit, current balance",
                user.getBalance());

    }

    @Transactional
    public BalanceResponse getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(false, "Invalid username"));
        return new BalanceResponse(true, "Get balance successful", user.getBalance());
    }

    @Transactional
    public BaseObjectResponse<Page<Bid>> getMyCurrentBids(String username) {
        Page<Bid> bids = bidService.getBidsByUser(username);
        return new BaseObjectResponse<Page<Bid>>(true, "succesfully got my bids", bids);
    }
}
