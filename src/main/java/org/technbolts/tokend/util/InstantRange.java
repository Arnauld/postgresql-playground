package org.technbolts.tokend.util;

import java.time.Instant;

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
  public String toString() {
    return "[" + min + ", " + max + ']';
  }
}
