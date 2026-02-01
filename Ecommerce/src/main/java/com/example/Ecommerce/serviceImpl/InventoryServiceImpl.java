package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Inventory;
import com.example.Ecommerce.Repository.InventoryRepository;
import com.example.Ecommerce.service.InventoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public boolean checkAvailability(Long productId, Integer quantity) {
        //TODO -> CHANGE by id to byproductid everywhere
        return inventoryRepository.findById(productId)
                .map(inv -> inv.getQuantity() >= quantity)
                .orElse(false);
    }

    //used transactional for atomicity and added versioning to add a optimistic lock
    @Transactional
    @Override
    public void reduceStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (inventory.getQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
        }else{
            throw new IllegalArgumentException("Product not found");
        }

    }

    @Override
    public void restockProduct(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

    }

    @Override
    public Inventory addNewProduct(Long productId, Integer quantity) {
        return null;
    }
}
