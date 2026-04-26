package com.auction.items;

import com.auction.common.BaseResponse;
import com.auction.items.dto.BaseItemResponse;
import com.auction.items.dto.GetItemsResponse;
import com.auction.items.dto.PublishItemRequest;
import com.auction.items.exceptions.ItemException;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.User;
import com.auction.users.UserService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemStatusService itemStatusService;

    public ItemService(ItemRepository itemRepository, UserService userService,
            ItemStatusService itemStatusService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemStatusService = itemStatusService;

    }

    @Transactional
    public BaseItemResponse publishItem(PublishItemRequest request) {
        User user = userService.getUserReferenceByUsername(request.sellerUsername());
        Item item = itemRepository.save(new Item(user, request.title()));

        // Create Item Status along with the item
        itemStatusService.saveStatus(
                new ItemStatus(item, 0.0, request.sellerUsername(), request.endTime(), request.startingPrice(),
                        request.buyItNowPrice(), request.bitIncrement()));

        return new BaseItemResponse(true, "Created new item.", item);
    }

    // Consider if you want to do a Cascading Deletion here
    @Transactional
    public BaseResponse deleteItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(false, "There is no Item with that ID"));
        itemRepository.deleteById(itemId);
        return new BaseResponse(true, "Item " + itemId + " was deleted");
    }

    @Transactional
    public BaseItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(false, "This Item Id does not exist"));
        return new BaseItemResponse(true, "Successfully get Item", item);
    }

    @Transactional
    public GetItemsResponse getItems() {
        List<Item> items = itemRepository.findAll();
        return new GetItemsResponse(true, "Successfully get all items", items);
    }

    @Transactional
    public Item getItemReferenceByItemId(Long itemId) {
        Item itemRef = itemRepository.getReferenceById(itemId);
        return itemRef;
    }
}
