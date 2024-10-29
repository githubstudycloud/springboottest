package com.study.scheduler;

import com.study.scheduler.listener.SchedulerJobListener;
import com.zaxxer.hikari.HikariDataSource;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class SchedulerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriver;

    private final ApplicationContext applicationContext;

    public SchedulerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @QuartzDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource quartzDataSource() {
        logger.info("Configuring Quartz DataSource...");
        logger.info("DataSource URL: {}", datasourceUrl);
        logger.info("DataSource Driver: {}", datasourceDriver);
        logger.info("DataSource Username: {}", datasourceUsername);

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create()
                .url(datasourceUrl)
                .username(datasourceUsername)
                .password(datasourcePassword)
                .driverClassName(datasourceDriver);

        HikariDataSource dataSource = (HikariDataSource) dataSourceBuilder.type(HikariDataSource.class).build();

        // 设置连接池配置
        dataSource.setPoolName("QuartzHikariCP");
        dataSource.setMinimumIdle(5);
        dataSource.setMaximumPoolSize(15);
        dataSource.setIdleTimeout(30000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setConnectionTimeout(30000);
        dataSource.setConnectionTestQuery("SELECT 1");

        logger.info("Quartz DataSource configured successfully");
        return dataSource;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource quartzDataSource) throws IOException {
        logger.info("Initializing Quartz SchedulerFactoryBean...");

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        try {
            // 设置数据源
            factory.setDataSource(quartzDataSource);
            logger.info("Set Quartz DataSource successfully");

            // 设置Quartz属性
            factory.setQuartzProperties(quartzProperties());
            logger.info("Set Quartz Properties successfully");

            // 设置JobFactory
            CustomJobFactory jobFactory = new CustomJobFactory();
            jobFactory.setApplicationContext(applicationContext);
            factory.setJobFactory(jobFactory);
            logger.info("Set JobFactory successfully");

            // 其他配置
            factory.setOverwriteExistingJobs(true);
            factory.setStartupDelay(10); // 延迟10秒启动
            factory.setApplicationContextSchedulerContextKey("applicationContext");

            logger.info("SchedulerFactoryBean initialized successfully");
            return factory;

        } catch (Exception e) {
            logger.error("Failed to initialize SchedulerFactoryBean", e);
            throw new RuntimeException("Failed to initialize SchedulerFactoryBean", e);
        }
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        logger.info("Loading Quartz properties...");
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        Properties properties = propertiesFactoryBean.getObject();

        if (properties != null) {
            properties.put("org.quartz.scheduler.instanceId", "AUTO");
            properties.put("org.quartz.scheduler.instanceName", "ClusterScheduler");
            properties.put("org.quartz.jobStore.isClustered", "true");
            properties.put("org.quartz.jobStore.clusterCheckinInterval", "10000");
            properties.put("org.quartz.jobStore.useProperties", "false");

            logger.info("Quartz Properties loaded successfully");
            logger.info("Instance Name: {}", properties.getProperty("org.quartz.scheduler.instanceName"));
            logger.info("Instance ID: {}", properties.getProperty("org.quartz.scheduler.instanceId"));
            logger.info("Is Clustered: {}", properties.getProperty("org.quartz.jobStore.isClustered"));
        }

        return properties;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, SchedulerJobListener jobListener)
            throws SchedulerException {
        logger.info("Creating Quartz Scheduler...");
        Scheduler scheduler = factory.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        logger.info("Quartz Scheduler created successfully");
        return scheduler;
    }
}

class CustomJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CustomJobFactory.class);
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.beanFactory = context.getAutowireCapableBeanFactory();
        logger.info("CustomJobFactory initialized with ApplicationContext");
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        logger.info("Created and autowired job instance: {}", job.getClass().getSimpleName());
        return job;
    }
}