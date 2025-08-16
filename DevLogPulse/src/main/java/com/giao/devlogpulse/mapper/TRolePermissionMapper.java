package com.giao.devlogpulse.mapper;

import com.giao.devlogpulse.entity.po.TRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 角色权限关联表 Mapper 接口
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Mapper
public interface TRolePermissionMapper extends BaseMapper<TRolePermission> {

    @Select("<script>" +
            "SELECT code FROM t_permission WHERE id IN " +
            "<foreach collection='permissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> selectPermissionCodesByIds(List<Long> permissionIds);
    
    @Select("<script>" +
            "SELECT DISTINCT p.code FROM t_permission p " +
            "INNER JOIN t_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> selectPermissionCodesByRoleIds(List<Long> roleIds);
}
