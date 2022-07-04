package org.technbolts.tokend;

import com.cronutils.model.time.ExecutionTime;
import org.junit.jupiter.api.Test;
import org.technbolts.tokend.util.InstantRange;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RotationPeriodTest {

  @Test
  void compute_transitionTo__case01() {
    RotationPeriod period = new RotationPeriod(
            ZonedDateTime.parse("2020-06-05T04:00+02:00[Europe/Paris]").toInstant(),
            // At 04:00 on day-of-month 5
            "0 4 5 * *",
            ZoneId.of("Europe/Paris"));

    // tag::compute_next_cron_range_when_expression_change[]
    // Sunday, June the 5th, 2022
    Instant lastUpperBound = Instant.parse("2022-06-05T01:00:00.00Z");

    // At 03:00 PM, only on Monday
    InstantRange instantRange = period.transitionTo(lastUpperBound, "0 15 * * 1");

    // Monday, June the 6th, 2022 at 15h Europe/Paris
    assertThat(instantRange.min).isEqualTo(lastUpperBound);
    assertThat(instantRange.min).isEqualTo(ZonedDateTime.parse("2022-06-06T15:00+02:00[Europe/Paris]").toInstant());
    // end::compute_next_cron_range_when_expression_change[]
  }
}