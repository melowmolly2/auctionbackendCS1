package com.auction.bids.bidsdetails;

import org.springframework.stereotype.Service;

import com.auction.bids.bidsdetails.dto.BidPostRequest;
import com.auction.bids.bidsdetails.dto.BidPostResponse;
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
        Bid bid;
        if (bidRepository.existsByUserAndItem(userRepository.getReferenceById(request.username()),
                itemRepository.getReferenceById(request.itemId()))) {
            bid = bidRepository.findByUserAndItem(userRepository.getReferenceById(request.username()),
                    itemRepository.getReferenceById(request.itemId()));
            bid.setBidAmount(request.bidAmount());
            bidRepository.save(bid);
        } else {
            bid = new Bid(itemRepository.getReferenceById(request.itemId()),
                    userRepository.getReferenceById(request.username()), request.bidAmount());
            bidRepository.save(bid);
        }

        return new BidPostResponse(true, "Successfully created bid for an item", bid);
    }
}
