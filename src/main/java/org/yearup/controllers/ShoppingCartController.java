package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()") // Tüm metodlara login zorunluluğu
@CrossOrigin
public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // Kullanıcının sepetini getir
    @GetMapping("")
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            User user = getAuthenticatedUser(principal);
            return shoppingCartDao.getByUserId(user.getId());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get cart", e);
        }
    }

    // Sepete ürün ekle
    @PostMapping("/products/{productId}")
    public void addProductToCart(@PathVariable int productId, Principal principal)
    {
        try
        {
            User user = getAuthenticatedUser(principal);
            shoppingCartDao.addProduct(user.getId(), productId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product", e);
        }
    }

    // Sepetteki ürünün miktarını güncelle
    @PutMapping("/products/{productId}")
    public void updateCartItem(@PathVariable int productId,
                               @RequestBody ShoppingCartItem item,
                               Principal principal)
    {
        try
        {
            User user = getAuthenticatedUser(principal);
            shoppingCartDao.updateQuantity(user.getId(), productId, item.getQuantity());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update cart item", e);
        }
    }


    // Sepeti tamamen boşalt
    @DeleteMapping("")
    public void clearCart(Principal principal)
    {
        try
        {
            User user = getAuthenticatedUser(principal);
            shoppingCartDao.clearCart(user.getId());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to clear cart", e);
        }
    }

    // Yardımcı: Kullanıcıyı username'den bul
    private User getAuthenticatedUser(Principal principal)
    {
        String userName = principal.getName();
        return userDao.getByUserName(userName);
    }
}
