package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.WmsInventory;
import com.warehouse.warehouse_backend.mapper.WmsInventoryMapper;
import com.warehouse.warehouse_backend.service.WmsInventoryService;
import org.springframework.stereotype.Service;

@Service
public class WmsInventoryServiceImpl extends ServiceImpl<WmsInventoryMapper, WmsInventory> implements WmsInventoryService {

}