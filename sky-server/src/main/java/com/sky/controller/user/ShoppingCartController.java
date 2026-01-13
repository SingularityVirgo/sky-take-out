package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 1. 添加
     *
     * @param shoppingCartDTO 购物车dto
     * @return {@link Result }<{@link String }>
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }
    /**
     * 2. 查看购物车列表
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> cartList = shoppingCartService.listCart();
        return Result.success(cartList);
    }

    /**
     * 3. 修改商品数量
     */
    @PutMapping("/update")
    @ApiOperation("修改数量")
    public Result<String> update(@RequestBody ShoppingCartDTO cartDTO, @RequestParam Integer quantity) {
        shoppingCartService.updateQuantity(cartDTO, quantity);
        return Result.success("修改成功");
    }

    /**
     * 4. 删除商品
     */
    @PostMapping("/sub")
    @ApiOperation("删除商品")
    public Result<String> delete(@RequestBody ShoppingCartDTO cartDTO) {
        shoppingCartService.deleteFromCart(cartDTO);
        return Result.success("删除成功");
    }

    /**
     * 5. 清空购物车
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> clear() {
        shoppingCartService.clearCart();
        return Result.success("清空成功");
    }

    /**
     * 6. 获取商品总数（用于前端角标显示）
     */
    @GetMapping("/count")
    @ApiOperation("购物车商品总数")
    public Result<Integer> count() {
        Integer count = shoppingCartService.getTotalCount();
        return Result.success(count);
    }

}
