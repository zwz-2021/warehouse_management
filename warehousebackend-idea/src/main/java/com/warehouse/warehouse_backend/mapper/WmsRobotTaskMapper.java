package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import com.warehouse.warehouse_backend.vo.RobotTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmsRobotTaskMapper extends BaseMapper<WmsRobotTask> {

    /**
     * 关联查询任务详情
     * 逻辑说明：
     * 1. 关联 base_robot 表查 robot_name
     * 2. 如果是 INBOUND，关联 wms_inbound_note 再关联 base_goods 查货物名
     * 3. 如果是 OUTBOUND，关联 wms_outbound_note 再关联 base_goods 查货物名
     */
    @Select("SELECT t.*, r.robot_name, " +
            "CASE " +
            "  WHEN t.task_type = 'INBOUND' THEN g_in.goods_name " +
            "  WHEN t.task_type = 'OUTBOUND' THEN g_out.goods_name " +
            "  ELSE NULL " +
            "END AS goods_name " +
            "FROM wms_robot_task t " +
            "LEFT JOIN base_robot r ON t.robot_id = r.id " +
            // 关联入库单线索
            "LEFT JOIN wms_inbound_note in_note ON t.task_type = 'INBOUND' AND t.source_doc_id = in_note.id " +
            "LEFT JOIN base_goods g_in ON in_note.goods_id = g_in.id " +
            // 关联出库单线索
            "LEFT JOIN wms_outbound_note out_note ON t.task_type = 'OUTBOUND' AND t.source_doc_id = out_note.id " +
            "LEFT JOIN base_goods g_out ON out_note.goods_id = g_out.id " +
            "ORDER BY t.create_time DESC")
    IPage<RobotTaskVO> selectTaskWithDetails(Page<?> page);
}