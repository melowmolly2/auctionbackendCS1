package com.auction.auctionorchestration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auctionorchestration.dto.BidPostRequest;
import com.auction.auctionorchestration.dto.BidPostResponse;
import com.auction.bids.Bid;
import com.auction.bids.BidService;
import com.auction.common.BaseException;
import com.auction.common.BaseObjectResponse;
import com.auction.common.jointdata.BidAndItem;
import com.auction.items.Item;
import com.auction.items.ItemService;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.User;
import com.auction.users.UserService;

@Service
public class AuctionService {
    public final ItemService itemService;
    public final UserService userService;
    public final ItemStatusService itemStatusService;
    public final BidService bidService;

    public AuctionService(ItemService itemService, UserService userService, ItemStatusService itemStatusService,
            BidService bidService) {
        this.itemService = itemService;
        this.userService = userService;
        this.itemStatusService = itemStatusService;
        this.bidService = bidService;
    }

    @Transactional
    public BidPostResponse createBid(BidPostRequest request, String username) {
        Bid bid;
        Item itemRef = itemService.getItemRef(request.itemId());
        User user = userService.getUserRef(username);

        ItemStatus itemStatus = itemStatusService.getItemStatus(request.itemId());

        // big amount must be higher than starting price and bid time must be lower than
        // endtime and bid amount must be higher than current balance
        if (request.bidAmount() < itemStatus.getStartingPrice()
                || Instant.now().toEpochMilli() > itemStatus.getEndTime() || request.bidAmount() > user.getBalance()) {
            throw new BaseException("Failed to bid");
        }

        // if bid exist then get bid from DB and then edit bid and save it again to db
        if (bidService.existUserAndItem(user, itemRef)) {
            bid = bidService.getBidByUserAndItem(user, itemRef);
            bid.setBidAmount(request.bidAmount());
            bidService.saveBid(bid);
        } else { // Else make new bid
            bid = new Bid(itemRef, user, request.bidAmount());
            bidService.saveBid(bid);
        }
        // If user bid amount if higher than the current highest + increment, they would
        // be the highest bidder
        if (request.bidAmount() > itemStatus.getCurrentPrice() + itemStatus.getBidIncrement()) {
            itemStatusService.updateStatus(itemRef, request.bidAmount(), username);
        }
        return new BidPostResponse(true, "Successfully created bid for an item", bid);
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<Page<Bid>> getMyCurrentBids(String username, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        User userRef = userService.getUserRef(username);

        Page<Bid> bids = bidService.getAllUserBid(userRef, pageable);

        return new BaseObjectResponse<Page<Bid>>(true, "succesfully got my bids", bids);
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<List<BidAndItem>> getMyWinnings(String username) {

        List<Bid> bids = bidService.getUserWins(username);
        ArrayList<BidAndItem> items = new ArrayList<BidAndItem>();
        for (Bid bid : bids) {
            items.add(new BidAndItem(bid, bid.getItem()));
        }
        return new BaseObjectResponse<List<BidAndItem>>(true, "sucesfully returned winnings", items);
    }

    // Need to delete everybid before you can cancel the auction.
    // WHen a bid is deleted, the second highest bid will be the current highest
    // bid.
    /*
     * @Transactional
     * public BaseResponse deleteBid(String username, Long itemId, String
     * sellername) {
     * Item item = itemService.getItemRef(itemId);
     * if (!sellername.equals(item.getUser().getUsername())) {
     * throw new BaseException("You are not the seller of this item");
     * }
     * ItemStatus itemStatus = itemStatusService.getItemStatus(itemId);
     * User user = userService.getUserByUsername(username);
     * boolean response = bidService.deleteBid(user, item);
     * if (response) {
     * return new BaseResponse(true, "Succesfully removed bid");
     * } else {
     * throw new BaseException("Failed to delete bid");
     * }
     * }
     */

}
