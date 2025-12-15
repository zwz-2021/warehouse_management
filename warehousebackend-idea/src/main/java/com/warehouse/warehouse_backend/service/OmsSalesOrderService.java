package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.OmsSalesOrder;
import com.warehouse.warehouse_backend.dto.request.OrderCreateDTO;
import com.warehouse.warehouse_backend.vo.OmsSalesOrderVO;

public interface OmsSalesOrderService extends IService<OmsSalesOrder> {
    boolean createOrderWithOutbound(OrderCreateDTO dto);
    IPage<OmsSalesOrderVO> getOrderListWithDetails(int pageNum, int pageSize, Long customerId);
}