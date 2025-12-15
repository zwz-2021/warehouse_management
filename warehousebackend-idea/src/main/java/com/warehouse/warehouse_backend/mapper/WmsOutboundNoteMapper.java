package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import com.warehouse.warehouse_backend.vo.WmsOutboundNoteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmsOutboundNoteMapper extends BaseMapper<WmsOutboundNote> {

    /**
     * 联表查询出库单 (关联货物表)
     */
    @Select("SELECT n.*, g.goods_name " +
            "FROM wms_outbound_note n " +
            "LEFT JOIN base_goods g ON n.goods_id = g.id " +
            "ORDER BY n.id DESC")
    IPage<WmsOutboundNoteVO> selectOutboundWithDetails(Page<?> page);
}