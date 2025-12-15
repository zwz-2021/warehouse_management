package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.entity.WmsInventory;
import com.warehouse.warehouse_backend.vo.InventoryDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmsInventoryMapper extends BaseMapper<WmsInventory> {

    /**
     * 联表查询库存详情，显示货物名称和货位编码
     * 注意：这里使用了 MyBatis 的 if 语句来进行动态条件查询。
     */
    @Select({"<script>",
            "SELECT t1.*, t2.goods_name, t3.location_code ",
            "FROM wms_inventory t1 ",
            "LEFT JOIN base_goods t2 ON t1.goods_id = t2.id ",
            "LEFT JOIN base_location t3 ON t1.location_id = t3.id ",
            "<where>",
            "1=1",
            "<if test='goodsId != null'>",
            "   AND t1.goods_id = #{goodsId}",
            "</if>",
            "<if test='locationId != null'>",
            "   AND t1.location_id = #{locationId}",
            "</if>",
            "</where>",
            "ORDER BY t1.update_time DESC",
            "</script>"})
    IPage<InventoryDetailVO> selectInventoryDetail(
            Page<?> page,
            @Param("goodsId") Long goodsId,
            @Param("locationId") Long locationId
    );
}