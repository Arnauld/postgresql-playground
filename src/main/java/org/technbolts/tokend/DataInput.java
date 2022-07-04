package org.technbolts.tokend;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DataInput {
  public final String tenantCode;
  public final String templateName;
  //
  public final Instant timestamp;
  public final List<String> content;

  public DataInput(String tenantCode, String templateName, Instant timestamp, String... content) {
    this(tenantCode, templateName, timestamp, Arrays.asList(content));
  }

  public DataInput(String tenantCode, String templateName, Instant timestamp, List<String> content) {
    this.tenantCode = tenantCode;
    this.templateName = templateName;
    this.timestamp = timestamp;
    this.content = content;
  }
}
