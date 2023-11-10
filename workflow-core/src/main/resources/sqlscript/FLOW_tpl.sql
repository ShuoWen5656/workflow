CREATE TABLE `FLOW_tpl` (
  `id` varchar(36) NOT NULL DEFAULT uuid(),
  `tpl_name` varchar(128) NOT NULL DEFAULT '' COMMENT '全局唯一的模板编码',
  `name` varchar(128) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL DEFAULT 0,
  `description` text DEFAULT '',
  `version` varchar(64) NOT NULL,
  `create_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  `update_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='工作流_模板表';
