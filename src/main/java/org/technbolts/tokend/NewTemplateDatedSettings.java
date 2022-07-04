package org.technbolts.tokend;

import org.json.JSONObject;
import org.technbolts.tokend.util.InstantRange;

public class NewTemplateDatedSettings {

  private final TemplateId templateId;
  private final InstantRange range;
  private final JSONObject settings;

  public NewTemplateDatedSettings(
          TemplateId templateId,
          InstantRange range,
          JSONObject settings) {

    this.templateId = templateId;
    this.range = range;
    this.settings = settings;
  }

  public TemplateId templateId() {
    return templateId;
  }

  public InstantRange range() {
    return range;
  }

  public JSONObject settings() {
    return settings;
  }
}
