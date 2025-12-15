package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.BaseRobot;
import com.warehouse.warehouse_backend.mapper.BaseRobotMapper;
import com.warehouse.warehouse_backend.service.BaseRobotService;
import org.springframework.stereotype.Service;

@Service
public class BaseRobotServiceImpl extends ServiceImpl<BaseRobotMapper, BaseRobot> implements BaseRobotService {

}