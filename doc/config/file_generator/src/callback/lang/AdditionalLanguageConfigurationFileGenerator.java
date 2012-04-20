package callback.lang;

import java.util.List;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 23:36:20
 */
public class AdditionalLanguageConfigurationFileGenerator {

  private static final String OUTPUT_FILE_NAME = "../6C03";

  public static void main(String[] args) throws Exception {

    int totalBytes = 0;
    OutputStream fos = null;
    try {
      fos = new FileOutputStream(OUTPUT_FILE_NAME);
      byte[] content = new byte[2];
      content[0] = (byte) 8; //LanguagesNo: nr of all languages
      content[1] = (byte) 21; //LanguageTextsNo: nr of texts in a language file. 

      System.out.println("Writting record, bytes=" + content.length);
      fos.write(content);
      totalBytes = totalBytes + content.length;
    } finally {
      if (fos != null) {
        fos.flush();
        fos.close();
      }
    }
    System.out.println("Total bytes written=" + totalBytes);
  }

}