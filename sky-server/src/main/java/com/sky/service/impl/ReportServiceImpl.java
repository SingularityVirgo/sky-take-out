package com.sky.service.impl;

import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 获取营业额统计
     *
     * @param beginDate 开始
     * @param endDate   结束
     */
    @Override
    /**
     * 生成营业额报表VO
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        // 1. 构建完整日期范围
        List<LocalDate> dateRange = getDateRange(beginDate, endDate);

        // 2. 查询原始数据（List<Map>）
        List<Map<String, Object>> rawData = reportMapper.getDailyTurnoverList(
                beginDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );
        log.info("原始查询结果: {}", rawData);

        // 3. 转换为 Map<LocalDate, BigDecimal>
        Map<LocalDate, BigDecimal> turnoverMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Date) row.get("orderDate")).toLocalDate(),
                        row -> (BigDecimal) row.get("turnover")
                ));
        log.info("转换后的Map: {}", turnoverMap);

        // 4. 拼接字符串（无数据补0）
        StringJoiner dateJoiner = new StringJoiner(",");
        StringJoiner turnoverJoiner = new StringJoiner(",");

        for (LocalDate date : dateRange) {
            dateJoiner.add(date.toString());
            BigDecimal turnover = turnoverMap.getOrDefault(date, BigDecimal.ZERO);
            turnoverJoiner.add(turnover.toPlainString());
        }

        return TurnoverReportVO.builder()
                .dateList(dateJoiner.toString())
                .turnoverList(turnoverJoiner.toString())
                .build();
    }
    @Transactional
    public UserReportVO getUserStatistics(LocalDate beginDate, LocalDate endDate) {
        // 1. 查询原始数据
        List<Map<String, Object>> allUserRawData = reportMapper.getDailyNewUserList(
                null,
                beginDate.minusDays(1).atTime(23, 59, 59)
        );

        // 2. 转换为Map结构
        Map<LocalDate, Long> allUserMap = allUserRawData.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row.get("createTime")).toLocalDate(),
                        row -> (Long) row.get("newUser")
                ));
        // 1. 查询原始数据
        List<Map<String, Object>> newUserRawData = reportMapper.getDailyNewUserList(
                beginDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 2. 转换为Map结构
        Map<LocalDate, Long> newUserMap = newUserRawData.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row.get("createTime")).toLocalDate(),
                        row -> (Long) row.get("newUser")
                ));

        // 3. 构建日期范围
        List<LocalDate> dateRange = getDateRange(beginDate, endDate);

        // 4. 拼接三个字符串
        StringJoiner dateSj = new StringJoiner(",");
        StringJoiner newUserSj = new StringJoiner(",");
        StringJoiner totalUserSj = new StringJoiner(",");

        Long totalUsers = allUserMap.getOrDefault(beginDate.plusDays(-1), 0L);

        for (LocalDate date : dateRange) {
            Long newUsers = newUserMap.getOrDefault(date, 0L);

            totalUsers += newUsers;

            dateSj.add(date.toString());
            newUserSj.add(String.valueOf(newUsers));
            totalUserSj.add(String.valueOf(totalUsers));
        }

        return UserReportVO.builder()
                .dateList(dateSj.toString())
                .newUserList(newUserSj.toString())
                .totalUserList(totalUserSj.toString())
                .build();
    }

    /**
     * 获取订单统计信息
     *
     * @param beginDate 开始
     * @param endDate   结束
     * @return {@link OrderReportVO }
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate beginDate, LocalDate endDate) {
        // 1. 查询每日订单总数
        List<Map<String, Object>> orderCountRaw = reportMapper.getDailyValidOrderCountList(
                beginDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                null
        );
        Map<LocalDate, Long> orderCountMap = orderCountRaw.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row.get("orderDate")).toLocalDate(),
                        row -> (Long) row.get("orderCount")
                ));

        // 2. 查询每日有效订单数
        List<Map<String, Object>> validOrderRaw = reportMapper.getDailyValidOrderCountList(
                beginDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                5
        );
        Map<LocalDate, Long> validOrderMap = validOrderRaw.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row.get("orderDate")).toLocalDate(),
                        row -> (Long) row.get("orderCount")
                ));

        // 3. 构建日期范围
        List<LocalDate> dateRange = getDateRange(beginDate, endDate);

        // 4. 拼接字符串和计算总量
        StringJoiner dateSj = new StringJoiner(",");
        StringJoiner orderCountSj = new StringJoiner(",");
        StringJoiner validOrderCountSj = new StringJoiner(",");

        long totalOrderCount = 0;
        long validOrderCount = 0;

        for (LocalDate date : dateRange) {
            Long dailyOrder = orderCountMap.getOrDefault(date, 0L);
            Long dailyValid = validOrderMap.getOrDefault(date, 0L);

            totalOrderCount += dailyOrder;
            validOrderCount += dailyValid;

            dateSj.add(date.toString());
            orderCountSj.add(String.valueOf(dailyOrder));
            validOrderCountSj.add(String.valueOf(dailyValid));
        }

        // 5. 计算完成率
        double completionRate = totalOrderCount == 0 ? 0.0 :
                (double) validOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(dateSj.toString())
                .orderCountList(orderCountSj.toString())
                .validOrderCountList(validOrderCountSj.toString())
                .totalOrderCount((int)totalOrderCount)
                .validOrderCount((int)validOrderCount)
                .orderCompletionRate(completionRate)
                .build();
    }

    /**
     * 获得销售top10
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return {@link SalesTop10ReportVO }
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate beginDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = reportMapper.getSalesTop10(
                beginDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        StringJoiner nameList = new StringJoiner(",");
        StringJoiner numberList = new StringJoiner(",");

        for (Map<String, Object> row : rawData) {
            nameList.add((String) row.get("nameList"));
            numberList.add(String.valueOf(row.get("numberList")));
        }

        return SalesTop10ReportVO.builder()
                .nameList(nameList.toString())
                .numberList(numberList.toString())
                .build();
    }

    /**
     * 获取业务数据
     *
     * @param response 响应
     */
    @Override
    public void getBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据 --查询最近30天
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusDays(30);
        LocalDate end = now.minusDays(1);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN),
                                           LocalDateTime.of(end, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中

        try{
            InputStream inputStream = this.getClass().getClassLoader()
                    .getResourceAsStream("template/business_data_template.xlsx");
            XSSFWorkbook sheets = null;
            if (inputStream != null) {
                sheets = new XSSFWorkbook(inputStream);
            }
            XSSFSheet sheet = sheets.getSheet("Sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);

            // 获得第四行
            XSSFRow row = sheet.getRow(3);
            // 填充数据--营业额
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            // 填充数据--订单完成率
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            // 填充数据--新增用户数
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获取第五行
            row = sheet.getRow(4);
            // 填充数据--有效订单数
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            // 填充数据--平均客单价
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // 填充明细数据
            for(int i = 0; i < 30; i++){
                LocalDate date = begin.plusDays(i);
                //查询某天营业顺序
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 将Excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            sheets.write(outputStream);
            // 关闭流
            outputStream.close();
            sheets.close();

        }catch (IOException e){
            e.printStackTrace();
        }




    }

    /**
     * 生成连续日期列表
     */
    private List<LocalDate> getDateRange(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = begin;

        while (!current.isAfter(end)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }
}
