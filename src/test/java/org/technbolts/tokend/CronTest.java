package org.technbolts.tokend;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CronTest {

  private ZoneId zoneId = ZoneId.of("Europe/Paris");

  @Test
  void case01() {
    // run at 2:15pm on the first of every month
    String unix = "15 14 1 * *";
    // https://github.com/jmrozanec/cron-utils
    ExecutionTime executionTime = executionTime(unix);
    Instant start = ZonedDateTime.of(2020, 1, 1, 1, 15, 0, 0, zoneId).toInstant();
    for (int i = 0; i < 350; i++) {
      Instant at = start.plus(i, ChronoUnit.DAYS);
      Optional<ZonedDateTime> last = executionTime.lastExecution(at.atZone(zoneId));
      Optional<ZonedDateTime> next = executionTime.nextExecution(at.atZone(zoneId));
      System.out.println("" + at + " => [" + last + "; " + next + "]");
    }
  }

  @Test
  void compute_next_cron_range_when_expression_change() {
    // tag::compute_next_cron_range_when_expression_change[]
    // Sunday, June the 5th, 2022
    Instant lastUpperBound = Instant.parse("2022-06-05T01:00:00.00Z");

    // At 03:00 PM, only on Monday
    String unix = "0 15 * * 1";
    ExecutionTime executionTime = executionTime(unix);
    Optional<ZonedDateTime> effectiveAfter = executionTime.nextExecution(lastUpperBound.atZone(zoneId));

    // Monday, June the 6th, 2022 at 15h Europe/Paris
    assertThat(effectiveAfter).contains(ZonedDateTime.parse("2022-06-06T15:00+02:00[Europe/Paris]"));
    // end::compute_next_cron_range_when_expression_change[]
  }

  // tag::executionTime[]
  private ExecutionTime executionTime(String cronExpression) {
    // https://github.com/jmrozanec/cron-utils
    CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
    CronParser parser = new CronParser(cronDefinition);
    Cron cron = parser.parse(cronExpression);
    return ExecutionTime.forCron(cron);
  }
  // end::executionTime[]
}
