package com.sky.dto;

import io.swagger.annotations.ApiModel;

import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工更新密码传递的数据模型")
public class EmployeePasswordDTO implements Serializable {

    private Long id;

    private String newPassword;

    private String oldPassword;
}
