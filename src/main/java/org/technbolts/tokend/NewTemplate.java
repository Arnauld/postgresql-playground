package org.technbolts.tokend;

import org.json.JSONObject;

import java.time.Instant;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NewTemplate {
  private final String name;
  private final TemplateMode mode;
  private final JSONObject settings;
  private final RotationPeriod rotationPeriod;

  public NewTemplate(String name,
                     TemplateMode mode,
                     RotationPeriod rotationPeriod,
                     JSONObject settings) {
    this.name = name;
    this.mode = mode;
    this.settings = settings;
    this.rotationPeriod = rotationPeriod;
  }

  public String name() {
    return name;
  }

  public TemplateMode mode() {
    return mode;
  }

  public JSONObject settings() {
    return settings;
  }

  public RotationPeriod rotationPeriod() {
    return rotationPeriod;
  }
}
