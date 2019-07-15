package org.ega_archive.elixircore.event.sender;

import org.ega_archive.elixircore.dto.message.ServiceMessage;
import org.ega_archive.elixircore.interfaces.EventsSender;
import org.springframework.amqp.core.Message;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_SYSTEM')")
public interface RabbitEventSender extends EventsSender<Void, String, Message> {

  /**
   * Set message content and properties and sends the message. Setting properties include: content
   * type, delivery mode and time stamp.
   *
   * @param event : message content.
   */
  public void prepareMessageAndSend(String exchange, String routingkey, ServiceMessage event);

  public void prepareMessageAndSend(String exchange, String routingkey, ServiceMessage event,
      Integer priority);
}
