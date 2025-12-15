package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import com.warehouse.warehouse_backend.mapper.WmsOutboundNoteMapper;
import com.warehouse.warehouse_backend.service.WmsOutboundNoteService;
import com.warehouse.warehouse_backend.vo.WmsOutboundNoteVO;
import org.springframework.stereotype.Service;

@Service
public class WmsOutboundNoteServiceImpl extends ServiceImpl<WmsOutboundNoteMapper, WmsOutboundNote> implements WmsOutboundNoteService {
    @Override
    public IPage<WmsOutboundNoteVO> getOutboundListWithDetails(int pageNum, int pageSize) {
        return baseMapper.selectOutboundWithDetails(new Page<>(pageNum, pageSize));
    }
}