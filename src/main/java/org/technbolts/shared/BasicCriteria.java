package org.technbolts.shared;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class BasicCriteria<T> {
  private final T orderBy;
  private final String filter;

  public BasicCriteria(T orderBy, String filter) {
    this.orderBy = orderBy;
    this.filter = filter;
  }

  public T orderBy() {
    return orderBy;
  }

  public String filter() {
    return filter;
  }
}
