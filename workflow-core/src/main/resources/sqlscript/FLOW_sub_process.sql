CREATE TABLE `FLOW_sub_process` (
  `id` varchar(36) NOT NULL DEFAULT uuid(),
  `name` varchar(128) NOT NULL DEFAULT '',
  `sub_tpl_id` varchar(64) NOT NULL,
  `state` varchar(11) NOT NULL DEFAULT '0' COMMENT '0:未开始1：执行中2：已完成3：失败4：回滚中5：已回滚',
  `error_code` int(11) NOT NULL DEFAULT -1 COMMENT '错误代码',
  `error_type` int(11) NOT NULL DEFAULT 0 COMMENT '-1：无异常 0：业务异常 1：引擎异常 2：未知异常',
  `error_stack` text NOT NULL DEFAULT '' COMMENT '错误堆栈信息',
  `process_id` varchar(36) NOT NULL,
  `sort` int(11) NOT NULL COMMENT '当前子流程在整个流程中的排序',
  `method_name` varchar(64) NOT NULL DEFAULT '',
  `create_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  `update_time` varchar(64) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='工作流_子流程';
