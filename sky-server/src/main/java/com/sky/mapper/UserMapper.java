package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 通过开放id获取
     *
     * @param openid openid
     * @return {@link User }
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 插入
     *
     * @param user 用户
     */
    void insert(User user);

    /**
     * 按id获取
     *
     * @param userId 用户id
     * @return {@link User }
     */
    @Select("select * from user where id=#{id}")
    User getById(Long userId);

    /**
     * 根据map条件统计用户数量
     * @param map 包含begin、end
     * @return 用户数量
     */
    Integer countByMap(Map<String, Object> map);
}
