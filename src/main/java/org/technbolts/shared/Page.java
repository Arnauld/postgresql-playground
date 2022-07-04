package org.technbolts.shared;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Page<T> {
  private final List<T> values;
  private final String endCursor;

  public Page(List<T> values, String endCursor) {
    this.values = values;
    this.endCursor = endCursor;
  }

  public List<T> values() {
    return values;
  }
}
