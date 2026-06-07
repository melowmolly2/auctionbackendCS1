package com.auction.bids;

import com.auction.common.BaseException;
import com.auction.common.BaseObjectResponse;
import com.auction.items.Item;
import com.auction.users.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that manages business logic related to bids and auto-bids.
 */
@Service
public class BidService {

  private final BidRepository bidRepository;
  private final AutoBidRepository autoBidRepository;

  public BidService(BidRepository bidRepository, AutoBidRepository autoBidRepository) {
    this.bidRepository = bidRepository;
    this.autoBidRepository = autoBidRepository;
  }

  /**
   * Retrieves a paginated list of all bids for a specific item, sorted by bid amount in ascending order.
   */
  @Transactional(readOnly = true)
  public BaseObjectResponse<Page<Bid>> getBidsOnItem(Long itemId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("bidAmount"));
    Page<Bid> items = bidRepository.findItemBidHistory(pageable, itemId);
    return new BaseObjectResponse<Page<Bid>>(true, "Succesfully get all bids", items);
  }

  /**
   * Checks if a user has already placed a bid on a specific item.
   */
  @Transactional(readOnly = true)
  public boolean existUserAndItem(User user, Item item) {
    return bidRepository.existsByUserAndItem(user, item);
  }

  /**
   * Saves a new bid to the database.
   */
  @Transactional
  public Bid saveBid(Bid bid) {
    bid = bidRepository.save(bid);
    return bid;
  }

  /**
   * Retrieves the details of a user's bid on a specific item. Throws an exception if not found.
   */
  @Transactional
  public Bid getBidByUserAndItem(User user, Item item) {
    Bid bid =
        bidRepository
            .findByUserAndItem(user, item)
            .orElseThrow(() -> new BaseException("Unable to find user or item"));
    return bid;
  }

  /**
   * Retrieves a complete list of all bids placed by a user.
   */
  @Transactional(readOnly = true)
  public Page<Bid> getAllUserBid(User userRef, Pageable pageable) {
    Page<Bid> bids = bidRepository.findAllByUser(userRef, pageable);
    return bids;
  }

  /**
   * Retrieves a list of auctions that the user has currently won.
   *
   * @param username The username of the user
   * @return A list of winning bids
   */
  @Transactional
  public List<Bid> getUserWins(String username) {
    List<Bid> bids = bidRepository.getWinsByUser(username, Instant.now().toEpochMilli());
    return bids;
  }

  /**
   * Searches for the auto-bid configuration of a specific item.
   */
  @Transactional(readOnly = true)
  public Optional<AutoBid> getAutoBidByItemId(Long itemId) {
    return autoBidRepository.findByItemId(itemId);
  }

  /**
   * Creates or updates an auto-bid configuration.
   */
  @Transactional
  public void saveAutoBid(AutoBid autoBid) {
    autoBidRepository.save(autoBid);
  }

  /**
   * Deletes an auto-bid configuration.
   */
  @Transactional
  public void deleteAutoBid(AutoBid autoBid) {
    autoBidRepository.delete(autoBid);
  }
}
