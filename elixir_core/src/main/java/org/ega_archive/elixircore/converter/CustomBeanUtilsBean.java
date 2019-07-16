package org.ega_archive.elixircore.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * Do not use this class directly. Use <code>CustomBeanUtils</code> or <code>Converter</code>
 * classes.<br>
 * But if you insist to use it, remember to initialize <code>exludeProperties</code> field with your
 * desired value (this class is singleton so this field can store the value used in its last call).
 *
 */
public class CustomBeanUtilsBean extends BeanUtilsBean {
  
  public static List<String> exludeProperties;
  
  public static boolean ignoreNullProperties = true;

  /**
   * Ignores null properties.
   */
  @Override
  public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException,
      InvocationTargetException {

    if (CustomBeanUtilsBean.exludeProperties != null
        && CustomBeanUtilsBean.exludeProperties.contains(name)) {
      return;
    }
    if (ignoreNullProperties && value == null) {
      return;
    }
    super.copyProperty(bean, name, value);
  }

}
