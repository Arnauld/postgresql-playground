package org.technbolts.tokend;

import org.json.JSONObject;
import org.technbolts.tokend.util.InstantRange;

public class TemplateDatedSettings {
  private final TemplateId templateId;
  private final InstantRange range;
  private final JSONObject settings;

  public TemplateDatedSettings(TemplateId templateId, InstantRange range, JSONObject settings) {
    this.templateId = templateId;
    this.range = range;
    this.settings = settings;
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
            "templateId=" + templateId +
            ", range=" + range +
            ", settings=" + settings +
            '}';
  }
}
