package com.auction.itemstatus;

import java.time.OffsetDateTime;

import com.auction.items.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class ItemStatus {
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "username")
    private String higherBidUser;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "item_status")
    private boolean itemStatus;

    @Column(name = "starting_price")
    private Long startingPrice;

    @Column(name = "buy_it_now_price")
    private Long buyItNowPrice;

    @Column(name = "bid_increment")
    private Long bidIncrement;

    public ItemStatus() {
    };

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getHigherBidUser() {
        return higherBidUser;
    }

    public void setHigherBidUser(String higherBidUser) {
        this.higherBidUser = higherBidUser;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(boolean itemStatus) {
        this.itemStatus = itemStatus;
    }

    public Long getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Long startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Long getBuyItNowPrice() {
        return buyItNowPrice;
    }

    public void setBuyItNowPrice(Long buyItNowPrice) {
        this.buyItNowPrice = buyItNowPrice;
    }

    public Long getBidIncrement() {
        return bidIncrement;
    }

    public void setBidIncrement(Long bidIncrement) {
        this.bidIncrement = bidIncrement;
    }

}
