package com.auction.bids.bidsdetails;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auction.bids.bidsdetails.dto.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bids")
public class BidDetailsController {
    private final BidService bidService;

    public BidDetailsController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("")
    public ResponseEntity<BidPostResponse> makeBid(@Valid @RequestBody BidPostRequest request) {
        BidPostResponse response = bidService.createBid(request);
        return ResponseEntity.ok().body(response);
    }
}