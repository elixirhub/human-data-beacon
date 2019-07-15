package org.ega_archive.elixircore.controller.exception;

import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.exception.CookieNotFoundException;
import org.ega_archive.elixircore.exception.NotFoundException;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.ega_archive.elixircore.exception.PreConditionFailed;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.exception.ServerDownException;
import org.ega_archive.elixircore.exception.ServiceUnavailableException;
import org.ega_archive.elixircore.exception.SessionAlreadyClosed;
import org.ega_archive.elixircore.exception.SessionInvalidException;
import org.ega_archive.elixircore.exception.SessionTimedOutException;
import org.ega_archive.elixircore.exception.UnauthorizedException;
import org.ega_archive.elixircore.service.CustomParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

@Log4j
@ControllerAdvice
public class GeneralHandler {

  @Autowired
  CustomParametersService customParametersService;

  //Generic
  @ExceptionHandler
  @ResponseBody
  public Base<String> handleException(Exception ex, HttpServletResponse response) {

    response.setStatus(HttpStatus.BAD_REQUEST.value());
    int statusCode = HttpServletResponse.SC_BAD_REQUEST;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler({CookieNotFoundException.class, UnauthorizedException.class,
                     SessionTimedOutException.class, SessionInvalidException.class})
  @ResponseBody
  public Base<String> requestBindingException(Exception ex, HttpServletResponse response) {

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    int statusCode = HttpServletResponse.SC_UNAUTHORIZED;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler(RestRuntimeException.class)
  @ResponseBody
  public Base<String> requestBindingException(RestRuntimeException ex,
                                              HttpServletResponse response) {

    int statusCode = 400;
    try {
      statusCode = Integer.parseInt(ex.getCode());
    } catch (NumberFormatException exception) {

    }
    response.setStatus(statusCode);

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseBody
  public Base<String> requestBindingException(NotFoundException ex, HttpServletResponse response) {

    response.setStatus(HttpStatus.NOT_FOUND.value());
    int statusCode = HttpServletResponse.SC_NOT_FOUND;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler(PreConditionFailed.class)
  @ResponseBody
  public Base<String> requestBindingException(PreConditionFailed ex, HttpServletResponse response) {

    response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    int statusCode = HttpServletResponse.SC_PRECONDITION_FAILED;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler({ServerDownException.class, ServiceUnavailableException.class})
  @ResponseBody
  public Base<String> unavailableException(Exception ex, HttpServletResponse response) {

    response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
    int statusCode = HttpServletResponse.SC_SERVICE_UNAVAILABLE;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler(SessionAlreadyClosed.class)
  @ResponseBody
  public Base<String> requestBindingException(SessionAlreadyClosed ex,
                                              HttpServletResponse response) {

    response.setStatus(HttpStatus.GONE.value());
    int statusCode = HttpServletResponse.SC_GONE;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }

  @ExceptionHandler(NotImplementedException.class)
  @ResponseBody
  public Base<String> requestBindingException(NotImplementedException ex,
                                              HttpServletResponse response) {

    response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
    int statusCode = HttpServletResponse.SC_NOT_IMPLEMENTED;

    log.error("Exception: " + ex.toString(), ex);
    return new Base<String>(String.valueOf(statusCode), ex, customParametersService.isDebugRequest());
  }
}
