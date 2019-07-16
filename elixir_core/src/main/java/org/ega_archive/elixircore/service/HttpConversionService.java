package org.ega_archive.elixircore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

/**
 * This service serializes an objects to the content type requested (for example json or xml) and
 * returns it in the http request. To do so it uses an httpMessageConverter
 */


@Service
@Log4j
public class HttpConversionService {

  @Autowired
  RequestMappingHandlerAdapter requestMappingHandlerAdapter;
  @Autowired
  private ContentNegotiationManager contentNegotiationManager;

  public void convert(Object o, HttpServletRequest request, HttpServletResponse response) {

    MediaType mediaType = null;

    try {
      List<MediaType>
          types = contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
      // resolve to a single type
      String type = types == null || types.size() == 0 ? "" : types.get(0).toString();
      if (type != null) {
        mediaType = MediaType.parseMediaType(type);
      }

      for (HttpMessageConverter httpMessageConverter : requestMappingHandlerAdapter
          .getMessageConverters()) {
        if (httpMessageConverter.canWrite(o.getClass(), mediaType)) {
          try {
            httpMessageConverter.write(o, mediaType, new ServletServerHttpResponse(response));
            break;
          } catch (IOException e) {
            log.debug("Could not convert object to mediaType", e);
          }
        }
      }
    } catch (HttpMediaTypeNotAcceptableException e) {
      log.debug("Could not parse Mediatype from http request", e);
    }
  }

}
