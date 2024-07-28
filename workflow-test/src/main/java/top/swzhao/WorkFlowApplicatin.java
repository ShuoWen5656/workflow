package top.swzhao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author swzhao
 * @date 2024/7/27 10:29 上午
 * @Discreption <>
 */
@SpringBootApplication
@Slf4j
public class WorkFlowApplicatin {

    public static void main(String[] args) {
        try {
            SpringApplication.run(WorkFlowApplicatin.class, args);
        }catch (Exception e) {
            log.error("", e);
        }
    }



}
