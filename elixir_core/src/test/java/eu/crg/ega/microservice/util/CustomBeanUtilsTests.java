package eu.crg.ega.microservice.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.ega_archive.elixircore.converter.CustomBeanUtilsBean;
import org.ega_archive.elixircore.converter.DatetimeToTimestampConverter;
import org.ega_archive.elixircore.converter.TimestampToDatetimeConverter;
import org.ega_archive.elixircore.util.CustomBeanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CustomBeanUtilsTests {

  @Before
  public void setup() {
    BeanUtilsBean.setInstance(new CustomBeanUtilsBean());
    //Register typeConverter
    TimestampToDatetimeConverter timestampToDatetimeConverter = new TimestampToDatetimeConverter();
    ConvertUtils.register(timestampToDatetimeConverter, DateTime.class);
    DatetimeToTimestampConverter datetimeToTimestampConverter = new DatetimeToTimestampConverter();
    ConvertUtils.register(datetimeToTimestampConverter, Timestamp.class);
  }

  @Test
  public void testGetNullPropertyNames() {
    Object1 object1 = new Object1();
    String[] res = CustomBeanUtils.getNullPropertyNames(object1);
    assertEquals(res.length, 1);
  }

  @Test
  public void testCopyNotNullProperties() {
    Object1 object1 = new Object1();
    Object2 object2 = new Object2();
    CustomBeanUtils.copyNotNullProperties(object1, object2);
    assertEquals(object1.getA(), "a1");
    assertEquals(object1.getA(), object2.getA());
  }

  @Test
  public void testCopyNotNullProperties2() {
    Object1 object1 = new Object1();
    Object3 object3 = new Object3();
    CustomBeanUtils.copyNotNullProperties(object3, object1);
    assertEquals(object1.getA(), "a3");
  }

  @Test
  public void testCopyNotNullProperties3() {
    Object4 object4 = new Object4();
    Object5 object5 = new Object5();
    object5.setB(new Timestamp(1421407128));
    CustomBeanUtils.copyNotNullProperties(object5, object4);
    assertEquals(object4.getB().getMillis(), 1421407128);
  }

  @Test
  public void testCopyNotNullProperties4() {
    Object4 object4 = new Object4();
    Object5 object5 = new Object5();
    object4.setB(new DateTime(2005, 3, 26, 12, 0, 0, DateTimeZone.UTC));
    CustomBeanUtils.copyNotNullProperties(object4, object5);
    assertEquals(1111838400, object5.getB().getTime()/1000);
  }
  
  @Test
  public void copyAllProperties() {
    Object1 object1 = new Object1();
    Object2 object2 = new Object2("value1","value2");
    CustomBeanUtils.copyProperties(object1, object2);
    
    assertThat(object2.getA(), equalTo(object1.getA()));
    assertThat(object2.getB(), equalTo(object1.getB()));
  }

  @Getter
  @Setter
  public class Object1 {

    String a = "a1";
    String b = null;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public class Object2 {

    String a = null;
    String b = null;
  }

  @Getter
  @Setter
  public class Object3 {

    String a = "a3";
    String b = null;
    String c = null;
  }

  @Getter
  @Setter
  public class Object4 {

    String a = "a4";
    DateTime b = null;
    String c = null;
  }

  @Getter
  @Setter
  public class Object5 {

    String a = "a5";
    Timestamp b = null;
    String c = null;
  }


}