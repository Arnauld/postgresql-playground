package org.technbolts.tokend.inmemory;

import org.technbolts.tokend.NewTemplateDatedSettings;
import org.technbolts.tokend.Template;
import org.technbolts.tokend.TemplateDatedSettings;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InMemoryTemplateWrapper {
  private final Template template;
  private List<TemplateDatedSettings> datedSettings = new CopyOnWriteArrayList<>();

  public InMemoryTemplateWrapper(Template template) {
    this.template = template;
  }

  public Template template() {
    return template;
  }

  public Stream<TemplateDatedSettings> datedSettings() {
    return datedSettings.stream();
  }

  public synchronized TemplateDatedSettings addOrGetOnConflicts(NewTemplateDatedSettings newSettings) {
    for (TemplateDatedSettings datedSetting : datedSettings) {
      if (datedSetting.range().overlaps(newSettings.range())) {
        return datedSetting;
      }
    }
    TemplateDatedSettings settings = new TemplateDatedSettings(newSettings.templateId(), newSettings.range(), newSettings.settings());
    datedSettings.add(settings);
    return settings;
  }
}
