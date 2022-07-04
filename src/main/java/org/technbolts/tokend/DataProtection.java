package org.technbolts.tokend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.technbolts.tokend.util.InstantRange;

import java.time.Instant;
import java.util.Optional;

public class DataProtection {

  private final Templates templates;

  public DataProtection(Templates templates) {
    this.templates = templates;
  }

  public DataOutput encrypt(DataInput dataInput) {
    TemplateView templateView = resolvesAt(dataInput.tenantCode, dataInput.templateName, dataInput.timestamp, true)
            .orElseThrow(() -> new RuntimeException("Failed to resolve settings for '" + dataInput.templateName + "' at " + dataInput.timestamp));
    if (templateView.mode() == TemplateMode.FF3) {
      return FF3.encrypt(templateView, dataInput);
    }
    throw new UnsupportedOperationException("{" + templateView.mode() + "}");
  }

  public DataOutput decrypt(DataInput dataInput) {
    Optional<TemplateView> templateViewOpt = resolvesAt(dataInput.tenantCode, dataInput.templateName, dataInput.timestamp, false);
    if (templateViewOpt.isEmpty()) {
      return DataOutput.EMPTY;
    }

    TemplateView templateView = templateViewOpt.get();
    if (templateView.mode() == TemplateMode.FF3) {
      return FF3.decrypt(templateView, dataInput);
    }
    throw new UnsupportedOperationException("{" + templateView.mode() + "}");
  }

  public Optional<TemplateView> resolvesAt(String tenantCode, String templateName, Instant effectiveAt, boolean createIfMissing) {
    Template template = templates.findByName(templateName);
    Optional<TemplateDatedSettings> datedSettingsOpt = templates.findDatedSettingAt(template.id(), effectiveAt);
    if (datedSettingsOpt.isEmpty() && !createIfMissing) {
      return Optional.empty();
    }

    TemplateDatedSettings datedSettings = datedSettingsOpt.orElseGet(() -> {
      NewTemplateDatedSettings settings = generatesDatedSettings(template, effectiveAt);
      return templates.saveOrGetOnConflicts(settings);
    });
    JSONObject settings = consolidate(template.mode(), template.settings(), datedSettings.settings());
    return Optional.of(new TemplateView(template.mode(), settings));
  }

  private NewTemplateDatedSettings generatesDatedSettings(Template template, Instant effectiveAt) {
    InstantRange range = template.rotationPeriod().rangeFor(effectiveAt);
    return new NewTemplateDatedSettings(template.id(), range, newSettings(template));
  }

  private JSONObject newSettings(Template template) {
    if (template.mode() == TemplateMode.FF3) {
      return FF3.newFF3Settings(template);
    }
    throw new UnsupportedOperationException("{" + template.mode() + "}");
  }

  private JSONObject consolidate(TemplateMode mode, JSONObject templateSettings, JSONObject datedSettings) {
    if (mode == TemplateMode.FF3) {
      return FF3.consolidate(templateSettings, datedSettings);
    }
    JSONObject obj = new JSONObject();
    append(obj, templateSettings);
    append(obj, datedSettings);
    return obj;
  }

  private void append(JSONObject dst, JSONObject src) {
    JSONArray names = src.names();
    if (names == null) return;
    for (Object name : names) {
      String n = (String) name;
      dst.put(n, src.get(n));
    }
  }
}
