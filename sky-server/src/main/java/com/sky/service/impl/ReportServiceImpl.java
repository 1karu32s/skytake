package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO getTurnoverStatistic(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        String join = StringUtils.join(dateList, ",");
        String join1 = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(join)
                .turnoverList(join1)
                .build();
    }

    @Override
    public UserReportVO getUserStatistic(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            Integer totalUserNumber = userMapper.countByMap(map);
            totalUserNumber = totalUserNumber == null ? 0 : totalUserNumber;
            totalUserList.add(totalUserNumber);
            map.put("begin", beginTime);
            Integer newUserNumber = userMapper.countByMap(map);
            newUserNumber = newUserNumber == null ? 0 : newUserNumber;
            newUserList.add(newUserNumber);
        }

        String join = StringUtils.join(dateList, ",");
        String join1 = StringUtils.join(newUserList, ",");
        String join2 = StringUtils.join(totalUserList, ",");
        return UserReportVO.builder()
                .dateList(join)
                .newUserList(join1)
                .totalUserList(join2)
                .build();
    }


    @Override
    public OrderReportVO getOrdersStatistic(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();
        // 1. 日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        if (dateList == null) {
            return orderReportVO;
        }
        // 2. 订单数列表
        List<Integer> totalOrderList = new ArrayList<>();
        // 3. 有效订单数列表
        List<Integer> validOrderList = new ArrayList<>();
        // 4. 订单总数
        Integer totalOrderCount = 0;
        // 5. 有效订单总数
        Integer validOrderCount = 0;
        for (LocalDate localDate : dateList) {
            Map map = new HashMap();
            map.put("begin", LocalDateTime.of(localDate, LocalTime.MIN));
            map.put("end", LocalDateTime.of(localDate, LocalTime.MAX));
            Integer total = orderMapper.countByMap(map);
            total = total == null ? 0 : total;
            map.put("status", Orders.COMPLETED);
            Integer valid = orderMapper.countByMap(map);
            valid = valid == null ? 0 : valid;
            totalOrderList.add(total);
            validOrderList.add(valid);
            totalOrderCount += total;
            validOrderCount += valid;
        }
        // 6. 订单完成率
        Double completionR = 0.0;
        if (totalOrderCount != null) {
            completionR = validOrderCount * 1.0 / totalOrderCount;
        }

        orderReportVO.setDateList(StringUtils.join(dateList, ","));
        orderReportVO.setOrderCountList(StringUtils.join(totalOrderList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderList, ","));
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(validOrderCount);
        orderReportVO.setOrderCompletionRate(completionR);

        return orderReportVO;

    }

    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO
                .builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void exportBussinessData(HttpServletResponse response) {
//        获取营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now();
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));
//        利用poi写入excel
//        反射
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("时间"+dateBegin+"至"+dateEnd);
            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

//            填充明细数据


            for (int i = 0; i < 30; i ++) {
                LocalDate date = dateBegin.plusDays(i);
                workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row1 = sheet1.getRow(7 + i);
                row1.getCell(1).setCellValue(date.toString());
                row1.getCell(2).setCellValue(businessData.getTurnover());
                row1.getCell(3).setCellValue(businessData.getValidOrderCount());
                row1.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row1.getCell(5).setCellValue(businessData.getUnitPrice());
                row1.getCell(6).setCellValue(businessData.getNewUsers());
            }
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
            excel.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        利用输出流下载到浏览器
    }
}
