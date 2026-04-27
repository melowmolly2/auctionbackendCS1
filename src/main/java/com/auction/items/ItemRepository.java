package com.auction.items;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items INNER JOIN item_statuses ON items.item_id = item_statuses.item_id WHERE :now < item_statuses.end_time")
    public Page<Item> findActiveItemPages(@Param("now") long now, Pageable pageable);
}
