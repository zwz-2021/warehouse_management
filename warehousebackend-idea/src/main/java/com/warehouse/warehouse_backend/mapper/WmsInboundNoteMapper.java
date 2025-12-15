package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import com.warehouse.warehouse_backend.vo.InboundNoteVO; // 引入 VO
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmsInboundNoteMapper extends BaseMapper<WmsInboundNote> {

    /**
     * 联表查询入库单，带出货物名称和货位编码
     */
    @Select("SELECT n.*, g.goods_name, l.location_code " +
            "FROM wms_inbound_note n " +
            "LEFT JOIN base_goods g ON n.goods_id = g.id " +
            "LEFT JOIN base_location l ON n.target_location_id = l.id " +
            "ORDER BY n.id DESC")
    IPage<InboundNoteVO> selectInboundWithDetails(Page<?> page);
}