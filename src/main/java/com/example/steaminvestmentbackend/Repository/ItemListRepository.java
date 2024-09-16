package com.example.steaminvestmentbackend.Repository;

import com.example.steaminvestmentbackend.Entity.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemListRepository extends JpaRepository<ItemList, Long> {

    ItemList findByMarketHashName(String marketHashName);

    List<ItemList> findByMarketHashNameContainingIgnoreCase(String prefix);

}
