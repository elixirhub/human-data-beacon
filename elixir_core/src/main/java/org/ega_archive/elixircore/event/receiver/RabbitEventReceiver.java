package org.ega_archive.elixircore.event.receiver;


import org.ega_archive.elixircore.dto.message.ServiceMessage;
import org.ega_archive.elixircore.interfaces.EventsReceiver;
import org.springframework.amqp.core.Message;

//The name of this interface is Wrong, it Should be RabbitMessageReceiver
public interface RabbitEventReceiver extends EventsReceiver<ServiceMessage, Message> {

}
