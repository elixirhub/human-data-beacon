package org.ega_archive.elixircore.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class JsonUtils {
  
  private static Gson gson = new Gson(); 
  
  public static <T> Jackson2JsonMessageConverter jsonMessageConverter(Class<T> clazz,
      ObjectMapper objectMapper) {
    
    DefaultClassMapper classMapper = new DefaultClassMapper();
    classMapper.setDefaultType(clazz);

    Jackson2JsonMessageConverter jsonMessageConverter =
        new Jackson2JsonMessageConverter(objectMapper);
    jsonMessageConverter.setClassMapper(classMapper);

    return jsonMessageConverter;
  }
  
  /**
   * Converts a Json that contains a list of values into an ArrayList.<br>
   * Exemple of Json: ["one", "two", "three"]
   * 
   * @param json
   * @return
   */
  public static List<String> jsonToList(String json) {
    List<String> list = null;
    if (StringUtils.isNotBlank(json)) {
      String[] array = gson.fromJson(json, String[].class);
      list = Arrays.asList(array);
    }
    return list;
  }
  
  public static String listToJson(List<String> list) {
    String json = null;
    if (list != null && !list.isEmpty()) {
      json = gson.toJson(list);
    }
    return json;
  }
  
  /**
   * Deserializes any String as a Java object.
   * 
   * @param jsonString
   * @param clazz
   * @param objectMapper
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   */
  public static <T> T jsonToObject(String jsonString, Class<T> clazz, ObjectMapper objectMapper) throws JsonParseException,
      JsonMappingException, IOException {
    T converted = objectMapper.readValue(jsonString, clazz);
    return converted;
  }
  
  /**
   * Serializes any Java object as a String.
   * 
   * @param object
   * @param objectMapper
   * @return
   * @throws JsonProcessingException
   */
  public static <T> String objectToJson(T object, ObjectMapper objectMapper) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(object);
    return json;
  }
  
}
