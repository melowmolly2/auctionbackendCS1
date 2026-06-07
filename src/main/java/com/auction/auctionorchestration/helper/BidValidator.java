package com.auction.auctionorchestration.helper;

import com.auction.common.BaseException;
import com.auction.items.Item;
import com.auction.items.ItemService;
import com.auction.itemstatus.ItemStatus;
import com.auction.itemstatus.ItemStatusService;
import com.auction.users.User;
import com.auction.users.UserService;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Lớp trợ giúp để xác thực các lượt đặt giá.
 */
@Component
public class BidValidator {

  private final ItemStatusService itemStatusService;
  private final ItemService itemService;
  private final UserService userService;

  public BidValidator(
      ItemStatusService itemStatusService, ItemService itemService, UserService userService) {
    this.itemStatusService = itemStatusService;
    this.itemService = itemService;
    this.userService = userService;
  }

  /**
   * Xác thực một lượt đặt giá.
   * @param item Sản phẩm
   * @param user Người dùng
   * @param itemStatus Trạng thái sản phẩm
   * @param value Giá trị đặt
   */
  public void validate(Item item, User user, ItemStatus itemStatus, Double value) {
    if (item.getUser().getUsername().equals(user.getUsername())) {
      throw new BaseException("You can't place bid on your own item");
    }
    if (itemStatus.getStartingPrice() > value) {
      throw new BaseException("Your bid must be higher than the starting price");
    }
    validateAuctionActive(item.getItemId());
    validateSufficientFunds(user, value);
    validateAboveMinimum(itemStatus, value);
  }

  /**
   * Xác thực người dùng có đủ tiền hay không.
   * @param user Người dùng
   * @param value Giá trị
   */
  public void validateSufficientFunds(User user, Double value) {
    if (user.getBalance() < value) {
      throw new BaseException("You don't have enough money");
    }
  }

  /**
   * Xác thực phiên đấu giá có đang hoạt động hay không.
   * @param itemId ID sản phẩm
   */
  public void validateAuctionActive(Long itemId) {
    if (auctionEndedOrNot(itemId)) {
      throw new BaseException("Auction has already ended");
    }
  }

  /**
   * Xác thực giá trị đặt có cao hơn giá tối thiểu hay không.
   * @param itemStatus Trạng thái sản phẩm
   * @param value Giá trị
   */
  public void validateAboveMinimum(ItemStatus itemStatus, Double value) {
    if (itemStatus.getCurrentPrice() + itemStatus.getBidIncrement() > value) {
      throw new BaseException("Your bid must be higher than the current highest");
    }
  }

  /**
   * Kiểm tra phiên đấu giá đã kết thúc hay chưa.
   * @param itemId ID sản phẩm
   * @return true nếu đã kết thúc, ngược lại là false
   */
  public boolean auctionEndedOrNot(Long itemId) {
    ItemStatus itemStatus = itemStatusService.getItemStatus(itemId);

    if (itemStatus.getItemStatus().equals("ENDED")
        || itemStatus.getItemStatus().equals("CANCELED")) {
      return true;
    }
    return itemStatus.getEndTime() < Instant.now().toEpochMilli();
  }
}
