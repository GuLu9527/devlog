package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.common.exception.BusinessException;
import com.giao.devlogpulse.entity.po.TDepartment;
import com.giao.devlogpulse.model.dto.DepartmentCreateDTO;
import com.giao.devlogpulse.model.dto.DepartmentUpdateDTO;
import com.giao.devlogpulse.model.vo.DepartmentVO;
import com.giao.devlogpulse.service.ITDepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private ITDepartmentService departmentService;

    /**
     * 创建部门
     */
    @PostMapping
    @SaCheckPermission("department:create")
    public Result<DepartmentVO> create(@Valid @RequestBody DepartmentCreateDTO createDTO) {
        // 检查部门名称是否已存在
        if (departmentService.lambdaQuery().eq(TDepartment::getName, createDTO.getName()).exists()) {
            throw new BusinessException(ResultCode.DEPARTMENT_NAME_EXISTS);
        }
        
        // 检查父部门是否存在
        if (createDTO.getParentId() != null && departmentService.getById(createDTO.getParentId()) == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_FOUND, "父部门不存在");
        }

        TDepartment department = new TDepartment();
        BeanUtils.copyProperties(createDTO, department);
        department.setCreateTime(LocalDateTime.now());
        department.setUpdateTime(LocalDateTime.now());

        departmentService.save(department);
        
        DepartmentVO departmentVO = convertToVO(department);
        return Result.success(departmentVO);
    }

    /**
     * 分页查询部门列表
     */
    @GetMapping
    @SaCheckPermission("department:list")
    public Result<IPage<DepartmentVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        
        Page<TDepartment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TDepartment> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like(TDepartment::getName, name);
        }
        if (status != null) {
            queryWrapper.eq(TDepartment::getStatus, status);
        }
        
        queryWrapper.orderByDesc(TDepartment::getCreateTime);
        
        IPage<TDepartment> departmentPage = departmentService.page(page, queryWrapper);
        
        // 转换为VO
        IPage<DepartmentVO> voPage = departmentPage.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    /**
     * 获取部门树形结构
     */
    @GetMapping("/tree")
    public Result<List<DepartmentVO>> tree() {
        // 获取所有启用的部门
        List<TDepartment> allDepartments = departmentService.lambdaQuery()
                .eq(TDepartment::getStatus, 1)
                .orderByAsc(TDepartment::getName)
                .list();
        
        // 构建树形结构
        List<DepartmentVO> tree = buildDepartmentTree(allDepartments, null);
        
        return Result.success(tree);
    }

    /**
     * 构建部门树形结构
     */
    private List<DepartmentVO> buildDepartmentTree(List<TDepartment> departments, Long parentId) {
        return departments.stream()
                .filter(dept -> (parentId == null && dept.getParentId() == null) || 
                        (parentId != null && parentId.equals(dept.getParentId())))
                .map(dept -> {
                    DepartmentVO vo = convertToVO(dept);
                    
                    List<DepartmentVO> children = buildDepartmentTree(departments, dept.getId());
                    if (!children.isEmpty()) {
                        vo.setChildren(children);
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询部门详情
     */
    @GetMapping("/{id}")
    public Result<DepartmentVO> getById(@PathVariable Long id) {
        TDepartment department = departmentService.getById(id);
        if (department == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_FOUND);
        }
        
        DepartmentVO departmentVO = convertToVO(department);
        return Result.success(departmentVO);
    }

    /**
     * 更新部门信息
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateDTO updateDTO) {
        TDepartment existingDepartment = departmentService.getById(id);
        if (existingDepartment == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_FOUND);
        }
        
        // 检查部门名称是否已被其他部门使用
        if (!existingDepartment.getName().equals(updateDTO.getName()) &&
                departmentService.lambdaQuery().eq(TDepartment::getName, updateDTO.getName()).exists()) {
            throw new BusinessException(ResultCode.DEPARTMENT_NAME_EXISTS);
        }
        
        // 检查父部门是否存在
        if (updateDTO.getParentId() != null && !updateDTO.getParentId().equals(id) && 
            departmentService.getById(updateDTO.getParentId()) == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_FOUND, "父部门不存在");
        }
        
        // 检查是否将自己设置为父部门
        if (updateDTO.getParentId() != null && updateDTO.getParentId().equals(id)) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "不能将自己设置为父部门");
        }
        
        BeanUtils.copyProperties(updateDTO, existingDepartment);
        existingDepartment.setUpdateTime(LocalDateTime.now());
        
        departmentService.updateById(existingDepartment);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 检查部门是否存在
        TDepartment department = departmentService.getById(id);
        if (department == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_FOUND);
        }
        
        // 检查是否有子部门
        if (departmentService.lambdaQuery().eq(TDepartment::getParentId, id).exists()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "存在子部门，无法删除");
        }
        
        // 检查部门下是否有用户
        if (departmentService.hasUsers(id)) {
            throw new BusinessException(ResultCode.DEPARTMENT_HAS_USERS);
        }
        
        departmentService.removeById(id);
        return Result.success();
    }

    /**
     * 获取根部门列表
     */
    @GetMapping("/root")
    public Result<List<DepartmentVO>> getRootDepartments() {
        // 查询所有parentId为null的部门
        List<TDepartment> rootDepartments = departmentService.lambdaQuery()
                .isNull(TDepartment::getParentId)
                .eq(TDepartment::getStatus, 1)
                .orderByAsc(TDepartment::getName)
                .list();
        
        List<DepartmentVO> voList = rootDepartments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }
    
    /**
     * 转换为VO
     */
    private DepartmentVO convertToVO(TDepartment department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        
        // 设置状态文本
        vo.setStatusText(department.getStatus() == 1 ? "启用" : "禁用");
        
        // TODO: 设置父部门名称和用户数量
        
        return vo;
    }
}