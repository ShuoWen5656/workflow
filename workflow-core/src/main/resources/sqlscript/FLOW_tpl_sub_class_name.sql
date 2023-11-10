CREATE TABLE `FLOW_tpl_sub_class_name` (
  `id` varchar(36) NOT NULL DEFAULT uuid(),
  `tpl_id` varchar(36) NOT NULL DEFAULT '',
  `sub_class_name` varchar(128) NOT NULL,
  `sort` int(11) NOT NULL,
  `create_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  `update_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='工作流_模板_子模板关系表';
