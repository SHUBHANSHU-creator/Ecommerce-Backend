package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Inventory;
import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.Repository.InventoryRepository;
import com.example.Ecommerce.exception.InsufficientStockException;
import com.example.Ecommerce.exception.InventoryNotFoundException;
import com.example.Ecommerce.exception.InvalidInventoryOperationException;
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
        if (productId == null || quantity == null || quantity <= 0) {
            throw new InvalidInventoryOperationException("Product id and quantity must be provided for availability checks");
        }
        Inventory inv =  inventoryRepository.findByProductId(productId);
        log.info("Check availability of inventory {}",inv);
        return inv!=null && inv.getQuantity() >= quantity;
    }

    //used transactional for atomicity and added versioning to add a optimistic lock
    @Transactional
    @Override
    public void reduceStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new InvalidInventoryOperationException("Product id and quantity must be provided to reduce stock");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory == null){
            throw new InventoryNotFoundException(productId);
        }
        if (inventory.getQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
        }else{
            throw new InsufficientStockException(productId, quantity, inventory.getQuantity());
        }

    }

    @Override
    public void restockProduct(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new InvalidInventoryOperationException("Product id and quantity must be provided to restock");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory == null){
            throw new InventoryNotFoundException(productId);
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

    }

    @Override
    public Inventory addNewProduct(Product product, Integer quantity) {
        if (product == null || quantity == null || quantity < 0) {
            throw new InvalidInventoryOperationException("Product and non-negative quantity must be provided");
        }
        Inventory inv = new Inventory(product, quantity);
        return inventoryRepository.save(inv);
    }
}
