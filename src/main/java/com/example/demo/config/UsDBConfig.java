package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by nikolay.odintsov on 14.05.18.
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryUs",
        transactionManagerRef = "transactionManagerUs",
        basePackages = {"com.example.demo.repository.us"}
)
@Slf4j
public class UsDBConfig {

    @Autowired
    private EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Primary
    @Bean(name = "dataSourceUS")
    @ConfigurationProperties(prefix = "datasources.us")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactoryUs")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSourceUS") DataSource dataSource) {

        return entityManagerFactoryBuilder.build(dataSource, new String[]{"com.example.demo.entity"});
    }

    @Primary
    @Bean(name = "transactionManagerUs")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryUs") EntityManagerFactory
                                                                 entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        log.info("Created PlatformTransactionManager with entityManagerFactory(US)");

        return transactionManager;
    }
}
