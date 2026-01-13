package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 车间控制器
 *
 * @author maziy
 * @date 2025/12/20
 */
@RestController("AdminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags="店铺相关接口")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 设置状态
     *
     * @param status 状态
     * @return {@link Result }<{@link String }>
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result<String> setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态为：{}",status == 1 ?"营业中":"打烊中");
        shopService.setStatus(status);
        return Result.success();
    }

    /**
     * 获取状态
     *
     * @return {@link Result }<{@link Integer }>
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status = shopService.getStatus();
        log.info("查询店铺营业状态为：{}",status ==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
