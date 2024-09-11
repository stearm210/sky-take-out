package com.sky.service;

import com.sky.entity.User;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @author casillas
 * @date 9/9/2024
 **/
public interface ReportService {
    /*
    * 统计指定时间区间内的营业额数据
    * */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /*
    * 用户数据统计操作,在指定时间区间内的统计
    * */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /*
    * 订单数据统计操作,在指定时间区间内的统计
    * */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /*
    * 指定时间区间内的销量排名前十
    * */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /*
    * 导出运营数据报表
    * */
    void exportBusinessData(HttpServletResponse response);
}
