package org.technbolts.tokend.inmemory;

import org.technbolts.tokend.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InMemoryTemplates implements Templates {

  private final AtomicLong idGen = new AtomicLong();

  private Map<String, InMemoryTemplateWrapper> byName = new ConcurrentHashMap<>();

  @Override
  public TemplateId add(NewTemplate newTemplate) {
    TemplateId id = new TemplateId(idGen.incrementAndGet());
    Template template = new Template(id,
            newTemplate.name(),
            newTemplate.mode(),
            newTemplate.rotationPeriod(),
            newTemplate.settings());
    byName.put(newTemplate.name(), new InMemoryTemplateWrapper(template));
    return id;
  }


  @Override
  public Template findByName(String templateName) {
    return byName.get(templateName).template();
  }

  @Override
  public Optional<TemplateDatedSettings> findDatedSettingAt(TemplateId id, Instant at) {
    return byName.values().stream()
            .filter(m -> m.template().id().equals(id))
            .flatMap(InMemoryTemplateWrapper::datedSettings)
            .filter(dt -> dt.range().includes(at))
            .findFirst();
  }

  @Override
  public TemplateDatedSettings saveOrGetOnConflicts(NewTemplateDatedSettings datedSettings) {
    return byName.values().stream()
            .filter(m -> m.template().id().equals(datedSettings.templateId()))
            .map(m -> m.addOrGetOnConflicts(datedSettings))
            .findFirst()
            .get();
  }
}
