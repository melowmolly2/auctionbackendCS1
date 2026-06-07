package com.auction.bids;

import com.auction.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * The AutoBid entity represents a user's automatic bidding configuration for an auction item.
 * The system will automatically increase the bid when someone else bids higher, up to the maxBidLimit.
 */
@Entity
@Table(name = "autobids")
public class AutoBid {

  // Auction item ID (Primary Key), each item can have at most one active AutoBid configuration at a time
  @Id
  @JsonIgnore
  @Column(name = "item_id")
  private Long itemId;

  // The maximum bid limit the user is willing to pay for this item
  @Column(name = "max_bid_limit")
  private Double maxBidLimit;

  // The current bid value that the system has automatically placed on behalf of the user
  @Column(name = "current_bid_value")
  private Double currentBidValue;

  // The user who set up this automatic bidding configuration
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "bidder_username")
  private User bidder;

  // The time the automatic bidding was configured or updated
  @Column(name = "bid_time")
  private Long time;

  /**
   * JPA Lifecycle Callback: Automatically records the creation time of the AutoBid configuration.
   */
  @PrePersist
  void addTime() {
    time = Instant.now().toEpochMilli();
  }

  public AutoBid() {}
  ;

  public AutoBid(Long itemId, User bidder, Double maxBidLimit, Double currentBidValue) {
    this.itemId = itemId;
    this.maxBidLimit = maxBidLimit;
    this.currentBidValue = currentBidValue;
    this.bidder = bidder;
  }

  public Double getCurrentBidValue() {
    return currentBidValue;
  }

  public void setCurrentBidValue(Double currentBidValue) {
    this.currentBidValue = currentBidValue;
  }

  public Long getItemId() {
    return itemId;
  }

  public Double getMaxBidLimit() {
    return maxBidLimit;
  }

  public User getBidder() {
    return bidder;
  }

  public Long getTime() {
    return time;
  }

  public void setMaxBidLimit(Double maxBidLimit) {
    this.maxBidLimit = maxBidLimit;
  }
}
