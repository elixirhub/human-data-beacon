package org.ega_archive.elixircore.dto.message;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageHeader {

  private Format format;

  private Producer producer;

  private String messageId;

  private String conversationId;

  // default value = 1
  private Integer idInSequence = 1;

  private DateTime timestamp = DateTime.now().withZone(DateTimeZone.UTC);

  private Long millisecToExpiration;

  // For request & reply messages
  private String replyTo;

}
