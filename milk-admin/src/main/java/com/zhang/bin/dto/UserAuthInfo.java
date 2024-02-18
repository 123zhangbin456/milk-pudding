package com.zhang.bin.dto;

import lombok.Data;

import java.util.Set;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.dto
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-31  17:57
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UserAuthInfo {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户状态(1:正常;0:禁用)
     */
    private Integer status;

    /**
     * 用户角色编码集合 ["ROOT","ADMIN"]
     */
    private Set<String> roles;

    /**
     * 用户权限标识集合
     */
    private Set<String> perms;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 数据权限范围
     */
    private Integer dataScope;
}
