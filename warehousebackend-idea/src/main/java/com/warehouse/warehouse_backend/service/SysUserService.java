package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.SysUser;
import com.warehouse.warehouse_backend.dto.request.LoginDTO;

public interface SysUserService extends IService<SysUser> {
    SysUser login(LoginDTO loginDTO);
    boolean addUser(SysUser user);
    boolean deleteUserById(Long id);
    IPage<SysUser> getUserList(int pageNum, int pageSize, String name);
}