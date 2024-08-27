package com.example.steaminvestmentbackend.Repository;

import com.example.steaminvestmentbackend.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserId(Long userId);

}
