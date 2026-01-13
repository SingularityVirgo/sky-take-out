package com.sky.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    /**
     * 查询每日营业额（Map结构：key=日期, value=营业额）
     */
    @MapKey("orderDate")
    List<Map<String, Object>> getDailyTurnoverList(
            @Param("begin") LocalDateTime begin,
            @Param("end") LocalDateTime end
    );

    /**
     * 查询每日新增用户
     * 返回结构：[{orderDate=日期, newUser=新增用户数}, ...]
     */
    @MapKey("createTime")
    List<Map<String, Object>> getDailyNewUserList(
            @Param("begin") LocalDateTime begin,
            @Param("end") LocalDateTime end
    );

    /**
     * 获取每日有效订单盘点列表
     *
     * @param begin  开始
     * @param end    结束
     * @param status 状态
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     */
    @MapKey("orderDate")
    List<Map<String, Object>> getDailyValidOrderCountList(LocalDateTime begin, LocalDateTime end,Integer status);

    /**
     * 获得销售top10
     *
     * @param begin 开始
     * @param end   结束
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     */
    @MapKey("nameList")
    List<Map<String, Object>> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
