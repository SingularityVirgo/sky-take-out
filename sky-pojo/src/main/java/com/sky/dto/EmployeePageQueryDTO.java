package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工页查询dto
 *
 * @author maziy
 * @date 2025/12/17
 */
@Data
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
