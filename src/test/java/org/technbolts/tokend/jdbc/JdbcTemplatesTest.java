package org.technbolts.tokend.jdbc;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.technbolts.tokend.NewTemplate;
import org.technbolts.tokend.NewTemplateDatedSettings;
import org.technbolts.tokend.RotationPeriod;
import org.technbolts.tokend.TemplateDatedSettings;
import org.technbolts.tokend.TemplateId;
import org.technbolts.tokend.TemplateMode;
import org.technbolts.tokend.util.InstantRange;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
class JdbcTemplatesTest {

  private JdbcTemplates jdbcTemplates;

  @BeforeEach
  void setUp() {
    jdbcTemplates = new JdbcTemplates();
  }

  @Test
  void case_conflict() {
    String name = "templ-" + System.currentTimeMillis();
    Instant effectiveAt = Instant.parse("2020-06-05T01:00:00.00Z");
    TemplateId templateId = jdbcTemplates.add(new NewTemplate(
            name,
            TemplateMode.FF3,
            // https://crontab.guru/
            // At 04:00 on day-of-month 5
            new RotationPeriod(effectiveAt, "0 4 5 * *", ZoneId.of("Europe/Paris")),
            new JSONObject()));
    assertThat(templateId).isNotNull();


    NewTemplateDatedSettings datedSettings1 = new NewTemplateDatedSettings(
            templateId,
            new InstantRange(effectiveAt, effectiveAt.plus(1, ChronoUnit.DAYS)),
            new JSONObject().put("a", "zza-" + name)
    );
    NewTemplateDatedSettings datedSettings2 = new NewTemplateDatedSettings(
            templateId,
            new InstantRange(effectiveAt, effectiveAt.plus(1, ChronoUnit.DAYS)),
            new JSONObject().put("b", "zzb-" + name)
    );
    TemplateDatedSettings tds1 = jdbcTemplates.saveOrGetOnConflicts(datedSettings1);
    TemplateDatedSettings tds2 = jdbcTemplates.saveOrGetOnConflicts(datedSettings2);
    assertThat(tds2.id()).isEqualTo(tds1.id());
    assertThat(tds2.settings().toString())
            .describedAs("id: %s", tds2.id())
            .isEqualTo(new JSONObject().put("a", "zza-" + name).toString());
  }

}