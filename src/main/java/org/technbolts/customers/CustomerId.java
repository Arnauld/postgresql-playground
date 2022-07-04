package org.technbolts.customers;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CustomerId {
  private final long id;

  public CustomerId(long id) {
    this.id = id;
  }

  public long raw() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CustomerId that = (CustomerId) o;

    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
