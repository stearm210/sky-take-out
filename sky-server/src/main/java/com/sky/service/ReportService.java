package com.sky.service;

import com.sky.vo.TurnoverReportVO;

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
}
