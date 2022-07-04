package org.technbolts.tokend;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.technbolts.tokend.util.InstantRange;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.cronutils.model.definition.CronDefinitionBuilder.instanceDefinitionFor;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class RotationPeriod {

  private final Instant effectiveAt;
  private final String cronExpression;

  private final ZoneId zoneId;

  public RotationPeriod(Instant effectiveAt,
                        String cronExpression,
                        ZoneId zoneId) {
    this.effectiveAt = effectiveAt;
    this.cronExpression = cronExpression;
    this.zoneId = zoneId;
  }

  public Instant effectiveAt() {
    return effectiveAt;
  }

  public String cronExpression() {
    return cronExpression;
  }

  public ZoneId zoneId() {
    return zoneId;
  }

  public InstantRange rangeFor(Instant at) {
    ExecutionTime executionTime = executionTime(cronExpression);
    Optional<ZonedDateTime> last = executionTime.lastExecution(at.atZone(zoneId));
    Optional<ZonedDateTime> next = executionTime.nextExecution(at.atZone(zoneId));
    return new InstantRange(last.get().toInstant(), next.get().toInstant());
  }


  public InstantRange transitionTo(Instant closestAfter,
                                   String nextCronExpression) {
    ExecutionTime nextExec = executionTime(nextCronExpression);
    Optional<ZonedDateTime> zdt = nextExec.nextExecution(closestAfter.atZone(zoneId));
    return new InstantRange(closestAfter, zdt.get().toInstant());
  }

  private static ExecutionTime executionTime(String unixExpr) {
    // https://github.com/jmrozanec/cron-utils
    CronDefinition cronDefinition = instanceDefinitionFor(CronType.UNIX);
    CronParser parser = new CronParser(cronDefinition);
    Cron cron = parser.parse(unixExpr);
    return ExecutionTime.forCron(cron);
  }
}
