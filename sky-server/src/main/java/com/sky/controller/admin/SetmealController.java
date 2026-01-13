package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐");
        setmealService.save(setmealDTO);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询");
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除")
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("批量删除");
        setmealService.deleteBatch(ids);
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("查询")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("查询");
        SetmealVO setmealVO = setmealService.getByIdWithDishes(id);
        return Result.success(setmealVO);
    }
    @PutMapping
    @ApiOperation("编辑")
    public Result<String> update(@RequestBody SetmealDTO setmealDTO){
        log.info("编辑");
        setmealService.update(setmealDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售、停售")
    public Result<String> startOrStop(@PathVariable Integer status,Long id){
        log.info("起售、停售");
        setmealService.startOrStop(status,id);
        return Result.success();
    }
}
