package com.auction.bids;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.items.Item;
import com.auction.users.User;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    // JPA Delivery Query Method black magic. Syntax: [Prefix] + [Subject] + [By] +
    // [Property Expression] + [Keyword] + [Logical Operator]
    // Prefix: find, read, get, query, search, stream, exist
    // Subject: entity's name or "Distinct"
    // By: query conditions begin here
    // Property Expression: Must be a variable name in your class (the class here is
    // Bid)
    Bid findByUserAndItem(User user, Item item);

    Page<Bid> findAllByUser(User user);

    boolean existsByUserAndItem(User user, Item item);
}
