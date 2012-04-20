package callback.menu;

import java.util.List;
import java.io.OutputStream;
import java.io.FileOutputStream;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 23:32:20
 */
public class AdditionalMenuConfigurationFileGenerator {

  private static final String OUTPUT_FILE_NAME = "../6C02";

  public static void main(String[] args) throws Exception {

    int totalBytes = 0;
    OutputStream fos = null;
    try {
      fos = new FileOutputStream(OUTPUT_FILE_NAME);
      byte[] content = new byte[5];
      content[0] = (byte) 3; //CustomMenuRecordsNo: Number of Custom menus
      content[1] = (byte) 1; //PhoneBookMenuEnabled: 1-enabled, 0-disabled
      content[2] = (byte) 1; //LastDialedMenuEnabled: 1-enabled, 0-disabled
      content[3] = (byte) 1; //CallbackMenuEnabled: 1-always enabled, 0-always disabled, 2-enabled only if there is no Call Control support
      content[4] = (byte) 1; //CustomMenuEnabled: 1-enabled, 0-disabled

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