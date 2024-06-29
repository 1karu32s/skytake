package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("修改店铺状态为{}",status);
        redisTemplate.opsForValue().set("Shop_Status", status);
        return Result.success();
    }


    @GetMapping("/status")
    @ApiOperation(value = "查看店铺营业状态")
    public Result<Integer> getStatus(){
        log.info("正在查看当前店铺的营业状态");
        Integer shopStatus = (Integer)redisTemplate.opsForValue().get("Shop_Status");
        return Result.success(shopStatus);
    }
}
