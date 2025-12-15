package com.warehouse.warehouse_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.entity.BaseLocation;
import com.warehouse.warehouse_backend.vo.LocationSensorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BaseLocationMapper extends BaseMapper<BaseLocation> {

    /**
     * 自定义查询：查询货位列表，并关联最新的传感器数据
     * 使用 @Select 注解直接写 SQL，适合简单的联表
     * * 逻辑：
     * 1. 查 base_location (l)
     * 2. 左连接 iot_sensor_data (s)
     * 3. 假设 iot_sensor_data 存储的是实时数据（每个货位只有一条最新记录）
     */
    @Select({"<script>",
            "SELECT l.id, l.location_code, l.area_zone, l.status, ", // ★ 仅选择存在的字段
            "s.temperature, s.humidity, s.is_alarm ",
            "FROM base_location l ",
            // 使用 LEFT JOIN 关联子查询结果，找到最新的 sensor_data_id
            "LEFT JOIN (",
            "   SELECT s1.* FROM iot_sensor_data s1 ",
            "   INNER JOIN (",
            "       SELECT location_id, MAX(record_time) AS max_time ", // 修复：使用 record_time 确定最新
            "       FROM iot_sensor_data GROUP BY location_id",
            "   ) s2 ON s1.location_id = s2.location_id AND s1.record_time = s2.max_time",
            ") s ON l.id = s.location_id ",

            "<where>",
            "1=1",
            "<if test='locationCode != null and locationCode != \"\"'>",
            "   AND l.location_code LIKE CONCAT('%', #{locationCode}, '%')",
            "</if>",
            "</where>",
            "ORDER BY l.location_code ASC",
            "</script>"})
    IPage<LocationSensorVO> selectLocationWithSensor(
            Page<?> page,
            @Param("locationCode") String locationCode
    );
}