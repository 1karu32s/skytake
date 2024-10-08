package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品接口")
public class DishController {

    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
//        清除缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        log.info("清理redis缓存");
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "菜品分页")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询，参数{}",dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation(value = "删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品id{}", ids);
        dishService.deleteBatch(ids);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        log.info("清理redis缓存");
        return Result.success();
    }

    @PutMapping
    @ApiOperation(value = "修改菜品根据id")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品为{}",dishDTO);
        dishService.upadteWithFlavor(dishDTO);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        log.info("清理redis缓存");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("查询的菜品id为{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品{}状态修改为{}",id ,status);
        dishService.startOrStop(status, id);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }

}
