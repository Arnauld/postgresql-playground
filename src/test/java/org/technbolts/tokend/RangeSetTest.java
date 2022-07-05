package org.technbolts.tokend;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class RangeSetTest {

  public static final ZoneId PARIS = ZoneId.of("Europe/Paris");

  @Test
  void generate_all_missing_ranges() {
    // At 04:00 on day-of-month 5
    ExecutionTime executionTime = executionTime("0 4 5 * *");

    ZonedDateTime start = LocalDate.of(2021, 3, 6).atStartOfDay().atZone(PARIS);
    Range<Instant> range1 = rangeFrom(executionTime, start);
    Range<Instant> range2 = rangeFrom(executionTime, start.plus(3, ChronoUnit.MONTHS));
    Range<Instant> range3 = rangeFrom(executionTime, start.plus(10, ChronoUnit.MONTHS));

    RangeSet<Instant> rangeSet = TreeRangeSet.create();
    rangeSet.add(Range.upTo(LocalDate.of(2022, 7, 5).atStartOfDay().atZone(PARIS).toInstant(), BoundType.OPEN));
    rangeSet.remove(range1);
    rangeSet.remove(range2);
    rangeSet.remove(range3);

    rangeSet.asRanges().forEach(r -> {
      System.out.println(r);
    });
  }

  private Range<Instant> rangeFrom(ExecutionTime executionTime, ZonedDateTime at) {
    return Range.range(
            executionTime.lastExecution(at).get().toInstant(),
            BoundType.CLOSED,
            executionTime.nextExecution(at).get().toInstant(),
            BoundType.OPEN);
  }


  private static ExecutionTime executionTime(String cronExpression) {
    // https://github.com/jmrozanec/cron-utils
    CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
    CronParser parser = new CronParser(cronDefinition);
    Cron cron = parser.parse(cronExpression);
    return ExecutionTime.forCron(cron);
  }
}
