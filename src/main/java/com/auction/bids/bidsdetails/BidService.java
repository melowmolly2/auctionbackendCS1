package com.auction.bids.bidsdetails;

import org.springframework.stereotype.Service;

import com.auction.bids.bidsdetails.dto.BidPostRequest;
import com.auction.bids.bidsdetails.dto.BidPostResponse;
import com.auction.items.Item;
import com.auction.items.ItemService;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.User;
import com.auction.users.UserService;

import jakarta.transaction.Transactional;

@Service
public class BidService {
    // Don't inject repository, inject service instead. Refactor tomorrow.

    private final BidRepository bidRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemStatusService itemStatusService;

    public BidService(BidRepository bidRepository, UserService userService, ItemService itemService,
            ItemStatusService itemStatusService) {
        this.bidRepository = bidRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.itemStatusService = itemStatusService;
    }

    @Transactional
    public BidPostResponse createBid(BidPostRequest request) {
        Bid bid;
        Item itemRef = itemService.getItemReferenceByItemId(request.itemId());
        User userRef = userService.getUserReferenceByUsername(request.username());

        // if bid exist then get bid from DB and then edit bid and save it again to db
        if (bidRepository.existsByUserAndItem(userRef, itemRef)) {
            bid = bidRepository.findByUserAndItem(userRef, itemRef);
            bid.setBidAmount(request.bidAmount());
            bidRepository.save(bid);
        } else { // Else make new bid
            bid = new Bid(itemRef, userRef, request.bidAmount());
            bidRepository.save(bid);
        }
        ItemStatus itemStatus = itemStatusService.getItemStatus(request.itemId());
        // If user bid amount if higher than the current highest + increment, they would
        // be the highest bidder
        if (request.bidAmount() > itemStatus.getCurrentPrice() + itemStatus.getBidIncrement()) {
            itemStatusService.updateStatus(itemRef, request.bidAmount(), request.username());
        }
        return new BidPostResponse(true, "Successfully created bid for an item", bid);
    }

}
