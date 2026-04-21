package com.auction.items;

import com.auction.items.dto.PublishItemRequest;
import com.auction.items.dto.PublishItemResponse;
import com.auction.items.exceptions.ItemException;
import com.auction.users.User;
import com.auction.users.UserRepository;

public class ItemService {
   private final ItemRepository itemRepository;
   private final UserRepository userRepository;
   public PublishItemResponse publishItem(PublishItemRequest request) {
    User user = userRepository.findByUsername(request.sellerUsername()).orElseThrow(() -> new ItemException(false, "There is no seller with that username."));
    Item item = itemRepository.save(new Item(user, request.title()));
    return new PublishItemResponse(item); 
} 
}
