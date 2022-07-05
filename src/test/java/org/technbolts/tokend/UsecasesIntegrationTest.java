package org.technbolts.tokend;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.technbolts.tokend.inmemory.InMemoryTemplates;
import org.technbolts.tokend.jdbc.JdbcTemplates;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class UsecasesIntegrationTest {

  public static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
  private JdbcTemplates templates;
  private DataProtection dataProtection;

  @BeforeEach
  void setup() {
    templates = new JdbcTemplates();
    dataProtection = new DataProtection(templates);
  }

  @Test
  void case001() {
    String name = "reservation";
    TemplateId templateId = templates.add(new NewTemplate(
            name,
            TemplateMode.FF3,
            // https://crontab.guru/
            // At 04:00 on day-of-month 5
            new RotationPeriod(Instant.parse("2020-06-05T01:00:00.00Z"), "0 4 5 * *", ZoneId.of("Europe/Paris")),
            new JSONObject()));

    assertThat(templateId).isNotNull();

    DataOutput output = dataProtection.encrypt(new DataInput(
            null,
            name,
            ZonedDateTime.of(LocalDate.of(2021, 3, 23), LocalTime.now(), ZONE_ID).toInstant(),
            "ChuckFinley"));
    System.out.println(output.results());
    assertThat(output.results()).isNotEmpty();
  }

  @Test
  void case001_apres() {
    String name = "reservation";
    DataOutput output = dataProtection.encrypt(new DataInput(
            null,
            name,
            ZonedDateTime.of(LocalDate.of(2021, 3, 23), LocalTime.now(), ZONE_ID).toInstant(),
            "ChuckFinley"));
    System.out.println(output.results());
    assertThat(output.results()).isNotEmpty();

    output = dataProtection.encrypt(new DataInput(
            null,
            name,
            ZonedDateTime.of(LocalDate.of(2022, 3, 23), LocalTime.now(), ZONE_ID).toInstant(),
            "ChuckFinley"));
    System.out.println(output.results());


    assertThat(output.results()).isNotEmpty();output = dataProtection.encrypt(new DataInput(
            null,
            name,
            ZonedDateTime.of(LocalDate.of(2022, 1, 3), LocalTime.now(), ZONE_ID).toInstant(),
            "ChuckFinley"));
    System.out.println(output.results());
    assertThat(output.results()).isNotEmpty();
  }

}
