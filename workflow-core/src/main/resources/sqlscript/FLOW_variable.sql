CREATE TABLE `FLOW_variable` (
  `id` varchar(36) NOT NULL DEFAULT uuid(),
  `name` varchar(128) NOT NULL,
  `class_type` varchar(512) NOT NULL,
  `variable_content` varchar(512) NOT NULL,
  `type` int(11) NOT NULL COMMENT '0:全局变量1：子任务变量2：回滚变量（全局）3：回滚变量（临时）',
  `process_id` varchar(36) NOT NULL,
  `create_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  `update_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='工作流_变量集合';
