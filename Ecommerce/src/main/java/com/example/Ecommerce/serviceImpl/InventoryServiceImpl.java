package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Inventory;
import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.Repository.InventoryRepository;
import com.example.Ecommerce.exception.InsufficientStockException;
import com.example.Ecommerce.exception.InventoryNotFoundException;
import com.example.Ecommerce.exception.InvalidInventoryOperationException;
import com.example.Ecommerce.response.ApiResponse;
import com.example.Ecommerce.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public ApiResponse<Boolean> checkAvailability(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new InvalidInventoryOperationException("Product id and quantity must be provided for availability checks");
        }
        Inventory inv =  inventoryRepository.findByProductId(productId);
        log.info("Check availability of inventory {}",inv);
        boolean available = inv != null && inv.getQuantity() >= quantity;
        return new ApiResponse<>(HttpStatus.OK.value(), "Inventory availability checked", available);
    }

    //used transactional for atomicity and added versioning to add a optimistic lock
    @Transactional
    @Override
    public ApiResponse<Inventory> reduceStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new InvalidInventoryOperationException("Product id and quantity must be provided to reduce stock");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory == null){
            throw new InventoryNotFoundException(productId);
        }
        if (inventory.getQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            Inventory savedInventory = inventoryRepository.save(inventory);
            return new ApiResponse<>(HttpStatus.OK.value(), "Stock reduced successfully", savedInventory);
        }else{
            throw new InsufficientStockException(productId, quantity, inventory.getQuantity());
        }

    }

    @Override
    public ApiResponse<Inventory> restockProduct(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new InvalidInventoryOperationException("Product id and quantity must be provided to restock");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory == null){
            throw new InventoryNotFoundException(productId);
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return new ApiResponse<>(HttpStatus.OK.value(), "Stock restocked successfully", savedInventory);

    }

    @Override
    public ApiResponse<Inventory> addNewProduct(Product product, Integer quantity) {
        if (product == null || quantity == null || quantity < 0) {
            throw new InvalidInventoryOperationException("Product and non-negative quantity must be provided");
        }
        Inventory inv = new Inventory(product, quantity);
        Inventory savedInventory = inventoryRepository.save(inv);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "Product added to inventory", savedInventory);
    }
}
