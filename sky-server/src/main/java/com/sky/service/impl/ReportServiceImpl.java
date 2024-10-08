package com.sky.service.impl;/**
 * @author casillas
 * @date 9/9/2024
 **/

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
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;


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

    /*
     * 指定时间区间内的用户数据进行统计
     * */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间的每天对应的日期
        List<LocalDate> dateList =new ArrayList<>();
        dateList.add(begin);
        //将日期全部添加到对应的集合中去
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        //存放每天的新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量
        List<Integer> totalUserList = new ArrayList<>();

        //遍历出每一天的用户数量和新增用户数量
        for (LocalDate date : dateList){
            //起始时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //结束时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //构造map
            Map map = new HashMap();
            map.put("end",endTime);

            //先查总用户数量
            Integer totalUser = userMapper.countByMap(map);

            //之后再管新增用户数量
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);

            //总用户数量
            totalUserList.add(totalUser);
            //新增用户数量
            newUserList.add(newUser);
        }

        //封装结果的额数据
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /*
     * 在指定时间区间内的用户订单数据的统计操作
     * */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间的每天对应的日期
        List<LocalDate> dateList =new ArrayList<>();
        dateList.add(begin);
        //将日期全部添加到对应的集合中去
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每天的订单总数
        List<Integer> orderCountList = new ArrayList<>();

        //存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        //遍历集合，查询每天的有效订单数和订单总数
        for (LocalDate date : dateList){
            //查询每天订单总数
            //获取开始时刻
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //获取结束时刻
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询每天的订单总数
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            //加入集合
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //计算时间区间内的订单总数量
        //steam流写法：遍历集合之后使用sum求和，返回的类型为integer，使用get方法获得
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        //当分子不为0时才进行计算
        if (totalOrderCount != 0){
            //计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        //构造返回类型
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate).build();
    }


    /*
    根据对应的条件统计订单的数量
    * 查询订单的数量
    * */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

    /*
     * 统计指定时间区间内的销量排名前十
     * */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //计算起始时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        //计算结束时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        //使用steam流取得集合中的菜品名字
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        //拼接得到的名字
        String nameList = StringUtils.join(names,",");

        //获得对应的菜品数量
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers,",");

        //封装返回结果数据
        return SalesTop10ReportVO.builder()
                .nameList(nameList).numberList(numberList)
                .build();
    }

    /*
    * 导出运营数据报表
    * */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1.查询数据库，获取营业数据-查询最近30天的运营数据
        //指定对应的时间区间
        //当天的前30天的
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        //昨天
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //传入参数，查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2.查到的数据通过POI写入到Excel文件中
        //从类路径下来读取资源,获得输入流对象
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模版文件来创建新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据-时间
            sheet.getRow(1).getCell(1).setCellValue("时间:" + dateBegin + "至" + dateEnd);

            //获取第4行，营业额数据填充
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            //订单完成率
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            //新增用户的数量
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获取第5行
            row = sheet.getRow(5);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());


            /*
            * 填充明细数据
            * */
            //这里的30根据上面的起始和结束时间获取到
            //这里的循环是对30天中每一天进行循环操作
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(1);
                //查询这一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MAX), LocalDateTime.of(date, LocalTime.MIN));

                //获得某一行
                row = sheet.getRow(7 + i);
                //填充日期
                row.getCell(1).setCellValue(date.toString());
                //填充营业额
                row.getCell(2).setCellValue(businessData.getTurnover());
                //填充有效订单
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                //填充订单完成率
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                //填充平均客单价
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                //填充新增用户数
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3.通过输出流将excel文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}