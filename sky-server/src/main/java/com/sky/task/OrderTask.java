package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@Api(tags = "定时订单任务类")
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOrder(){
        log.info("定时处理未付款订单{}", LocalDateTime.now());
//        处理未付款时间超过15分钟的
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAngOrderTime(Orders.PENDING_PAYMENT, time);
        if (ordersList != null && ordersList.size() > 0){
            for(Orders orders : ordersList){
                orders.setCancelReason("订单超时，自动取消");
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    private void processTimeDelivery(){
        log.info("定时处理订单{}", LocalDateTime.now());
//        处理未付款时间超过1小时的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAngOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        if (ordersList != null && ordersList.size() > 0){
            for(Orders orders : ordersList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
