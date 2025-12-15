package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.SysUser;
import com.warehouse.warehouse_backend.mapper.SysUserMapper;
import com.warehouse.warehouse_backend.service.SysUserService;
import org.springframework.stereotype.Service;
import com.warehouse.warehouse_backend.dto.request.LoginDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public SysUser login(LoginDTO dto) {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());

        SysUser user = this.getOne(wrapper);

        // 2. 判断用户是否存在
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }

        // 3. 比对密码 (实际项目中应该用 BCryptPasswordEncoder，这里为了简单直接比对字符串)
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("密码错误！");
        }

        // 4. 登录成功，返回用户信息 (处于安全考虑，把密码置空再返回)
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public boolean addUser(SysUser user) {
        // 1. 校验用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        // count() 是 MyBatisPlus 提供的方法
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("添加失败：用户名 [" + user.getUsername() + "] 已存在！");
        }

        // 2. 密码处理：如果前端没传密码，给默认值 123456
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }

        // 3. 设置创建时间
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }

        // 4. 保存
        return this.save(user);
    }

    /**
     * ★ 实现删除用户逻辑
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserById(Long id) {
        // 这里可以加一些逻辑，比如 "admin" 账号不允许删除
        SysUser user = this.getById(id);
        if (user != null && "admin".equals(user.getUsername())) {
            throw new RuntimeException("系统超级管理员不允许删除！");
        }
        return this.removeById(id);
    }

    @Override
    public IPage<SysUser> getUserList(int pageNum, int pageSize, String name) {
        // 1. 创建分页对象
        Page<SysUser> page = new Page<>(pageNum, pageSize);

        // 2. 组装查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            // 模糊匹配：用户名 OR 真实姓名
            wrapper.and(w -> w.like(SysUser::getUsername, name)
                    .or()
                    .like(SysUser::getRealName, name));
        }

        // 3. 按时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 4. 执行查询
        return this.page(page, wrapper);
    }
}