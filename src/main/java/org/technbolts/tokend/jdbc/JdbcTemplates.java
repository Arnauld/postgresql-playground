package org.technbolts.tokend.jdbc;

import org.json.JSONObject;
import org.postgresql.util.PGInterval;
import org.technbolts.tokend.*;
import org.technbolts.tokend.util.InstantRange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class JdbcTemplates implements Templates {

  private Connection openConnection() {
    try {
      Class.forName("org.postgresql.Driver");
      return DriverManager.getConnection(
              "jdbc:postgresql://localhost:7001/futurama",
              "futurama_secrets_app",
              "secrets_p");
    } catch (Exception e) {
      throw new RuntimeException("Failed to obtain connection", e);
    }
  }

  @Override
  public TemplateId add(NewTemplate newTemplate) {
    String sql = "insert into secrets (" +
            "name, " + //1
            "rotation_started_at, " + //2
            "rotation_period, " + //3
            "rotation_timezone, " + //4
            "mode, " +//5
            "settings" + //6
            ") values (?, ?, ?, ?, ?::secrettype, ?::JSONB) returning id";
    try (Connection conn = openConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, newTemplate.name());
      pstmt.setTimestamp(2, Timestamp.from(newTemplate.rotationPeriod().effectiveAt()));
      pstmt.setString(3, newTemplate.rotationPeriod().cronExpression());
      pstmt.setString(4, newTemplate.rotationPeriod().zoneId().getId());
      pstmt.setString(5, newTemplate.mode().name());
      pstmt.setString(6, newTemplate.settings().toString());
      boolean rowUpdated = pstmt.execute();
      if (!rowUpdated) {
        throw new RuntimeException("No row inserted...");
      }
      ResultSet resultSet = pstmt.getResultSet();
      if (resultSet.next()) {
        return TemplateId.of(resultSet.getLong(1));
      }
      throw new RuntimeException("No id generated...");
    } catch (Exception e) {
      throw new RuntimeException("Failed to insert secrets...", e);
    }
  }

  @Override
  public Template findByName(String templateName) {
    String sql = "select " +
            "id, " + //1
            "name, " + //2
            "rotation_started_at, " + //3
            "rotation_period, " + //4
            "rotation_timezone, " + //5
            "mode, " +//6
            "settings " + //7
            "from secrets where name = ?";
    try (Connection conn = openConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, templateName);
      ResultSet resultSet = pstmt.executeQuery();
      if (resultSet.next()) {
        return new Template(
                TemplateId.of(resultSet.getLong("id")),
                resultSet.getString("name"),
                TemplateMode.valueOf(resultSet.getString("mode")),
                new RotationPeriod(
                        resultSet.getTimestamp("rotation_started_at").toInstant(),
                        resultSet.getString("rotation_period"),
                        ZoneId.of(resultSet.getString("rotation_timezone"))),
                new JSONObject(resultSet.getString("settings")));
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException("Failed to find secrets...", e);
    }
  }

  @Override
  public Optional<TemplateDatedSettings> findDatedSettingAt(TemplateId id, Instant at) {
    String sql = "select " +
            "id, " + //
            "secret_id, " + //
            "lower(tz_range) as rg_min, " + //
            "upper(tz_range) as rg_max, " + //
            "settings " + //
            "from dated_settings " +//
            "where secret_id = ? and tz_range @> ?::timestamptz ";
    try (Connection conn = openConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, id.raw());
      pstmt.setTimestamp(2, Timestamp.from(at));
      ResultSet resultSet = pstmt.executeQuery();
      if (resultSet.next()) {
        return Optional.of(new TemplateDatedSettings(
                id,
                new InstantRange(
                        resultSet.getTimestamp("rg_min").toInstant(),
                        resultSet.getTimestamp("rg_max").toInstant()
                ),
                new JSONObject(resultSet.getString("settings"))));
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException("Failed to find dated secrets...", e);
    }
  }

  private Optional<TemplateDatedSettings> findDatedSettingById(long raw) {
    String sql = "select " +
            "id, " + //
            "secret_id, " + //
            "lower(tz_range) as rg_min, " + //
            "upper(tz_range) as rg_max, " + //
            "settings " + //
            "from dated_settings " +//
            "where id = ?";
    try (Connection conn = openConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, raw);
      ResultSet resultSet = pstmt.executeQuery();
      if (resultSet.next()) {
        return Optional.of(new TemplateDatedSettings(
                TemplateId.of(resultSet.getLong("secret_id")),
                new InstantRange(
                        resultSet.getTimestamp("rg_min").toInstant(),
                        resultSet.getTimestamp("rg_max").toInstant()
                ),
                new JSONObject(resultSet.getString("settings"))));
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException("Failed to find dated secrets...", e);
    }
  }

  @Override
  public TemplateDatedSettings saveOrGetOnConflicts(NewTemplateDatedSettings datedSettings) {
    String sql = "INSERT INTO dated_settings (" +
            "secret_id, " + //1
            "tz_range, " + //2
            "settings" + //3
            ") VALUES (?, ?::TSTZRANGE, ?::JSONB) " +
            "ON CONFLICT ON CONSTRAINT dated_settings_tz_range " +
            "DO NOTHING " +
            "returning id";
    try (Connection conn = openConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, datedSettings.templateId().raw());
      pstmt.setString(2, toSqlString(datedSettings.range()));
      pstmt.setString(3, datedSettings.settings().toString());
      boolean rowInserted = pstmt.execute();
      ResultSet resultSet = pstmt.getResultSet();
      if (resultSet.next()) {
        return findDatedSettingById(resultSet.getLong(1)).get();
      }
      throw new RuntimeException("No id generated...");
    } catch (Exception e) {
      throw new RuntimeException("Failed to insert secrets...", e);
    }
  }

  private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Europe/Paris"));

  private String toSqlString(InstantRange range) {
    StringBuilder b = new StringBuilder();
    if (range.min == null) {
      b.append("(");
    } else {
      b.append("[");
      dtf.formatTo(range.min, b);
    }
    b.append(",");
    if (range.max == null) {
      b.append(")");
    } else {
      dtf.formatTo(range.max, b);
      b.append(")");
    }
    return b.toString();
  }
}
