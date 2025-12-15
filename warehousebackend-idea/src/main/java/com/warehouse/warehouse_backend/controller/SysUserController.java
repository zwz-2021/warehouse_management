package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.SysUser;
import com.warehouse.warehouse_backend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.warehouse.warehouse_backend.dto.request.LoginDTO;

import java.util.List;

@RestController
@RequestMapping("/user")
public class SysUserController {
    @Autowired private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody LoginDTO loginDTO) {
        try {
            SysUser user = sysUserService.login(loginDTO);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @GetMapping("/clients")
    public Result<List<SysUser>> getClients() {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getRole, "CLIENT"); // 只查客户
        return Result.success(sysUserService.list(query));
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysUser user) {
        try {
            // 调用 Service 里的 addUser 方法（里面包含了重名检测）
            boolean success = sysUserService.addUser(user);
            return Result.success(success);
        } catch (Exception e) {
            // 如果 Service 抛出 "用户名已存在" 异常，这里会捕获并返回给前端
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean success = sysUserService.deleteUserById(id);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<IPage<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name
    ) {
        // 调用 Service 层的 getUserList 方法
        IPage<SysUser> pageResult = sysUserService.getUserList(pageNum, pageSize, name);

        // 敏感数据处理 (密码脱敏)
        pageResult.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(pageResult);
    }

}