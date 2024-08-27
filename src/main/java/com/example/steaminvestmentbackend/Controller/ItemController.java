package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.Item;
import com.example.steaminvestmentbackend.Service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/all")
    public List<Item> getAll(@AuthenticationPrincipal UserDTO userDTO) {
        return itemService.getAll(userDTO);
    }

    @PostMapping("/add")
    public List<Item> addItem(@RequestBody List<Item> items, @AuthenticationPrincipal UserDTO userDTO) {
        return itemService.addNew(items, userDTO);
    }

    @PatchMapping("/update")
    public Item updateItem(@RequestBody Item item, @AuthenticationPrincipal UserDTO userDTO) {
        return itemService.update(item, userDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteItem(@PathVariable List<Long> id, @AuthenticationPrincipal UserDTO userDTO) {
        itemService.delete(id, userDTO);
    }

}
