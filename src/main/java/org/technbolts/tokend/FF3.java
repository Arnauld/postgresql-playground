package org.technbolts.tokend;

import org.json.JSONObject;
import org.technbolts.tokend.util.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

class FF3 {

  public static final String ALPHABET = "alphabet";
  public static final String KEY = "key";
  public static final String KEY_SIZE = "keySize";
  public static final String TWEAK = "tweak";
  public static final String TWEAK_SIZE = "tweakSize";
  public static final String DEFAULT_ALPHABET = FF3Cipher.ASCII_LOWERCASE + FF3Cipher.ASCII_UPPERCASE + FF3Cipher.DIGITS;

  private static final SecureRandom secureRandom = new SecureRandom();

  public static JSONObject consolidate(JSONObject templateSettings, JSONObject datedSettings) {
    JSONObject settings = new JSONObject();
    String alphabet;
    if (datedSettings.has(ALPHABET)) {
      alphabet = datedSettings.getString(ALPHABET);
    } else if (templateSettings.has(ALPHABET)) {
      alphabet = templateSettings.getString(ALPHABET);
    } else {
      alphabet = DEFAULT_ALPHABET;
    }
    settings.put(ALPHABET, alphabet);

    String key;
    if (datedSettings.has(KEY)) {
      key = datedSettings.getString(KEY);
    } else if (templateSettings.has(KEY)) {
      key = templateSettings.getString(KEY);
    } else {
      throw new RuntimeException("No key defined");
    }
    settings.put(KEY, key);

    String tweak;
    if (datedSettings.has(TWEAK)) {
      tweak = datedSettings.getString(TWEAK);
    } else if (datedSettings.has(TWEAK_SIZE)) {
      tweak = datedSettings.getString(TWEAK_SIZE);
    } else {
      throw new RuntimeException("No tweak defined");
    }
    settings.put(TWEAK, tweak);

    return settings;
  }

  static JSONObject newFF3Settings(Template template) {
    JSONObject templateSettings = template.settings();
    JSONObject settings = new JSONObject();

    String key;
    if (templateSettings.has(KEY)) {
      key = templateSettings.getString(KEY);
    } else {
      int keySize = 256;
      if (templateSettings.has(KEY_SIZE)) {
        keySize = templateSettings.getInt(KEY_SIZE);
      }
      key = generateFF3Key(keySize);
    }
    settings.put(KEY, key);

    String tweak;
    if (templateSettings.has(TWEAK)) {
      tweak = templateSettings.getString(TWEAK);
    } else {
      int tweakSize = 56;
      if (templateSettings.has(TWEAK_SIZE)) {
        tweakSize = templateSettings.getInt(TWEAK_SIZE);
      }
      tweak = generateFF3Tweak(tweakSize);
    }
    settings.put(TWEAK, tweak);
    return settings;
  }

  private static String generateFF3Tweak(int tweakSize) {
    byte[] secureRandomKeyBytes = new byte[tweakSize / 8];
    secureRandom.nextBytes(secureRandomKeyBytes);
    return Utils.toHexString(secureRandomKeyBytes);
  }

  private static String generateFF3Key(int keySize) {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(keySize);
      SecretKey secretKey = keyGenerator.generateKey();
      return Utils.toHexString(secretKey.getEncoded());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static void checkContentAgainstAlphabet(String content, String alphabet) {
    if (!content.matches("[" + alphabet + "]+")) {
      throw new RuntimeException("Input does not match alphabet got: '" + content + "', vs :" + alphabet);
    }
  }

  static DataOutput encrypt(TemplateView templateView, DataInput dataInput) {
    DataOutput out = new DataOutput(new ArrayList<>());

    JSONObject settings = templateView.settings();
    String key = settings.getString(KEY);
    String tweak = settings.getString(TWEAK);
    String alphabet = settings.getString(ALPHABET);

    FF3Cipher cipher = new FF3Cipher(key, tweak, alphabet);
    for (String content : dataInput.content) {
      checkContentAgainstAlphabet(content, alphabet);
      try {
        out.add(cipher.encrypt(content));
      } catch (BadPaddingException | IllegalBlockSizeException e) {
        throw new RuntimeException(e);
      }
    }
    return out;
  }

  static DataOutput decrypt(TemplateView templateView, DataInput dataInput) {
    DataOutput out = new DataOutput(new ArrayList<>());

    JSONObject settings = templateView.settings();
    String key = settings.getString(KEY);
    String tweak = settings.getString(TWEAK);
    String alphabet = settings.getString(ALPHABET);

    FF3Cipher cipher = new FF3Cipher(key, tweak, alphabet);
    for (String content : dataInput.content) {
      try {
        out.add(cipher.decrypt(content));
      } catch (BadPaddingException | IllegalBlockSizeException e) {
        throw new RuntimeException(e);
      }
    }
    return out;
  }
}
