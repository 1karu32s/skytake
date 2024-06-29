package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation(value = "查看店铺营业状态")
    public Result<Integer> getStatus(){
        log.info("正在查看当前店铺的营业状态");
        Integer shopStatus = (Integer)redisTemplate.opsForValue().get("Shop_Status");
        return Result.success(shopStatus);
    }
}
