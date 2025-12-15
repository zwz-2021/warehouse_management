package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.warehouse_backend.entity.TmsTransportOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TmsTransportOrderMapper extends BaseMapper<TmsTransportOrder> {
    boolean signOrder(Long transportId);
}