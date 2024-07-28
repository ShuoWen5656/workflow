package top.swzhao.project.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.swzhao.WorkFlowApplicatin;
import top.swzhao.project.workflow.common.engine.OmpFlowEngine;
import top.swzhao.project.workflow.common.exception.OmpFlowException;

/**
 * @author swzhao
 * @date 2024/7/27 10:20 上午
 * @Discreption <>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WorkFlowApplicatin.class)
@RunWith(SpringRunner.class)
@Slf4j
public class WorkFlowTest {

    @Autowired
    OmpFlowEngine ompFlowEngine;

    /**
     * 测试正向执行
     */
    @Test
    public void testWorkFlow() throws OmpFlowException, InterruptedException {
        ompFlowEngine.start("c331db12-4cb7-11ef-998f-0242ac110005", null);
        log.error("test");
        Thread.sleep(10000);
    }


    /**
     * 测试回滚
     */
    @Test
    public void testWorkFlowRollBack() {

    }


}
