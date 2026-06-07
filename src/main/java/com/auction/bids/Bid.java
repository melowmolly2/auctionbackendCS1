package com.auction.bids;

import com.auction.items.Item;
import com.auction.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * The Bid entity represents a manual bid placed by a user on an auction item.
 */
@Entity
@Table(name = "bids")
public class Bid {

  // Bid ID (Primary Key), auto-incremented
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bid_id")
  private Long bidId;

  // The associated auction item. An item can have multiple bids.
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  // The user who placed the bid. A user can place multiple bids.
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "bidder_username")
  private User user;

  // The amount of the bid for this auction
  @Column(name = "bid_amount")
  private Double bidAmount;

  // The time the bid was placed (Epoch Milliseconds)
  @Column(name = "bid_time")
  private Long time;

  /**
   * JPA Lifecycle Callback: Automatically records the time the bid was placed before saving to the database.
   */
  @PrePersist
  void addTime() {
    time = Instant.now().toEpochMilli();
  }

  public Bid() {}
  ;

  public Bid(Item item, User bidder_username, Double bidAmount) {
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

  public Double getBidAmount() {
    return bidAmount;
  }

  public void setBidAmount(Double bidAmount) {
    this.bidAmount = bidAmount;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }
}
