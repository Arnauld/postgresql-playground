package org.technbolts.tokend;

import com.privacylogistics.FF3Cipher;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class FF3Test {

  @Test
  void ff3usage() throws IllegalBlockSizeException, BadPaddingException {
    FF3Cipher cipher = new FF3Cipher(
            "5c321c78c8d9a1f8ffa504f9f30a4a0e39e072aa0d72358849f3a0df5bf62b3c",
            "acc1f3f833283f",
            "0123456789abcdefghijk:");

    String s = cipher.encrypt("0000000:abjk12");
    System.out.println(s);
    System.out.println(cipher.decrypt(s));

  }
}
