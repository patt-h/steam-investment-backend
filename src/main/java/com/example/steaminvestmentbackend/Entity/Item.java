package com.example.steaminvestmentbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "market_hash_name")
    private String marketHashName;

    @Column(name = "price")
    private Float price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "item_id")
    private Long itemId;

}
