package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.TmsTransportOrder;

public interface TmsTransportOrderService extends IService<TmsTransportOrder> {
    boolean signOrder(Long transportId);
}