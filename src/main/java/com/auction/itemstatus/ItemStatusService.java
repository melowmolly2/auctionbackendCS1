package com.auction.itemstatus;

import com.auction.items.ItemRepository;
import com.auction.itemstatus.dto.ItemStatusGetResponse;

import jakarta.transaction.Transactional;

public class ItemStatusService {
    public final ItemStatusRepository itemStatusRepository;
    public final ItemRepository itemRepository;

    public ItemStatusService(ItemStatusRepository itemStatusRepository, ItemRepository itemRepository) {
        this.itemStatusRepository = itemStatusRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void saveStatus(ItemStatus itemStatus) {
        itemStatusRepository.save(itemStatus);
    }

    @Transactional
    public ItemStatusGetResponse getStatus(Long itemId) {
        ItemStatus itemStatus = itemStatusRepository.findByItem(itemRepository.getReferenceById(itemId));
        return new ItemStatusGetResponse(true, "Succesfully get item status", itemStatus);
    }
}
