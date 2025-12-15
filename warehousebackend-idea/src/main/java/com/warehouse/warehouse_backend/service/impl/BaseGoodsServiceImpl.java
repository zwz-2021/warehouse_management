package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.BaseGoods;
import com.warehouse.warehouse_backend.mapper.BaseGoodsMapper;
import com.warehouse.warehouse_backend.service.BaseGoodsService;
import org.springframework.stereotype.Service;

@Service
public class BaseGoodsServiceImpl extends ServiceImpl<BaseGoodsMapper, BaseGoods> implements BaseGoodsService {

}