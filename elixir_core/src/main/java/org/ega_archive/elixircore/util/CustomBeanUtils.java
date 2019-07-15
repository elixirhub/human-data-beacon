package org.ega_archive.elixircore.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.beanutils.BeanUtils;
import org.ega_archive.elixircore.converter.CustomBeanUtilsBean;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Log4j
public class CustomBeanUtils {

  public static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

    Set<String> emptyNames = new HashSet<String>();
    for (java.beans.PropertyDescriptor pd : pds) {
      Object srcValue = src.getPropertyValue(pd.getName());
      if (srcValue == null) {
        emptyNames.add(pd.getName());
      }
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  public static String[] getNullPropertyNamesExcept(Object source, String... exceptions) {
    String[] nullProperties = getNullPropertyNames(source);
    List<String> list = new ArrayList<String>(Arrays.asList(nullProperties));
    list.removeAll(Arrays.asList(exceptions));
    return list.toArray(nullProperties);
  }

  /**
   * Copy all NOT null properties from one object to another.
   * 
   * @param source
   * @param target
   * @param excludeProperties
   */
  public static void copyNotNullProperties(Object source, Object target,
      String... excludeProperties) {

    CustomBeanUtilsBean.ignoreNullProperties = true; // Ignore null properties
    try {
      CustomBeanUtilsBean.exludeProperties = new ArrayList<String>();
      if (excludeProperties != null) {
        CustomBeanUtilsBean.exludeProperties = Arrays.asList(excludeProperties);
      }
      BeanUtils.copyProperties(target, source);
    } catch (InvocationTargetException | IllegalAccessException e) {
      log.error("Exception copying properties", e);
    }
  }

  public static boolean hasNullProperties(Object source) {
    return getNullPropertyNames(source).length != 0;
  }

  /**
   * Copy all properties from one object to another.
   * 
   * @param source
   * @param target
   * @param excludeProperties
   */
  public static void copyProperties(Object source, Object target, String... excludeProperties) {

    CustomBeanUtilsBean.ignoreNullProperties = false;// Don't ignore null properties
    try {
      CustomBeanUtilsBean.exludeProperties = new ArrayList<String>();
      if (excludeProperties != null) {
        CustomBeanUtilsBean.exludeProperties = Arrays.asList(excludeProperties);
      }
      BeanUtils.copyProperties(target, source);
    } catch (InvocationTargetException | IllegalAccessException e) {
      log.error("Exception copying properties", e);
    }
  }
  
}
