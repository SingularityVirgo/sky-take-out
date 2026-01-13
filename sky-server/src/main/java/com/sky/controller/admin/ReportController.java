package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计管理接口")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;
    /**
     * 获取营业额统计
     *
     * @param begin 开始
     * @param end   结束
     * @return {@link Result }<{@link TurnoverReportVO }>
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> TurnoverStatistics
            (@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询营业额数据：{}到{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }
    /**
     * 获取用户统计
     *
     * @param begin 开始
     * @param end   结束
     * @return {@link Result }<{@link UserReportVO }>
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics
            (@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询用户数据：{}到{}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }
    /**
     * 获取订单统计
     *
     * @param begin 开始
     * @param end   结束
     * @return {@link Result }<{@link OrderReportVO }>
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics
            (@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询订单数据：{}到{}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }
    /**
     * 获取销量排名
     *
     * @param begin 开始
     * @param end   结束
     * @return {@link Result }<{@link SalesTop10ReportVO }>
     */
    @GetMapping("/top10")
    @ApiOperation("销量排名")
    public Result<SalesTop10ReportVO> top10
            (@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询销量排名：{}到{}", begin, end);
        return Result.success(reportService.getSalesTop10(begin, end));
    }

    /**
     * 导出业务数据
     *
     * @param response 响应
     */
    @GetMapping("/export")
    @ApiOperation("导出营业数据")
    public void exportBusinessData(HttpServletResponse response){
        reportService.getBusinessData(response);

    }

}
