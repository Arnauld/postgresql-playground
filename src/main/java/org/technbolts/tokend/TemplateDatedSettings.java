package org.technbolts.tokend;

import org.json.JSONObject;
import org.technbolts.tokend.util.InstantRange;

public class TemplateDatedSettings {
  private final TemplateDatedSettingsId id;
  private final TemplateId templateId;
  private final InstantRange range;
  private final JSONObject settings;

  public TemplateDatedSettings(
          TemplateDatedSettingsId id,
          TemplateId templateId,
          InstantRange range,
          JSONObject settings) {
    this.id = id;
    this.templateId = templateId;
    this.range = range;
    this.settings = settings;
  }

  public TemplateDatedSettingsId id() {
    return id;
  }

  public InstantRange range() {
    return range;
  }

  public JSONObject settings() {
    return settings;
  }

  @Override
  public String toString() {
    return "TemplateDatedSettings{" +
            "" + id +
            ", " + templateId +
            ", range=" + range +
            ", settings=" + settings +
            '}';
  }
}
