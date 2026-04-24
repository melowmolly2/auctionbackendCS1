package com.auction.bids.bidsdetails.dto;

import com.auction.bids.bidsdetails.Bid;
import com.auction.common.BaseResponse;

public class BidPostResponse extends BaseResponse {
    Bid bid;

    public BidPostResponse(boolean status, String message, Bid bid) {
        super(status, message);
        this.bid = bid;
    }

    public Bid getBid() {
        return bid;
    }

}
