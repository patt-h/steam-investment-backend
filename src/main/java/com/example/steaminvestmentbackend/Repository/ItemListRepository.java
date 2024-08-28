package com.example.steaminvestmentbackend.Repository;

import com.example.steaminvestmentbackend.Entity.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemListRepository extends JpaRepository<ItemList, Long> {

    ItemList findByMarketHashName(String marketHashName);

}
