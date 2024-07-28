-- 对子任务进行编排的sql数据
-- 刷入测试模板以及模板子模板关系
set @tplId = UUID();
INSERT INTO swzhao_test.FLOW_tpl (id, tpl_name, name, type, description, version, create_time, update_time)
VALUES (@tplId, '测试模板1', 'ceshi1', 0, '测试模板111', 'v1.0', now(), now());
INSERT INTO swzhao_test.FLOW_tpl_sub_class_name (id, tpl_id, sub_class_name, sort, create_time, update_time)
VALUES (UUID(), @tplId, 'top.swzhao.substeps.Step1', 1, now(), now());
INSERT INTO swzhao_test.FLOW_tpl_sub_class_name (id, tpl_id, sub_class_name, sort, create_time, update_time)
VALUES (UUID(), @tplId, 'top.swzhao.substeps.Step2', 2, now(), now());

