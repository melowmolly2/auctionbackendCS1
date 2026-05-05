package com.auction.users;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auth.RefreshToken;
import com.auction.auth.RefreshTokenRepository;
import com.auction.auth.jwtools.JwtUtil;
import com.auction.bids.Bid;
import com.auction.bids.BidRepository;
import com.auction.common.BaseException;
import com.auction.common.BaseObjectResponse;
import com.auction.common.jointdata.BidAndItem;
import com.auction.users.dto.AuthResponse;
import com.auction.users.dto.BalanceResponse;
import com.auction.users.dto.LoginRequest;
import com.auction.users.dto.RegisterRequest;
import com.auction.users.dto.UserResponse;

@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BidRepository bidRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.bidRepository = bidRepository;
    }

    public UserResponse userRegister(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BaseException("Username has already been taken");
        }
        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.displayName(), hashedPassword, 0.0);
        user = userRepository.save(user);
        return user.toResponse();
    }

    @Transactional
    public AuthResponse userLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BaseException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getHashedPassword())) {
            throw new BaseException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Save the refresh token to the database
        refreshTokenRepository.save(new RefreshToken(user.getUsername(), refreshToken));

        return new AuthResponse(true, "Login successful", token, refreshToken);
    }

    @Transactional
    public User getUserReferenceByUsername(String username) {
        User userRef = userRepository.getReferenceById(username);
        return userRef;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        User request = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException("Invalid username"));
        return request;
    }

    @Transactional
    public BalanceResponse depositCredit(String username, Double creditAmount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException("Invalid username"));
        user.setBalance(user.getBalance() + creditAmount);
        user = userRepository.save(user);
        return new BalanceResponse(true,
                "Succesfully deposited credit, current balance",
                user.getBalance());

    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException("Invalid username"));
        return new BalanceResponse(true, "Get balance successful", user.getBalance());
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<Page<Bid>> getMyCurrentBids(String username, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        User userRef = userRepository.getReferenceById(username);

        Page<Bid> bids = bidRepository.findAllByUser(userRef, pageable);

        return new BaseObjectResponse<Page<Bid>>(true, "succesfully got my bids", bids);
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<List<BidAndItem>> getMyWinnings(String username) {

        List<Bid> bids = bidRepository.getWinsByUser(username, Instant.now().getEpochSecond());
        ArrayList<BidAndItem> items = new ArrayList<BidAndItem>();
        for (Bid bid : bids) {
            items.add(new BidAndItem(bid, bid.getItem()));
        }
        return new BaseObjectResponse<List<BidAndItem>>(true, "sucesfully returned winnings", items);

    }
}
