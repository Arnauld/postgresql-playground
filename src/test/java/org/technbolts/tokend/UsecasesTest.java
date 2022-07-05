package org.technbolts.tokend;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.technbolts.tokend.inmemory.InMemoryTemplates;
import org.technbolts.tokend.util.InstantRange;
import org.technbolts.tokend.util.Utils;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UsecasesTest {

  private static final String TENANT = "t001";
  private static final String TEMPLATE_NAME = "ssn";
  private Templates templates;
  private TemplateId templateId;
  private DataProtection dataProtection;
  private Template template;

  @BeforeEach
  void setup() {
    templates = new InMemoryTemplates();
    templateId = templates.add(new NewTemplate(TEMPLATE_NAME,
            TemplateMode.FF3,
            // https://crontab.guru/
            // At 04:00 on day-of-month 5
            new RotationPeriod(Instant.parse("2020-06-05T01:00:00.00Z"), "0 4 5 * *", ZoneId.of("Europe/Paris")),
            new JSONObject()));
    template = templates.findByName(TEMPLATE_NAME);
    dataProtection = new DataProtection(templates);
  }

  @Test
  void case001() {
    Instant when = Instant.now();
    pr(when, "CarmenMcCallum");
  }

  @Test
  void maxLen() {
    Instant when = Instant.parse("2022-06-15T11:57:03Z");
    String actual = "CarmenMcCallum" + "CarmenMcCallum" + "CarmenMcCallum" + "CarmenMcCallum" + "CarmenMcCallum" + "CarmenMcCallum";
    assertThatThrownBy(() -> dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, when, actual)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("message length 84 is not within min 4 and max 32 bounds");
  }

  class T {
    final Instant when;
    final List<String> what;

    T(Instant when, String what) {
      this.when = when;
      this.what = Collections.singletonList(what);
    }

    T(Instant when, List<String> what) {
      this.when = when;
      this.what = what;
    }
  }

  @Test
  void loop_per_day() {
    SecureRandom random = new SecureRandom();
    int nbSecondsPerDay = 60 * 60 * 24;
    ZonedDateTime begin = LocalDate.now().atStartOfDay().atZone(ZoneId.of("Europe/Paris"));
    byte[] buffer = new byte[16];
    List<T> data = new ArrayList<>();
    for (int i = 0; i < 5_000; i++) {
      Instant when = begin.minusSeconds(random.nextInt(nbSecondsPerDay)).toInstant();
      random.nextBytes(buffer);
      String what = Utils.toHexString(buffer);
      data.add(new T(when, what));
    }

    for (int iter = 0; iter < 10; iter++) {
      long start = System.nanoTime();
      for (int i = 0, n = data.size(); i < n; i++) {
        T t = data.get(i);
        DataOutput encrypted = dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, t.when, t.what));
        DataOutput decrypted = dataProtection.decrypt(new DataInput(TENANT, TEMPLATE_NAME, t.when, encrypted.results()));
        assertThat(decrypted.results()).isEqualTo(t.what);
      }
      long end = System.nanoTime();
      System.out.println("elapsed " + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
    }
  }

  @Test
  void loop_multiple_text_per_day() {
    SecureRandom random = new SecureRandom();
    int nbSecondsPerDay = 60 * 60 * 24;
    ZonedDateTime begin = LocalDate.now().atStartOfDay().atZone(ZoneId.of("Europe/Paris"));
    byte[] buffer = new byte[16];
    List<T> data = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      Instant when = begin.minusSeconds(random.nextInt(nbSecondsPerDay)).toInstant();
      List<String> what = new ArrayList<>();
      for (int j = 0; j < 100; j++) {
        random.nextBytes(buffer);
        String w = Utils.toHexString(buffer);
        what.add(w);
      }
      data.add(new T(when, what));
    }

    for (int iter = 0; iter < 10; iter++) {
      long start = System.nanoTime();
      for (int i = 0, n = data.size(); i < n; i++) {
        T t = data.get(i);
        DataOutput encrypted = dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, t.when, t.what));
        DataOutput decrypted = dataProtection.decrypt(new DataInput(TENANT, TEMPLATE_NAME, t.when, encrypted.results()));
        assertThat(decrypted.results()).isEqualTo(t.what);
      }
      long end = System.nanoTime();
      System.out.println("elapsed " + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
    }
  }

  @Test
  void encrypt_decrypt_round_trip() {
    Instant when = Instant.parse("2022-06-15T11:57:03Z");
    String actual = "Carmen McCallum";
    DataOutput encrypted = dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, when, actual));
    System.out.println(encrypted.results());
    DataOutput decrypted = dataProtection.decrypt(new DataInput(TENANT, TEMPLATE_NAME, when, encrypted.results()));
    assertThat(decrypted.results().get(0)).isEqualTo(actual);
  }

  @Test
  void should_encrypt_the_exact_same_value_with_known_tweak_and_key() {
    templates.saveOrGetOnConflicts(new NewTemplateDatedSettings(templateId,
            new InstantRange(Instant.parse("2022-06-05T02:00:00Z"), Instant.parse("2022-07-05T02:00:00Z")),
            new JSONObject()
                    .put(FF3.ALPHABET, FF3.DEFAULT_ALPHABET)
                    .put(FF3.TWEAK, "b3a5c08480c274")
                    .put(FF3.KEY, "b55fca344c3c0dcab03ff395dd33d3802a5e4713e2c612167754eca6f1f78a37")));
    Instant when = Instant.parse("2022-06-15T11:57:03Z");

    assertThat(dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, when, "CarmenMcCallum")).results())
            .contains("o6tphMDTXcFQUg");
  }

  @Test
  void should_encrypt_with_extended_alphabet() {
    templates.saveOrGetOnConflicts(new NewTemplateDatedSettings(templateId,
            new InstantRange(Instant.parse("2022-06-05T02:00:00Z"), Instant.parse("2022-07-05T02:00:00Z")),
            new JSONObject()
                    .put(FF3.ALPHABET, FF3.DEFAULT_ALPHABET + " ")
                    .put(FF3.TWEAK, "b3a5c08480c274")
                    .put(FF3.KEY, "b55fca344c3c0dcab03ff395dd33d3802a5e4713e2c612167754eca6f1f78a37")));
    Instant when = Instant.parse("2022-06-15T11:57:03Z");

    assertThat(dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, when, "Carmen McCallum")).results())
            .contains("fkHmSnglthu8SWo");
    assertThat(dataProtection.decrypt(new DataInput(TENANT, TEMPLATE_NAME, when, "fkHmSnglthu8SWo")).results())
            .contains("Carmen McCallum");
  }

  private void pr(Instant when, String content) {
    DataOutput encrypt = dataProtection.encrypt(new DataInput(TENANT, TEMPLATE_NAME, when, content));
    System.out.println("'" + content + "' (" + content.length() + ") => '" + encrypt.results().get(0) + "' (" + encrypt.results().get(0).length() + ")");
    Optional<TemplateDatedSettings> datedSettings = templates.findDatedSettingAt(templateId, when);
    System.out.println(datedSettings);
  }
}
