package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流轨迹节点表
 */
@Data
@TableName("tms_transport_node")
public class TmsTransportNode {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String trackingNo;

    private String nodeName;

    private String description;

    private LocalDateTime arrivalTime;
}