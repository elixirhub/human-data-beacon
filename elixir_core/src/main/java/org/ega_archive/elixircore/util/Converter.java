package org.ega_archive.elixircore.util;

import java.util.ArrayList;
import java.util.List;

public class Converter {
  
  public static <S, T> T convert(Class<T> targetClass, S sourceObject, String... excludeProperties) {
    T targetObject = null;
    if (sourceObject == null) {
      return null;
    }

    try {
      targetObject = targetClass.newInstance();
      CustomBeanUtils.copyNotNullProperties(sourceObject, targetObject, excludeProperties);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return targetObject;
  }

  public static <S, T> List<T> convertList(Class<T> targetClass, List<S> sourceListObject) {
    List<T> targetList = new ArrayList<T>();
    for (S sourceObject : sourceListObject) {
      if (sourceObject != null) {
        targetList.add(convert(targetClass, sourceObject));
      }
    }
    return targetList;
  }

}