package org.ega_archive.elixircore.config;

import org.eclipse.jetty.server.Server;
import org.ega_archive.elixircore.jetty.JettyCustomErrorHandler;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class Jetty {

  @Bean
  public EmbeddedServletContainerFactory embeddedServletContainerFactory() throws Exception {
    return new JettyEmbeddedServletContainerFactory() {
      @Override
      protected JettyEmbeddedServletContainer getJettyEmbeddedServletContainer(Server server) {
        log.info("Registering custom error Handler for Jetty");
        server.addBean(new JettyCustomErrorHandler());
        return super.getJettyEmbeddedServletContainer(server);
      }
    };
  }
}
