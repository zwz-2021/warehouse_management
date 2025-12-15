package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import com.warehouse.warehouse_backend.vo.WmsOutboundNoteVO;

public interface WmsOutboundNoteService extends IService<WmsOutboundNote> {
    IPage<WmsOutboundNoteVO> getOutboundListWithDetails(int pageNum, int pageSize);
}