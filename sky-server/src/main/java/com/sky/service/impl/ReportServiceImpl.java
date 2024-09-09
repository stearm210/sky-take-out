package com.sky.service.impl;/**
 * @author casillas
 * @date 9/9/2024
 **/

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Auther: stearm210
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
/*
* 营业额统计操作
* */
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合中用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList =new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //计算日期。计算指定的日期的后一天对应的日期。总共的日期总数是begin到end之间的所有日期总数
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        /*
        * 循环查询并计算集合中的日期对应的订单金额合计
        * */
        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            //查询date日期对应的营业额数据，营业额是指，状态为“已完成”的订单的订单金额合计
            //获得起始时间和结束时间进行计算
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //select sum(amount) from orders where order_time > ? and order_time < ? and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);

            //如果当天的营业额为null，则需要进行转换
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);

        }

        //将list集合中的所有字符取出，之后变成一个字符串进行拼接
        /*
        * 封装对应的返回结果
        * */
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }
}