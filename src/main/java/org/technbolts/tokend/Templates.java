package org.technbolts.tokend;

import java.time.Instant;
import java.util.Optional;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Templates {

  TemplateId add(NewTemplate newTemplate);

  Template findByName(String templateName);

  Optional<TemplateDatedSettings> findDatedSettingAt(TemplateId id, Instant at);

  TemplateDatedSettings saveOrGetOnConflicts(NewTemplateDatedSettings datedSettings);
}
