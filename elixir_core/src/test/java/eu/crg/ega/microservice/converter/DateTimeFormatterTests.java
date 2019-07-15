package eu.crg.ega.microservice.converter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DateTimeFormatterTests {

  private static final DateTimeFormatter FORMAT_SYSLOG_TIMESTAMP = DateTimeFormat
      .forPattern("yyyy MMM dd HH:mm:ss").withLocale(Locale.ENGLISH);

  @Test
  public void testDateTime() {

    String timestampToParse = "2014 feb 19 14:12:23";
    DateTime timestamp = FORMAT_SYSLOG_TIMESTAMP.parseDateTime(timestampToParse);

    assertEquals(2, timestamp.getMonthOfYear());
    assertEquals(19, timestamp.getDayOfMonth());
  }
}
