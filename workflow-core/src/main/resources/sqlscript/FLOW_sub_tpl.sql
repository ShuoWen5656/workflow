CREATE TABLE `FLOW_sub_tpl` (
  `id` varchar(36) NOT NULL DEFAULT uuid(),
  `name` varchar(128) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL DEFAULT 0,
  `class_name` varchar(64) NOT NULL COMMENT '模板对应的className',
  `description` text DEFAULT '',
  `create_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  `update_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='工作流_子任务模板';
