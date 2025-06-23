package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController
{
    private final ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Product> search(@RequestParam(name = "cat", required = false) Integer categoryId,
                                @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                                @RequestParam(name = "color", required = false) String color)
    {
        try
        {
            return productDao.search(categoryId, minPrice, maxPrice, color);
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Product getById(@PathVariable int id)
    {
        try
        {
            var product = productDao.getById(id);
            if (product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return product;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Product addProduct(@RequestBody Product product)
    {
        try
        {
            return productDao.create(product);
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Product updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        try
        {
            productDao.update(id, product);
            return productDao.getById(id); // Güncellenmiş ürünü döndür
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable int id)
    {
        try
        {
            var product = productDao.getById(id);
            if (product == null)
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);

            productDao.delete(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new ResponseEntity<>("Error deleting product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
