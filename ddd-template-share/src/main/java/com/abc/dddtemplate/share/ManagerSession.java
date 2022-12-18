package com.abc.dddtemplate.share;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author <template/>
 * @date
 */
@Service
public class ManagerSession {
    private static final ThreadLocal<AdminUserInfo> THREAD_LOCAL = new ThreadLocal<AdminUserInfo>();

    public void clear(){
        THREAD_LOCAL.remove();
    }

    public void set(AdminUserInfo adminUserInfo){
        THREAD_LOCAL.set(adminUserInfo);
    }

    public Optional<AdminUserInfo> get(){
        return Optional.ofNullable(THREAD_LOCAL.get());
    }

    @Data
    public static class AdminUserInfo {
        /**
         * 管理员id
         */
        private Integer adminId;
        /**
         * 管理员的用户名 企业邮箱
         */
        private String userName;
        /**
         * 管理员的昵称
         */
        private String nickName;
        /**
         * 真实姓名
         */
        private String fullName;
        /**
         * 角色id
         */
        private Integer roleId;
        /**
         * admin_role 角色ids
         */
        private String roleIds;
        /**
         * 部门id
         */
        private Integer departmentId;
        /**
         * 权限ID
         */
        private String priIds;
        /**
         * 汇报人
         */
        private Integer leadId;
        /**
         * 职务
         */
        private String title;
        /**
         * 电话
         */
        private String tel;
        /**
         * 工作城市
         */
        private Integer workCity;
        /**
         * 头像
         */
        private String avatar;
        /**
         * 部门id
         */
        private Integer adminDepartmentId;
        /**
         * 是否部门主管
         */
        private Integer isDepartmentLeader;
        /**
         * 登录令牌
         */
        private String jwtToken;
        /**
         * socketId
         */
        private String socketId;
    }
}
