package org.technbolts.shared;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Paging {
  private final int count;
  private final String cursor;

  public Paging(int count, String cursor) {
    this.count = count;
    this.cursor = cursor;
  }
}
