package org.technbolts.tokend.util;

import java.time.Instant;
import java.util.Objects;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InstantRange {
  public final Instant min;
  public final Instant max;

  public InstantRange(Instant min, Instant max) {
    this.min = min;
    this.max = max;
  }

  public boolean overlaps(InstantRange range) {
    return !(this.min.isAfter(range.max) || range.min.isAfter(this.max));
  }

  public boolean includes(Instant instant) {
    return !(instant.isBefore(min) || instant.isAfter(max));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InstantRange that = (InstantRange) o;
    return Objects.equals(min, that.min) && Objects.equals(max, that.max);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(min) + 31 * Objects.hashCode(max);
  }

  @Override
  public String toString() {
    return "[" + min + ", " + max + ']';
  }
}
