package com.auction.auctionorchestration;

import java.time.Instant;
import java.util.ArrayList;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auctionorchestration.dto.BidPostRequest;
import com.auction.auctionorchestration.dto.BidPostResponse;
import com.auction.bids.Bid;
import com.auction.common.BaseException;
import com.auction.common.BaseObjectResponse;
import com.auction.common.jointdata.BidAndItem;
import com.auction.items.ItemService;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.UserService;

@Service
public class AuctionService {
    public final ItemService itemService;
    public final UserService userService;
    public final ItemStatusService itemStatusService;

    public AuctionService(ItemService itemService, UserService userService, ItemStatusService itemStatusService) {
        this.itemService = itemService;
        this.userService = userService;
        this.itemStatusService = itemStatusService;
    }

    @Transactional
    public BidPostResponse createBid(BidPostRequest request, String username) {
        Bid bid;
        Item itemRef = itemRepository.getReferenceById(request.itemId());
        User user = userRepository.getReferenceById(username);

        ItemStatus itemStatus = itemStatusService.getItemStatus(request.itemId());

        // big amount must be higher than starting price and bid time must be lower than
        // endtime and bid amount must be higher than current balance
        if (request.bidAmount() < itemStatus.getStartingPrice()
                || Instant.now().toEpochMilli() > itemStatus.getEndTime() || request.bidAmount() > user.getBalance()) {
            throw new BaseException(
                    "Failed to bid");
        }

        // if bid exist then get bid from DB and then edit bid and save it again to db
        if (bidRepository.existsByUserAndItem(user, itemRef)) {
            bid = bidRepository.findByUserAndItem(user, itemRef)
                    .orElseThrow(() -> new BaseException("No user or item"));
            bid.setBidAmount(request.bidAmount());
            bidRepository.save(bid);
        } else { // Else make new bid
            bid = new Bid(itemRef, user, request.bidAmount());
            bidRepository.save(bid);
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
