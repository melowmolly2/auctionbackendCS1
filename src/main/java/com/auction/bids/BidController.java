package com.auction.bids;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auction.auth.jwtools.UserDetailsImpl;
import com.auction.common.BaseObjectResponse;
import com.auction.common.BaseResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/bids")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/buy-now/{itemId}")
    public ResponseEntity<BaseResponse> buyNow(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @PathVariable Long itemId) {
        BaseResponse response = bidService.buyItemNow(itemId, userDetailsImpl.getUsername());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{itemId}/bids")
    public ResponseEntity<BaseObjectResponse<Page<Bid>>> getBids(
            @PathVariable Long itemId,
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(20) @RequestParam(defaultValue = "10") int size) {
        BaseObjectResponse<Page<Bid>> response = bidService.getBidsOnItem(itemId, page, size);
        return ResponseEntity.ok().body(response);
    }
}