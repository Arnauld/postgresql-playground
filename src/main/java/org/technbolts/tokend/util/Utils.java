package org.technbolts.tokend.util;

public class Utils {

  /**
   * Converts a byte array to a hexadecimal String. Each byte is presented by
   * its two digit hex-code; 0x0A -> "0a", 0x00 -> "00". No leading "0x" is
   * included in the result.
   *
   * @param value the byte array to be converted
   * @return the hexadecimal string representation of the byte array
   */
  public static String toHexString(byte[] value) {
    if (value == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder(2 * value.length);
    for (byte b : value) {
      int single = b & 0xFF;
      if (single < 0x10) {
        sb.append('0');
      }
      sb.append(Integer.toString(single, 16));
    }
    return sb.toString();
  }
}
