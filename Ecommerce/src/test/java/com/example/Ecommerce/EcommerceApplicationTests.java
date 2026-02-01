package com.example.Ecommerce;

import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcommerceApplicationTests {
    @Autowired
    InventoryService inventoryService;

	@Test
	void contextLoads() {
	}

    @Test
    void checkAvailability() {
        Long ProductId = new Long(1);
        Integer Quantity = new Integer(1);
        inventoryService.checkAvailability(ProductId, Quantity);

    }

    @Test
    void addNewProduct() {
        Product product = new Product();
        product.setProductName("Iphone");
        product.setProductDescription("Iphone");
        Integer Quantity = new Integer(1);
        inventoryService.addNewProduct(product, Quantity);
    }

}
