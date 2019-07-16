package org.ega_archive.elixirbeacon.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.ega_archive.elixircore.repository.CustomQuerydslJpaRepositoryImpl;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "org.ega_archive.elixirbeacon.repository.elixirbeacon",
    entityManagerFactoryRef = "elixirbeaconEntityManagerFactory",
    transactionManagerRef = "elixirbeaconTransactionManager",
    repositoryBaseClass = CustomQuerydslJpaRepositoryImpl.class)
@EnableTransactionManagement
public class ElixirBeaconDBConfig {

  @Bean
  @ConfigurationProperties(prefix = "datasource.elixirbeacon")
  public DataSource elixirbeaconDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public EntityManagerFactory elixirbeaconEntityManagerFactory() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(false);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setPackagesToScan("org.ega_archive.elixirbeacon.model.elixirbeacon");
    factory.setDataSource(elixirbeaconDataSource());
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPersistenceUnitName("elixirbeacon");
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public PlatformTransactionManager elixirbeaconTransactionManager() {

    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager
        .setEntityManagerFactory(elixirbeaconEntityManagerFactory());

    return transactionManager;
  }

}
