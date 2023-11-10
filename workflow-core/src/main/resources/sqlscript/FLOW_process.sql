CREATE TABLE `FLOW_process` (
  `id` varchar(36) NOT NULL DEFAULT uuid(),
  `name` varchar(128) NOT NULL DEFAULT '',
  `tpl_id` varchar(36) NOT NULL,
  `state` int(11) NOT NULL DEFAULT 0 COMMENT '0:未开始1：运行中2：已完成 3：失败 4：回滚中 5：已回滚\n',
  `cur_sub_id` varchar(64) NOT NULL DEFAULT '' COMMENT '当前流程正在执行的子任务id',
  `create_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  `update_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='工作流_流程表';