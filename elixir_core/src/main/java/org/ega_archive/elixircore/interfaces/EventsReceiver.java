package org.ega_archive.elixircore.interfaces;

//The name of this interface is Wrong, it Should be MessageReceiver
public interface EventsReceiver<U, V> {

  public U receive(V event);

}
