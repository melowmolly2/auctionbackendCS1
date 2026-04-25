package com.auction.itemstatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auction.itemstatus.dto.ItemStatusGetResponse;

@Controller
@RequestMapping("/item")
public class ItemStatusController {
    public final ItemStatusService itemStatusService;

    public ItemStatusController(ItemStatusService itemStatusService) {
        this.itemStatusService = itemStatusService;
    }

    @GetMapping("/status/{itemId}")
    public ResponseEntity<ItemStatusGetResponse> getItemStatus(@PathVariable Long itemId) {
        ItemStatusGetResponse repsonse = itemStatusService.getStatus(itemId);
        return ResponseEntity.ok().body(repsonse);
    }
}
