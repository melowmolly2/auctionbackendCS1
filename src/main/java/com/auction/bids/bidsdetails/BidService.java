package com.auction.bids.bidsdetails;

import org.springframework.stereotype.Service;

import com.auction.bids.bidsdetails.dto.*;
import com.auction.items.ItemRepository;
import com.auction.users.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BidService(BidRepository bidRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public BidPostResponse createBid(BidPostRequest request) {
        Bid bid = new Bid(itemRepository.getReferenceById(request.getItemId()),
                userRepository.getReferenceById(request.getUsername()), request.getBidAmount());
        bid = bidRepository.save(bid);
        return new BidPostResponse(true, "Successfully created bid for an item", bid);
    }
}
