create table if not exists `FLOW_dic` (
    `id` varchar(36) not null default uuid(),
    `type` varchar(64) not null,
    `key` varchar(64) not null,
    `value` varchar(512) not null,
    `en_value` varchar(512) not null,
    `create_time` varchar(64) not null default current_timestamp(),
    `update_time` varchar(64) not null default current_timestamp(),
    primary key (`id`),
    unique key `FLOW_dic_pk_type_key_value` (`type`, `key`, `value`),
    unique key `FLOW_dic_type_key` (`type`, `key`),
    unique key `FLOW_dic_type_key_en_value` (`type`, `key`, `en_value`)
) ENGINE=InnoDB default CHARSET=utf8 COMMENT='工作流_字典表';
