package org.ega_archive.elixirbeacon;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"${custom.package.scan:org.ega_archive.custom.elixirbeacon}", "org.ega_archive.elixirbeacon", "org.ega_archive.elixircore"})
@EnableAutoConfiguration(exclude = {
    HibernateJpaAutoConfiguration.class,
    RabbitAutoConfiguration.class
})
public class Application {

  @Value("${service.name}")
  private String serviceName;

  @PostConstruct
  public void initServiceName() {
    System.setProperty("service.name", serviceName);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
