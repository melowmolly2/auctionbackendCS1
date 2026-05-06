package com.auction.auctionorchestration;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auction.auth.jwtools.UserDetailsImpl;
import com.auction.bids.dto.BidPostRequest;
import com.auction.bids.dto.BidPostResponse;

import jakarta.validation.Valid;

@RequestMapping
public class AuctionController {
    public final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("")
    public ResponseEntity<BidPostResponse> makeBid(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @Valid @RequestBody BidPostRequest request) {

        BidPostResponse response = auctionService.createBid(request, userDetailsImpl.getUsername());
        return ResponseEntity.ok().body(response);
    }
}
