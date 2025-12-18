package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.EmployeePasswordDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 保存
     *
     * @param employeeDTO 员工dto
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 页面查询
     *
     * @param employeePageQueryDTO 员工页查询dto
     * @return {@link PageResult }
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启动或禁用
     *
     * @param status 状态
     * @param id     id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 按id获取
     *
     * @param id id
     * @return {@link Employee }
     */
    Employee getById(Long id);

    /**
     * 更新
     *
     * @param employeeDTO 员工dto
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 编辑密码
     *
     * @param employeePasswordDTO 员工密码dto
     * @return {@link Result }
     */
    Result<String> editPassword(EmployeePasswordDTO employeePasswordDTO);
}
