package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> listCart();

    void deleteFromCart(ShoppingCartDTO cartDTO);

    void clearCart();

    Integer getTotalCount();

    void updateQuantity(ShoppingCartDTO cartDTO, Integer quantity);
}
