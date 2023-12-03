package top.swzhao.project.workflow.core.init;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import top.swzhao.project.workflow.common.OmpFlowable;
import top.swzhao.project.workflow.common.annotation.FlowDescription;
import top.swzhao.project.workflow.common.contants.EngineConstants;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.exception.EngineException;
import top.swzhao.project.workflow.common.exception.ErrorCodes;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowDic;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;
import top.swzhao.project.workflow.common.service.DdlService;
import top.swzhao.project.workflow.common.service.FlowDicService;
import top.swzhao.project.workflow.common.service.FlowSubTplService;
import top.swzhao.project.workflow.core.utils.ExecutorPoolConfiguration;
import top.swzhao.project.workflow.core.utils.ExecutorPoolUtils;
import top.swzhao.project.workflow.core.utils.SpringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author swzhao
 * @date 2023/11/22 7:10 下午
 * @Discreption <> 1、初始化引擎：
 * ` 检测应用中的表结构是否完全， 如果不完整需要补全所有表项
 * ` 加载项目中实现了OmpFlow的实现类
 * ` 将所有实现类名作为索引对数据库表进行检索
 * ` 如果没有对应的子模板的类名则数据库 insert into on duplicate key update
 * 2、输出引擎logo
 */
@Component
@Slf4j
public class InitEngine implements CommandLineRunner {


    @Autowired
    DdlService ddlService;

    @Autowired
    FlowSubTplService flowSubTplService;

    @Autowired
    FlowDicService flowDicService;

    @Autowired
    Environment environment;

    @Autowired
    SpringUtils springUtils;


    @Override
    public void run(String... args) throws Exception {
          try {
              log.info("正在检查OMP_FLOW表结构是否完整......");
              String databaseName = getDatabaseName();
              log.info("当前database名称:{}", databaseName);
              dealTablesDDL(databaseName);
              log.info("表结构检查完成， 正在初始化子模板......");
              dealSubTpl();
              log.info("初始化子模板完成， 正在初始化OMP_FLOW线程池......");
              // 读库初始化线程池
              dealThreadPool();
              log.info("初始化OMP_FLOW线程池成功!");
              // 打印logo
              System.out.println("   _______          _________    _          ____             ______ _      ______          __\n" +
                      "  / ____\\ \\        / /___  / |  | |   /\\   / __ \\           |  ____| |    / __ \\ \\        / /\n" +
                      " | (___  \\ \\  /\\  / /   / /| |__| |  /  \\ | |  | |          | |__  | |   | |  | \\ \\  /\\  / / \n" +
                      "  \\___ \\  \\ \\/  \\/ /   / / |  __  | / /\\ \\| |  | |          |  __| | |   | |  | |\\ \\/  \\/ /  \n" +
                      "  ____) |  \\  /\\  /   / /__| |  | |/ ____ \\ |__| |  ______  | |    | |___| |__| | \\  /\\  /   \n" +
                      " |_____/    \\/  \\/   /_____|_|  |_/_/    \\_\\____/  |______| |_|    |______\\____/   \\/  \\/    \n" +
                      "                                                                                           \n" +
                      "                                                                                           ");
          }catch (EngineException e) {
              log.error("InitEngine 引擎初始化异常：{}", e.getMessage(), e);
          }catch (Exception e) {
              log.error("InitEngine 引擎初始化未知异常：{}", e.getMessage(), e);
          }
    }

    /**
     * 初始化线程池
     * 如果数据库中没有配置线程池参数，则使用默认参数，如果配置了线程池参数，则使用数据库中配置的线程池参数
     */
    private void dealThreadPool() {
        ExecutorPoolConfiguration executorPoolConfiguration = new ExecutorPoolConfiguration();
        try {
            OperResult<List<FlowDic>> operResult = flowDicService.listDicByType(FlowKvConstants.STR_VALUE_THREAD_POOL);
            if (Objects.nonNull(operResult) && operResult.success() && Objects.nonNull(operResult.getData())) {
                List<FlowDic> flowDics = operResult.getData();
                // 过滤掉不合法的并转成map
                Map<String, String> flowDicMap = flowDics.stream().filter(u -> StringUtils.isAnyBlank(u.getId(), u.getType(), u.getKey(), u.getValue())).collect(Collectors.toMap(FlowDic::getKey, FlowDic::getValue, (k1, k2) -> k1));
                if (Objects.nonNull(flowDicMap.get(FlowKvConstants.STR_VALUE_CORE_NUM))) {
                    log.info("检测到线程池存在数据库配置：core_num:{}", flowDicMap.get(FlowKvConstants.STR_VALUE_CORE_NUM));
                    executorPoolConfiguration.setSizeCore(Integer.parseInt(flowDicMap.get(FlowKvConstants.STR_VALUE_CORE_NUM)));
                }
                if (Objects.nonNull(flowDicMap.get(FlowKvConstants.STR_VALUE_MAX_NUM))) {
                    log.info("检测到线程池存在数据库配置：max_num:{}", flowDicMap.get(FlowKvConstants.STR_VALUE_MAX_NUM));
                    executorPoolConfiguration.setSizeMax(Integer.parseInt(flowDicMap.get(FlowKvConstants.STR_VALUE_MAX_NUM)));
                }
                if (Objects.nonNull(flowDicMap.get(FlowKvConstants.STR_VALUE_KEEP_ALIVE_NUM))) {
                    log.info("检测到线程池存在数据库配置：keep_alive_num:{}", flowDicMap.get(FlowKvConstants.STR_VALUE_KEEP_ALIVE_NUM));
                    executorPoolConfiguration.setKeepAliveTime(Integer.parseInt(flowDicMap.get(FlowKvConstants.STR_VALUE_KEEP_ALIVE_NUM)));
                }
                if (Objects.nonNull(flowDicMap.get(FlowKvConstants.STR_VALUE_QUEUE_LENGTH))) {
                    log.info("检测到线程池存在数据库配置：queue_length:{}", flowDicMap.get(FlowKvConstants.STR_VALUE_QUEUE_LENGTH));
                    executorPoolConfiguration.setQueue(new ArrayBlockingQueue<>(Integer.parseInt(flowDicMap.get(FlowKvConstants.STR_VALUE_QUEUE_LENGTH))));
                }
            }
            ExecutorPoolUtils.setPool(executorPoolConfiguration.build());
        }catch (Exception e) {
            log.error("初始化线程池异常：{}", e.getMessage(), e);
            throw new EngineException(ErrorCodes.ENGINE_INIT_ERROR, "初始化线程池异常");
        }
    }

    /**
     * 处理客户端程序定义的子任务，将子任务查询并入库，如果子任务的class已经存在sub_tpl中，则不再创建
     * sub_tpl表中一条记录对应一个子任务的class
     */
    private void dealSubTpl() {
        Map<String, OmpFlowable> flowBeanMap = springUtils.getBeansFromClazz(OmpFlowable.class);
        ArrayList<FlowSubTpl> flowSubTplList = new ArrayList<>();
        for (String beanName : flowBeanMap.keySet()) {
            OmpFlowable ompFlowAble = flowBeanMap.get(beanName);
            FlowSubTpl flowSubTpl = new FlowSubTpl();
            // 变量准备
            String className = ompFlowAble.getClass().getName();
            FlowDescription flowDescription = ompFlowAble.getClass().getAnnotation(FlowDescription.class);
            String description = flowDescription.description();
            int type = flowDescription.type();
            flowSubTpl.setName(beanName);
            flowSubTpl.setClassName(className);
            flowSubTpl.setDescription(description);
            flowSubTpl.setType(type);
            flowSubTplList.add(flowSubTpl);
        }
        OperResult operResult = flowSubTplService.insertOrUpdate(flowSubTplList);
        if (!operResult.success()) {
            throw new EngineException(ErrorCodes.ENGINE_INIT_ERROR, "子任务初始化写入数据库异常");
        }
    }

    private void dealTablesDDL(String databaseName) {
        try {
            for (String tableName : EngineConstants.listTableName()) {
                OperResult<Boolean> operResult = ddlService.isTableExist(tableName, databaseName);
                if (!operResult.success()) {
                    throw new EngineException(ErrorCodes.ENGINE_INIT_ERROR, "检查表是否存在操作异常");
                }
                if (!operResult.getData()) {
                    log.info("数据库 {}, 表：{}, 不存在，正在创建表......", databaseName, tableName);
                    ddlService.createTableByName(tableName);
                }
            }
        }catch (Exception e) {
            log.error("检查表结构异常", e);
            throw new EngineException(ErrorCodes.ENGINE_INIT_ERROR, "检查表结构异常");
        }
    }

    /**
     * 从url 获取database的名称
     * @return
     */
    private String getDatabaseName() {
        String url = FlowKvConstants.STR_VALUE_EMP;
        String[] jdbcUrlKeys = EngineConstants.listJDBCUrl();
        for (String urlKeys : jdbcUrlKeys) {
            url = environment.getProperty(urlKeys);
            if (StringUtils.isNotBlank(urlKeys)) {
                break;
            }
        }
        if (StringUtils.isBlank(url)) {
            throw new EngineException(ErrorCodes.ENGINE_INIT_ERROR, "jdbcUrl为空");
        }
        try {
            return url.substring(
                    url.substring(FlowKvConstants.NUM_VALUE_O, url.indexOf(FlowKvConstants.STR_VALUE_QM)).lastIndexOf(FlowKvConstants.STR_VALUE_CLASH) + FlowKvConstants.NUM_VALUE_I
                    , url.indexOf(FlowKvConstants.STR_VALUE_QM));
        }catch (Exception e) {
            log.error("jdbc切割异常", e);
            throw new EngineException(ErrorCodes.ENGINE_INIT_ERROR, "jdbc切割异常");
        }
    }


    public static void main(String[] args) {
        System.out.println("   _______          _________    _          ____             ______ _      ______          __\n" +
                "  / ____\\ \\        / /___  / |  | |   /\\   / __ \\           |  ____| |    / __ \\ \\        / /\n" +
                " | (___  \\ \\  /\\  / /   / /| |__| |  /  \\ | |  | |          | |__  | |   | |  | \\ \\  /\\  / / \n" +
                "  \\___ \\  \\ \\/  \\/ /   / / |  __  | / /\\ \\| |  | |          |  __| | |   | |  | |\\ \\/  \\/ /  \n" +
                "  ____) |  \\  /\\  /   / /__| |  | |/ ____ \\ |__| |  ______  | |    | |___| |__| | \\  /\\  /   \n" +
                " |_____/    \\/  \\/   /_____|_|  |_/_/    \\_\\____/  |______| |_|    |______\\____/   \\/  \\/    \n" +
                "                                                                                           \n" +
                "                                                                                           ");
    }



}
