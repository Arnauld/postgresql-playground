package org.technbolts.tokend;

import org.json.JSONObject;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class TemplateView {

  private final TemplateMode mode;
  private final JSONObject settings;
  public TemplateView(TemplateMode mode, JSONObject settings) {
    this.mode = mode;
    this.settings = settings;
  }

  public TemplateMode mode() {
    return mode;
  }

  public JSONObject settings() {
    return settings;
  }

}
