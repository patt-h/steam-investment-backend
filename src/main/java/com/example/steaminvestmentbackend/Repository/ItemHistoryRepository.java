package com.example.steaminvestmentbackend.Repository;

import com.example.steaminvestmentbackend.Entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {

    List<ItemHistory> findByItemId(Long itemId);

    @Query("SELECT i FROM ItemHistory i WHERE i.itemId = :itemId AND i.date = :date")
    ItemHistory findByItemIdAndDate(@Param("itemId") Long itemId, @Param("date") LocalDate date);

}
