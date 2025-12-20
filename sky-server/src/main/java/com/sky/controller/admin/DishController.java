package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.EmployeeService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 碟形控制器
 *
 * @author maziy
 * @date 2025/12/18
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags="菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 保存
     *
     * @param dishDTO 碟形dto
     * @return {@link Result }<{@link String }>
     */
    @PostMapping
    @ApiOperation("保存菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO){
        log.info("保存菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 第页
     *
     * @param dishPageQueryDTO 菜品页面查询dto
     * @return {@link Result }<{@link PageResult }>
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除
     *
     * @param ids 身份证
     * @return {@link Result }
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 按id获取
     *
     * @param id id
     * @return {@link Result }<{@link DishVO }>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 更新
     *
     * @param dishDTO 碟形dto
     * @return {@link Result }
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 启动或停止
     *
     * @param status 状态
     * @param id     id
     * @return {@link Result }<{@link String }>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        log.info("起售停售：{}", id);
        dishService.startOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        List<DishVO> list = dishService.list(categoryId);
        return Result.success(list);
    }


}
