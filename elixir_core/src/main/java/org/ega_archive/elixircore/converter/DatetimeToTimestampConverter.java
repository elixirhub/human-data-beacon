package org.ega_archive.elixircore.converter;

import org.apache.commons.beanutils.Converter;
import org.joda.time.DateTime;

import java.sql.Timestamp;

public class DatetimeToTimestampConverter implements Converter {

  @Override
  @SuppressWarnings("unchecked")
  public <T> T convert(Class<T> type, Object value) {
    if (value instanceof DateTime) {
      return (T) new Timestamp(((DateTime) value).getMillis());
    }
    return null;
  }
}
