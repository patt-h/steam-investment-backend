package com.example.steaminvestmentbackend.Repository;

import com.example.steaminvestmentbackend.Entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {

    List<ItemHistory> findByItemId(Long itemId);

}
