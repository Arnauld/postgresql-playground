package org.technbolts.tokend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DataOutput {
  public static final DataOutput EMPTY = new DataOutput(Collections.emptyList());
  private final List<String> results;

  DataOutput(List<String> results) {
    this.results = results;
  }

  void add(String res) {
    results.add(res);
  }

  public List<String> results() {
    return results;
  }
}
