package org.ega_archive.elixircore.converter;

import org.apache.commons.beanutils.Converter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Timestamp;
import java.util.Date;

/*https://gist.github.com/DheerendraRathor/ebce6b5bea4f1ede9d61 */
public class TimestampToDatetimeConverter implements Converter {

  @Override
  public <T> T convert(Class<T> type, Object value) {
    if (value instanceof Timestamp) {
      Long milli = ((Timestamp) value).getTime();
      Date normalDate = new Date(milli);
      DateTime dt = new DateTime(normalDate, DateTimeZone.UTC);
      return (T) dt;
      //return (T) new DateTime(value);
    } else if(value instanceof DateTime) {
      return (T) value;
    }
    return null;
  }
}
