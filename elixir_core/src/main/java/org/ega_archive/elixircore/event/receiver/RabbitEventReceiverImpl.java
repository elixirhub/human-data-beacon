package org.ega_archive.elixircore.event.receiver;

import org.ega_archive.elixircore.dto.message.ServiceMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Log4j
//The name of this class is Wrong, it Should be RabbitMessageReceiverImpl
public class RabbitEventReceiverImpl implements RabbitEventReceiver {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public ServiceMessage receive(Message message) {

    ServiceMessage event = convertFromMessage(message);

    return event;
  }

  /**
   * Converts content from JSON format to DTO class.
   */
  private ServiceMessage convertFromMessage(Message message) {
    log.trace("Message to read: " + new String(message.getBody()));

    MessageConverter messageConverter = rabbitTemplate.getMessageConverter();
    ServiceMessage event = (ServiceMessage) messageConverter.fromMessage(message);
    return event;
  }

}
