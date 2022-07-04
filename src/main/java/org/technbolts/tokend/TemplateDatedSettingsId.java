package org.technbolts.tokend;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class TemplateDatedSettingsId {

  public static TemplateDatedSettingsId of(long raw) {
    return new TemplateDatedSettingsId(raw);
  }

  private final long raw;

  public TemplateDatedSettingsId(long raw) {
    this.raw = raw;
  }

  public long raw() {
    return raw;
  }

  @Override
  public String toString() {
    return "TemplateId{" + raw + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TemplateDatedSettingsId that = (TemplateDatedSettingsId) o;
    return raw == that.raw;
  }

  @Override
  public int hashCode() {
    return (int) (raw ^ (raw >>> 32));
  }
}
