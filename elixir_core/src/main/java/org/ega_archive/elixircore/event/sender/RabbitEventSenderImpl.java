package org.ega_archive.elixircore.event.sender;

import org.ega_archive.elixircore.dto.message.ServiceMessage;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Log4j
// This will probably call or be called in a service in latter versions
public class RabbitEventSenderImpl implements RabbitEventSender {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public void prepareMessageAndSend(String exchange, String queueName, ServiceMessage event) {
    prepareMessageAndSend(exchange, queueName, event, null);
  }

  @Override
  public void prepareMessageAndSend(String exchange, String queueName, ServiceMessage event, Integer priority) {
    MessageProperties messageProperties = fillProperties(priority);

    MessageConverter messageConverter = rabbitTemplate.getMessageConverter();
    Message message = messageConverter.toMessage(event, messageProperties);
    log.trace("Message to send: " + new String(message.getBody()));

    send(exchange, queueName, message);
  }

  private MessageProperties fillProperties(Integer priority) {
    MessageProperties messageProperties = new MessageProperties();

    messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
    // Message is persisted to prevent messages losses due system failure
    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
    if (priority != null) {
      messageProperties.setPriority(priority);
    }
    return messageProperties;
  }

  @Override
  public Void send(String exchange, String routingkey, Message message) {
    rabbitTemplate.convertAndSend(exchange, routingkey, message);
    return null;
  }

}
