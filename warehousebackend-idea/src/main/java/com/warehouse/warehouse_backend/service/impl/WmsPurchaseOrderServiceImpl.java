package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.WmsPurchaseOrder;
import com.warehouse.warehouse_backend.mapper.WmsPurchaseOrderMapper;
import com.warehouse.warehouse_backend.service.WmsPurchaseOrderService;
import org.springframework.stereotype.Service;

@Service
public class WmsPurchaseOrderServiceImpl extends ServiceImpl<WmsPurchaseOrderMapper, WmsPurchaseOrder> implements WmsPurchaseOrderService {

}