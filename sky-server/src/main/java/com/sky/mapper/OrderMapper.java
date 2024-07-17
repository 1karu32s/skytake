package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    Double sumByMap(Map map) ;

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from sky_take_out.orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);




    @Select("select * from sky_take_out.orders where status = #{status} and order_time = #{orderTime}")
    List<Orders> getByStatusAngOrderTime(Integer status, LocalDateTime orderTime);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from sky_take_out.orders where id=#{id}")
    Orders getById(Long id);

    @Select("select count(id) from sky_take_out.orders where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);

    Integer countByMap(Map map);



    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
