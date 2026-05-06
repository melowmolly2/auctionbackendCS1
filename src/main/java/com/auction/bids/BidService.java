package com.auction.bids;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.bids.exceptions.BuyItNowException;
import com.auction.common.BaseObjectResponse;
import com.auction.common.BaseResponse;
import com.auction.items.ItemRepository;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.User;
import com.auction.users.UserRepository;

@Service
public class BidService {
    // Don't inject repository, inject service instead. Refactor tomorrow.

    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ItemStatusService itemStatusService;

    public BidService(BidRepository bidRepository, UserRepository userRepository,
            ItemStatusService itemStatusService) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.itemStatusService = itemStatusService;
    }

    @Transactional
    public BaseResponse buyItemNow(Long itemId, String username) {
        ItemStatus itemStatus = itemStatusService.getItemStatus(itemId);
        User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));
        // Buy now if balance >= buyitnow, buyitnow > currentprice, buyitnow != 0
        // (Seller does not set a buy now price)
        if (user.getBalance() >= itemStatus.getBuyItNowPrice()
                && itemStatus.getBuyItNowPrice() > itemStatus.getCurrentPrice() && itemStatus.getBuyItNowPrice() != 0) {
            itemStatus.setHighestBidUser(username);
            itemStatus.setCurrentPrice(itemStatus.getBuyItNowPrice());
            itemStatus.setEndTime(Instant.now().toEpochMilli());
            itemStatusService.saveStatus(itemStatus);
            user.setBalance(user.getBalance() - itemStatus.getBuyItNowPrice());
            userRepository.save(user);
        } else {

            throw new BuyItNowException("You don't have enough money in your balance to buy the item");
        }
        return new BaseResponse(true, "Successfully bought item");
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<Page<Bid>> getBidsOnItem(Long itemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bidAmount"));
        Page<Bid> items = bidRepository.findItemBidHistory(pageable, itemId);
        return new BaseObjectResponse<Page<Bid>>(true, "Succesfully get all bids", items);
    }
}
