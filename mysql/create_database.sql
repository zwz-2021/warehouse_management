/*
 * 数据库初始化脚本 - 智能仓储与物流协同系统
 * 作者: zwz
 * 描述: 包含基础数据、仓储、订单、物流及IoT设备的表结构与初始化数据
 */

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `warehouse` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `warehouse`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 基础数据模块
-- ==========================================

-- 1.1 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) NOT NULL COMMENT '角色: ADMIN(管理员), OPERATOR(操作员), CLIENT(客户)',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB COMMENT='系统用户表';

-- 1.2 货物基础信息表
DROP TABLE IF EXISTS `base_goods`;
CREATE TABLE `base_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_code` varchar(50) NOT NULL COMMENT '货物编号',
  `goods_name` varchar(100) NOT NULL COMMENT '货物名称',
  `spec` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `unit` varchar(20) DEFAULT '个' COMMENT '单位',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `warn_threshold` int(11) DEFAULT '10' COMMENT '库存预警阈值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_goods_code` (`goods_code`)
) ENGINE=InnoDB COMMENT='货物基础信息表';

-- 1.3 货位表
DROP TABLE IF EXISTS `base_location`;
CREATE TABLE `base_location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_code` varchar(50) NOT NULL COMMENT '货位编号 (如 A-01-01)',
  `area_zone` varchar(50) DEFAULT 'A区' COMMENT '所属区域',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态: 0-空闲, 1-占用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_location_code` (`location_code`)
) ENGINE=InnoDB COMMENT='仓库货位表';

-- 1.4 机器人表 (base_robot)
DROP TABLE IF EXISTS `base_robot`;
CREATE TABLE `base_robot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `robot_code` varchar(50) NOT NULL COMMENT '机器人编号',
  `robot_name` varchar(50) DEFAULT NULL COMMENT '机器人名称',
  `status` varchar(20) DEFAULT 'IDLE' COMMENT '状态: IDLE(空闲), BUSY(工作中), ERROR(故障), CHARGING(充电)',
  `work_mode` varchar(20) DEFAULT 'AUTO' COMMENT '调度模式: AUTO(自动), MANUAL(手动)',
  `battery` int(11) DEFAULT '100' COMMENT '电量百分比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='AGV机器人表';


-- ==========================================
-- 入库模块
-- ==========================================

-- 2.1 采购单
DROP TABLE IF EXISTS `wms_purchase_order`;
CREATE TABLE `wms_purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `po_no` varchar(50) NOT NULL COMMENT '采购单号',
  `supplier_name` varchar(100) DEFAULT NULL COMMENT '供应商名称',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态: 0-待入库, 1-入库中, 2-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_po_no` (`po_no`)
) ENGINE=InnoDB COMMENT='采购单主表';

-- 2.2 入库单明细
DROP TABLE IF EXISTS `wms_inbound_note`;
CREATE TABLE `wms_inbound_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `po_no` varchar(50) NOT NULL COMMENT '关联采购单号',
  `goods_id` bigint(20) NOT NULL COMMENT '货物ID',
  `plan_qty` int(11) NOT NULL COMMENT '计划数量',
  `actual_qty` int(11) DEFAULT '0' COMMENT '实收数量',
  `qc_status` tinyint(4) DEFAULT '0' COMMENT '质检状态: 0-待检, 1-合格, 2-不合格',
  `putaway_status` tinyint(4) DEFAULT '0' COMMENT '上架状态: 0-待分配, 1-待上架, 2-已上架',
  `target_location_id` bigint(20) DEFAULT NULL COMMENT '分配的货位ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='入库单明细表';


-- ==========================================
-- 第三部分：在库与环境模块
-- ==========================================

-- 3.1 实时库存表
DROP TABLE IF EXISTS `wms_inventory`;
CREATE TABLE `wms_inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` bigint(20) NOT NULL COMMENT '货物ID',
  `location_id` bigint(20) NOT NULL COMMENT '所在货位ID',
  `total_qty` int(11) NOT NULL DEFAULT '0' COMMENT '物理库存',
  `locked_qty` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存(预占)',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_goods_location` (`goods_id`, `location_id`)
) ENGINE=InnoDB COMMENT='实时库存表';

-- 3.2 传感器数据表
DROP TABLE IF EXISTS `iot_sensor_data`;
CREATE TABLE `iot_sensor_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_id` bigint(20) DEFAULT NULL COMMENT '关联货位ID(可为空,代表区域)',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '温度',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度',
  `is_alarm` tinyint(4) DEFAULT '0' COMMENT '是否报警: 0-正常, 1-异常',
  `record_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='IoT传感器数据记录';

-- 3.3 机器人任务表
DROP TABLE IF EXISTS `wms_robot_task`;
CREATE TABLE `wms_robot_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_no` varchar(50) NOT NULL COMMENT '任务编号',
  `task_type` varchar(20) NOT NULL COMMENT '类型: INBOUND(上架), OUTBOUND(拣选)',
  `robot_id` bigint(20) DEFAULT NULL COMMENT '执行机器人ID',
  `source_doc_id` bigint(20) NOT NULL COMMENT '关联单据ID(入库单/出库单ID)',
  `target_location_id` bigint(20) NOT NULL COMMENT '目标货位ID',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态: PENDING(待分配), ASSIGNED(已分配), IN_PROGRESS(执行中), COMPLETED(完成)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='机器人任务调度表';


-- ==========================================
-- 订单与出库模块 
-- ==========================================

-- 4.1 销售订单
DROP TABLE IF EXISTS `oms_sales_order`;
CREATE TABLE `oms_sales_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '销售订单号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `status` int(4) DEFAULT '0' COMMENT '状态: 0-已创建, 1-已锁库, 2-拣选中, 3-已发货, 4-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_so_no` (`order_no`)
) ENGINE=InnoDB COMMENT='销售订单表';

-- 4.2 出库单明细
DROP TABLE IF EXISTS `wms_outbound_note`;
CREATE TABLE `wms_outbound_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '关联销售订单号',
  `goods_id` bigint(20) NOT NULL COMMENT '货物ID',
  `qty` int(11) NOT NULL COMMENT '应发数量',
  `pick_status` tinyint(4) DEFAULT '0' COMMENT '拣选状态: 0-待拣选, 1-已拣选',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='出库单明细';


-- ==========================================
-- 物流模块
-- ==========================================

-- 5.1 运输单
DROP TABLE IF EXISTS `tms_transport_order`;
CREATE TABLE `tms_transport_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tracking_no` varchar(50) NOT NULL COMMENT '物流运单号',
  `order_no` varchar(50) NOT NULL COMMENT '关联销售订单号',
  `logistics_company` varchar(50) DEFAULT '顺丰速运' COMMENT '物流公司',
  `status` varchar(20) DEFAULT 'TRANSIT' COMMENT '状态: TRANSIT(运输中), DELIVERED(已签收)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='物流运单表';

-- 5.2 物流轨迹 (tms_transport_node)
DROP TABLE IF EXISTS `tms_transport_node`;
CREATE TABLE `tms_transport_node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tracking_no` varchar(50) NOT NULL COMMENT '关联运单号',
  `node_name` varchar(100) NOT NULL COMMENT '节点名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `arrival_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='物流轨迹节点表';


-- ==========================================
-- 初始化数据
-- ==========================================

-- ==========================================
-- 全面初始化数据脚本 (Enhanced Data)
-- ==========================================

USE `warehouse`;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------
-- 1. 用户数据
-- ------------------------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role`, `phone`) VALUES 
(1, 'admin', '123456', '系统管理员', 'ADMIN', '13800138000'),
(2, 'operator', '123456', '库管员李四', 'OPERATOR', '13900139000'),
(3, 'purchaser', '123456', '采购员王五', 'PURCHASER', '13700137000'),
(4, 'client_jd', '123456', '京东自营', 'CLIENT', '010-88888888'),
(5, 'client_tm', '123456', '天猫超市', 'CLIENT', '0571-88888888');

-- ------------------------------------------
-- 2. 货物数据
-- ------------------------------------------
INSERT INTO `base_goods` (`id`, `goods_code`, `goods_name`, `spec`, `unit`, `price`, `warn_threshold`) VALUES 
(1, 'G_ELEC_001', 'iPhone 17 Pro', '256G', '台', 8999.00, 20), -- 高价值，阈值20
(2, 'G_ELEC_002', 'Dell XPS 15', 'i9/32G/1T', '台', 15999.00, 5),  -- 阈值低
(3, 'G_DAILY_001', '维达抽纸', '3层120抽*24包', '箱', 59.90, 100), -- 走量商品，阈值高
(4, 'G_COLD_001', '澳洲M5牛排', '250g/片', '片', 128.00, 50),   -- 冷链商品 (用于测试C区温湿度)
(5, 'G_IND_001', '工业螺母', 'M8标准', '包', 12.50, 500);       -- 工业品

-- ------------------------------------------
-- 3. 货位数据 (分区域：A区常温，C区冷链)
-- ------------------------------------------
-- A区：电子产品区 (1-10)
INSERT INTO `base_location` (`id`, `location_code`, `area_zone`, `status`) VALUES 
(1, 'A-01-01', 'A区(电子)', 1), (2, 'A-01-02', 'A区(电子)', 1), 
(3, 'A-01-03', 'A区(电子)', 0), (4, 'A-01-04', 'A区(电子)', 0),
(5, 'A-01-05', 'A区(电子)', 0);

-- B区：日用品堆放区 (11-15)
INSERT INTO `base_location` (`id`, `location_code`, `area_zone`, `status`) VALUES 
(11, 'B-01-01', 'B区(日用)', 1), (12, 'B-01-02', 'B区(日用)', 0),
(13, 'B-01-03', 'B区(日用)', 0);

-- C区：冷链区 (21-25)
INSERT INTO `base_location` (`id`, `location_code`, `area_zone`, `status`) VALUES 
(21, 'C-01-01', 'C区(冷库)', 1), (22, 'C-01-02', 'C区(冷库)', 0);

-- ------------------------------------------
-- 4. 机器人数据
-- ------------------------------------------
INSERT INTO `base_robot` (`id`, `robot_code`, `robot_name`, `status`, `work_mode`, `battery`) VALUES 
(1, 'AGV-001', 'YK1号', 'IDLE', 'AUTO', 95),  -- 空闲，自动模式
(2, 'AGV-002', 'YK2号', 'BUSY', 'AUTO', 45),  -- 忙碌，正在干活
(3, 'AGV-003', '堆高车R1', 'CHARGING', 'MANUAL', 12); -- 充电中

-- ------------------------------------------
-- 5. 库存数据
-- ------------------------------------------
INSERT INTO `wms_inventory` (`goods_id`, `location_id`, `total_qty`, `locked_qty`) VALUES 
-- 场景1：iPhone充足 (A区)
(1, 1, 100, 0), 
-- 场景2：Dell电脑库存不足 
(2, 2, 3, 0),   
-- 场景3：纸巾部分被锁定 (有人下单)
(3, 11, 200, 50), 
-- 场景4：牛排在冷库 (C区)
(4, 21, 500, 0);

-- ------------------------------------------
-- 6. 传感器模拟数据
-- ------------------------------------------
INSERT INTO `iot_sensor_data` (`location_id`, `temperature`, `humidity`, `is_alarm`) VALUES 
(1, 24.5, 45.0, 0),  -- A区正常
(11, 22.0, 50.0, 0), -- B区正常
(21, -5.0, 30.0, 1); -- C区异常

-- ------------------------------------------
-- 7. 采购流程预埋
-- ------------------------------------------
-- 一张已经完成的采购单 (历史数据)
INSERT INTO `wms_purchase_order` (`id`, `po_no`, `supplier_name`, `status`) VALUES 
(1, 'PO20231001001', '苹果电脑贸易(上海)有限公司', 2); -- 已完成

-- 一张待入库的采购单
INSERT INTO `wms_purchase_order` (`id`, `po_no`, `supplier_name`, `status`) VALUES 
(2, 'PO20231027008', '宝洁日化供应商', 0); -- 待入库

-- 对应的待入库明细 (入库维达纸巾)
INSERT INTO `wms_inbound_note` (`po_no`, `goods_id`, `plan_qty`, `actual_qty`, `qc_status`, `putaway_status`) VALUES 
('PO20231027008', 3, 1000, 0, 0, 0);

-- ------------------------------------------
-- 8. 销售与出库流程预埋 (待发货 + 已发货)
-- ------------------------------------------
-- 场景A：待发货订单 
INSERT INTO `oms_sales_order` (`id`, `order_no`, `customer_id`, `receiver_name`, `receiver_address`, `status`) VALUES 
(1, 'SO_WAIT_001', 4, '张三丰', '湖北省武当山真武大殿', 1); -- 1=已锁库/待拣选

INSERT INTO `wms_outbound_note` (`order_no`, `goods_id`, `qty`, `pick_status`) VALUES 
('SO_WAIT_001', 1, 2, 0); -- 买2台iPhone

-- 场景B：已发货订单
INSERT INTO `oms_sales_order` (`id`, `order_no`, `customer_id`, `receiver_name`, `receiver_address`, `status`) VALUES 
(2, 'SO_SHIPPED_002', 5, '李逍遥', '浙江省杭州市余杭仙剑客栈', 3); -- 3=已发货

-- ------------------------------------------
-- 9. 物流数据
-- ------------------------------------------
INSERT INTO `tms_transport_order` (`id`, `tracking_no`, `order_no`, `logistics_company`, `status`) VALUES 
(1, 'SF1234567890', 'SO_SHIPPED_002', '顺丰速运', 'TRANSIT');

-- 物流轨迹
INSERT INTO `tms_transport_node` (`tracking_no`, `node_name`, `description`, `arrival_time`) VALUES 
('SF1234567890', '杭州集散中心', '快递员已收件', DATE_SUB(NOW(), INTERVAL 24 HOUR)),
('SF1234567890', '天津分拨中心', '快件已到达', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('SF1234567890', '河北工业大学', '快件正发往顺义站点', DATE_SUB(NOW(), INTERVAL 2 HOUR));

SET FOREIGN_KEY_CHECKS = 1;