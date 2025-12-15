package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.OmsSalesOrder;
import com.warehouse.warehouse_backend.entity.TmsTransportOrder;
import com.warehouse.warehouse_backend.mapper.OmsSalesOrderMapper; // 引入订单Mapper
import com.warehouse.warehouse_backend.mapper.TmsTransportOrderMapper;
import com.warehouse.warehouse_backend.service.TmsTransportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TmsTransportOrderServiceImpl extends ServiceImpl<TmsTransportOrderMapper, TmsTransportOrder> implements TmsTransportOrderService {

    @Autowired
    private OmsSalesOrderMapper salesOrderMapper; // ★ 需要操作订单表

    @Override
    @Transactional(rollbackFor = Exception.class) // ★ 事务控制
    public boolean signOrder(Long transportId) {

        // 1. 获取运单信息
        TmsTransportOrder transportOrder = this.getById(transportId);
        if (transportOrder == null) {
            throw new RuntimeException("运单不存在！");
        }

        // 2. 更新运单状态 -> SIGNED
        transportOrder.setStatus("SIGNED");
        // transportOrder.setSignTime(LocalDateTime.now()); // 如果你表里有签收时间字段的话
        this.updateById(transportOrder);

        // 3. 更新关联的销售订单状态 -> 4 (已完成)
        String orderNo = transportOrder.getOrderNo();

        LambdaQueryWrapper<OmsSalesOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OmsSalesOrder::getOrderNo, orderNo);
        OmsSalesOrder salesOrder = salesOrderMapper.selectOne(queryWrapper);

        if (salesOrder != null) {
            salesOrder.setStatus(4); // 4-已完成/已签收
            salesOrderMapper.updateById(salesOrder);
        } else {
            throw new RuntimeException("关联的销售订单 [" + orderNo + "] 未找到！");
        }

        return true;
    }
}