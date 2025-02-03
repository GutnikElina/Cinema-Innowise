package org.cinema.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.cinema.repository")
public class JpaConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikariConfig.addDataSourceProperty("url", "jdbc:mysql://db:3306/cinema_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Moscow");
        hikariConfig.addDataSourceProperty("user", "cinema_user");
        hikariConfig.addDataSourceProperty("password", "cinema_password");

//        hikariConfig.addDataSourceProperty("url", "jdbc:mysql://127.0.0.1:3306/cinema_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Moscow");
//        hikariConfig.addDataSourceProperty("user", "root");
//        hikariConfig.addDataSourceProperty("password", "password");

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setLeakDetectionThreshold(2000);

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("org.cinema.model");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.current_session_context_class", "thread");
        jpaProperties.put("hibernate.jdbc.time_zone", "Europe/Moscow");
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");

        factoryBean.setJpaProperties(jpaProperties);
        factoryBean.setEntityManagerFactoryInterface(jakarta.persistence.EntityManagerFactory.class); //почему без
        // этого конфликт между EntityManagerFactory и SessionFactory

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public static PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
