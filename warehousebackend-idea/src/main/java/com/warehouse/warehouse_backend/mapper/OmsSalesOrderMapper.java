package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.entity.OmsSalesOrder;
import com.warehouse.warehouse_backend.vo.OmsSalesOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OmsSalesOrderMapper extends BaseMapper<OmsSalesOrder> {

    /**
     * 联表查询订单列表 (带出货物名称和客户名称)
     * 关联 base_goods 表和 sys_user 表
     */
    @Select("<script>" +
            "SELECT o.*, g.goods_name, u.username as customer_name, u.real_name " +
            "FROM oms_sales_order o " +
            "LEFT JOIN base_goods g ON o.goods_id = g.id " +
            "LEFT JOIN sys_user u ON o.customer_id = u.id " +
            "WHERE 1=1 " +
            "<if test='customerId != null'> AND o.customer_id = #{customerId} </if>" +
            "ORDER BY o.create_time DESC" +
            "</script>")
    IPage<OmsSalesOrderVO> selectOrderWithDetails(Page<?> page, @Param("customerId") Long customerId);
}