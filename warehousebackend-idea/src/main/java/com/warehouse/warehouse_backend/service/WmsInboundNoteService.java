package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import com.warehouse.warehouse_backend.dto.request.InboundCreateDTO;
import com.warehouse.warehouse_backend.vo.InboundNoteVO;

public interface WmsInboundNoteService extends IService<WmsInboundNote> {
    boolean createDirectInbound(InboundCreateDTO dto);
    IPage<InboundNoteVO> getInboundWithDetails(int pageNum, int pageSize);
}