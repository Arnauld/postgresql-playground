package org.technbolts.tokend;

import org.json.JSONObject;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Template {
  private final TemplateId id;
  private final String name;
  private final TemplateMode mode;
  private final RotationPeriod rotationPeriod;
  private final JSONObject settings;

  public Template(TemplateId id,
                  String name,
                  TemplateMode mode,
                  RotationPeriod rotationPeriod,
                  JSONObject settings) {
    this.id = id;
    this.name = name;
    this.mode = mode;
    this.rotationPeriod = rotationPeriod;
    this.settings = settings;
  }

  public TemplateId id() {
    return id;
  }

  public TemplateMode mode() {
    return mode;
  }

  public RotationPeriod rotationPeriod() {
    return rotationPeriod;
  }

  public JSONObject settings() {
    return settings;
  }

}
