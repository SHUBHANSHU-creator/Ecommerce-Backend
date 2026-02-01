package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Inventory;
import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.Repository.InventoryRepository;
import com.example.Ecommerce.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public boolean checkAvailability(Long productId, Integer quantity) {
        Inventory inv =  inventoryRepository.findByProductId(productId);
        log.info("Check availability of inventory {}",inv);
        return inv!=null && inv.getQuantity() >= quantity;
    }

    //used transactional for atomicity and added versioning to add a optimistic lock
    @Transactional
    @Override
    public void reduceStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory == null){
            throw new IllegalArgumentException("Product not found");
        }
        if (inventory.getQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
        }else{
            throw new IllegalArgumentException("Insufficient stock");
        }

    }

    @Override
    public void restockProduct(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory == null){
            throw new IllegalArgumentException("Product not found");
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

    }

    @Override
    public Inventory addNewProduct(Product product, Integer quantity) {
        Inventory inv = new Inventory(product, quantity);
        return inventoryRepository.save(inv);
    }
}
