package com.auction.bids.bidsdetails;

import java.time.OffsetDateTime;

import com.auction.items.Item;
import com.auction.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "bids")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long bidId;

    @ManyToOne // Many bids, Item
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne // One Bidder one Bid
    @JoinColumn(name = "bidder_username")
    private User user;

    @Column(name = "bid_amount")
    private Long bidAmount;

    @Column(name = "bid_time")
    private OffsetDateTime time;

    @PrePersist
    protected void addTime() {
        time = OffsetDateTime.now();
    }

    public Bid(Item item, User bidder_username, Long bidAmount) {
        this.item = item;
        this.user = bidder_username;
        this.bidAmount = bidAmount;
    }

    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Long bidAmount) {
        this.bidAmount = bidAmount;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }

}
