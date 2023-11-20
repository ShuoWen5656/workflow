package top.swzhao.project.workflow.core.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.sun.tools.javac.comp.Flow;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.core.utils.AutoFillMetaObjectHandler;

import javax.sql.DataSource;

/**
 * @author swzhao
 * @date 2023/11/19 3:49 下午
 * @Discreption <>
 */
@Configuration
@MapperScan(basePackages = {FlowKvConstants.STR_KEY_BASE_PACKAGE}, sqlSessionFactoryRef = FlowKvConstants.STR_KEY_SESSION_FACTORY_FLOW)
public class DataSource2Config {


    @Autowired
    AutoFillMetaObjectHandler autoFillMetaObjectHandler;


    @Bean(name = FlowKvConstants.STR_KEY_DATASOURCE_FLOW)
    @ConfigurationProperties(prefix = FlowKvConstants.STR_KEY_DATASOURCE_PREFIX)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = FlowKvConstants.STR_KEY_SESSION_FACTORY_FLOW)
    public SqlSessionFactory getSessionFactory(@Qualifier(FlowKvConstants.STR_KEY_DATASOURCE_FLOW) DataSource dataSource) throws Exception {
        // 使用mybatisplus的sqlSessionFactory
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        // 默认自动填充不会注入到config
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(autoFillMetaObjectHandler);
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);
        mybatisSqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResource(FlowKvConstants.STR_KEY_MAPPER_XML_LOCATION));
        return mybatisSqlSessionFactoryBean.getObject();
    }

    @Bean(name = FlowKvConstants.STR_KEY_TRANSACTION_MANAGER_FLOW)
    public DataSourceTransactionManager transactionManager(@Qualifier(FlowKvConstants.STR_KEY_DATASOURCE_FLOW) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = FlowKvConstants.STR_KEY_SESSION_FACTORY_FLOW)
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(FlowKvConstants.STR_KEY_SESSION_FACTORY_FLOW) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
