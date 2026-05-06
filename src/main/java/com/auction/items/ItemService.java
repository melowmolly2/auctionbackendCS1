package com.auction.items;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.bids.Bid;
import com.auction.bids.BidRepository;
import com.auction.common.BaseException;
import com.auction.common.BaseObjectResponse;
import com.auction.common.BaseResponse;
import com.auction.items.dto.BaseItemResponse;
import com.auction.items.dto.GetItemPagesResponse;
import com.auction.items.dto.GetItemsResponse;
import com.auction.items.dto.PublishItemRequest;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.User;
import com.auction.users.UserService;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemStatusService itemStatusService;
    private final BidRepository bidRepository;

    public ItemService(ItemRepository itemRepository, UserService userService,
            ItemStatusService itemStatusService, BidRepository bidRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemStatusService = itemStatusService;
        this.bidRepository = bidRepository;

    }

    @Transactional
    public BaseItemResponse publishItem(PublishItemRequest request, String username) {
        User user = userService.getUserReferenceByUsername(username);
        Item item = itemRepository.save(new Item(user, request.title(), request.description()));

        // Create Item Status along with the item
        itemStatusService.saveStatus(
                new ItemStatus(item, 0.0, username, request.endTime(), request.startingPrice(),
                        request.buyItNowPrice(), request.bidIncrement()));

        return new BaseItemResponse(true, "Created new item.", item);
    }

    @Transactional
    public boolean existByItemId(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    // Consider if you want to do a Cascading Deletion here
    @Transactional
    public BaseResponse deleteItem(Long itemId, String username) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseException("There is no Item with that ID"));
        itemRepository.deleteById(itemId);
        if (item.getUser().getUsername().equals(username)) {
            return new BaseResponse(true, "Item " + itemId + " was deleted");
        } else {
            throw new BaseException("You are not the owner of this item");
        }

    }

    @Transactional(readOnly = true)
    public BaseItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseException("This Item Id does not exist"));
        return new BaseItemResponse(true, "Successfully get Item", item);
    }

    @Transactional(readOnly = true)
    public GetItemsResponse getItems() {
        List<Item> items = itemRepository.findAll();
        return new GetItemsResponse(true, "Successfully get all items", items);
    }

    @Transactional(readOnly = true)
    public Item getItemReferenceByItemId(Long itemId) {
        Item itemRef = itemRepository.getReferenceById(itemId);
        return itemRef;
    }

    @Transactional(readOnly = true)
    public GetItemPagesResponse getActiveItemsByPageTitle(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("item.title"));
        Page<Item> pages = itemRepository.findActiveItemPage(pageable, Instant.now().toEpochMilli());
        return new GetItemPagesResponse(true, "successfully got pages", pages);
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<Page<Bid>> getBidsOnItem(Long itemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bidAmount"));
        Page<Bid> items = bidRepository.findItemBidHistory(pageable, itemId);
        return new BaseObjectResponse<Page<Bid>>(true, "Succesfully get all bids", items);
    }

    @Transactional(readOnly = true)
    public BaseObjectResponse<Page<Item>> getListingByUser(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items = itemRepository.findItemListing(pageable, username);
        return new BaseObjectResponse<Page<Item>>(true, "succesfully got listing", items);
    }

}
