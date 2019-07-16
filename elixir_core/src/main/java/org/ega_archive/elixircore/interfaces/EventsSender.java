package org.ega_archive.elixircore.interfaces;

public interface EventsSender<U, V, W> {

  public U send(String dest, V param, W event);

}
