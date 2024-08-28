package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.Item;
import com.example.steaminvestmentbackend.Exceptions.AppException;
import com.example.steaminvestmentbackend.Repository.ItemListRepository;
import com.example.steaminvestmentbackend.Repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemListRepository itemListRepository;

    public List<Item> getAll(@AuthenticationPrincipal UserDTO userDTO) {
        return itemRepository.findByUserId(userDTO.getId());
    }

    public List<Item> addNew(List<Item> items, @AuthenticationPrincipal UserDTO userDTO) {
        for (Item i : items) {
            i.setItemId(itemListRepository.findByMarketHashName(i.getMarketHashName()).getId());
            i.setUserId(userDTO.getId());
        }

        itemRepository.saveAll(items);

        return items;
    }

    public Item update(Item item, @AuthenticationPrincipal UserDTO userDTO) {
        if (item.getId() == null) {
            throw new AppException("Item can't be null", HttpStatus.BAD_REQUEST);
        } else {
            Optional<Item> itemToUpdate = itemRepository.findById(item.getId());
            if (itemToUpdate.isEmpty()) {
                throw new AppException("Couldn't find item with provided id", HttpStatus.NOT_FOUND);
            } else if (!itemToUpdate.get().getUserId().equals(userDTO.getId())) {
                throw new AppException("Access denied, user id doesn't match item user id", HttpStatus.FORBIDDEN);
            } else {
                item.setUserId(userDTO.getId());
                if (item.getMarketHashName() == null) {
                    item.setMarketHashName(itemToUpdate.get().getMarketHashName());
                    item.setItemId(itemToUpdate.get().getItemId());
                } else {
                    item.setItemId(itemListRepository.findByMarketHashName(item.getMarketHashName()).getId());
                }
                if (item.getPrice() == null) {
                    item.setPrice(itemToUpdate.get().getPrice());
                }
                if (item.getQuantity() == null) {
                    item.setQuantity(itemToUpdate.get().getQuantity());
                }

                return itemRepository.save(item);
            }
        }
    }

    public void delete(List<Long> id, @AuthenticationPrincipal UserDTO userDTO) {
        for (Long idToDelete : id) {
            Optional<Item> itemToDelete = itemRepository.findById(idToDelete);

            if (itemToDelete.isPresent() && itemToDelete.get().getUserId().equals(userDTO.getId())) {
                itemRepository.deleteById(idToDelete);
            } else if (itemToDelete.isPresent() && !itemToDelete.get().getUserId().equals(userDTO.getId())) {
                throw new AppException("Access denied, user id doesn't match expense user id", HttpStatus.FORBIDDEN);
            } else {
                throw new AppException("Couldn't find expense with provided id", HttpStatus.NOT_FOUND);
            }
        }
    }

}
