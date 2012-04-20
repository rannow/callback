package callback.menu;

import java.util.List;
import java.io.OutputStream;
import java.io.FileOutputStream;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:08:20
 */
public class MenuStructureConfigurationFileGenerator {

  private static final String OUTPUT_FILE_NAME = "../6C01";

  public static void main(String[] args) throws Exception {
    Menu customMenu = createMenu();
    List<RecordStructure> menuRecords = customMenu.getRecords();

    int totalBytes = 0;
    OutputStream fos = null;
    try {
      fos = new FileOutputStream(OUTPUT_FILE_NAME);
      for (RecordStructure menuRecord : menuRecords) {
        byte[] recordContent = menuRecord.getByteContent();
        System.out.println("Writting record, bytes=" + recordContent.length);
        fos.write(recordContent);
        totalBytes = totalBytes + recordContent.length;
      }
    } finally {
      if (fos != null) {
        fos.flush();
        fos.close();
      }
    }
    System.out.println("Total bytes written=" + totalBytes);
  }

  private static Menu createMenu() {
    Menu menu = new Menu();

    //RECHARGE MENU
    byte[] rechargePhoneNo = new byte[]{  //national no: 094
            (byte) 0xA1, (byte) 0x90, (byte) 0xF4
    };
    MenuRecord balanceMenu = new MenuRecord(20, 1, MenuRecord.MENU_TYPE_PHONE_NO,
            new PhoneNoMenuTypeValue(rechargePhoneNo));
    menu.addRecord(balanceMenu);


    //SMS MENU
/*    byte[] smscPhoneNo = new byte[7];
    smscPhoneNo[0] = (byte)0x91;
    smscPhoneNo[1] = (byte)0x84;
    smscPhoneNo[2] = (byte)0x66;
    smscPhoneNo[3] = (byte)0x48;
    smscPhoneNo[4] = (byte)0x38;
    smscPhoneNo[5] = (byte)0x60;
    smscPhoneNo[6] = (byte)0xF8;

    byte[] smsDestPhoneNo = new byte[7];
    smsDestPhoneNo[0] = (byte)0x91;
    smsDestPhoneNo[1] = (byte)0x84;
    smsDestPhoneNo[2] = (byte)0x66;
    smsDestPhoneNo[3] = (byte)0x48;
    smsDestPhoneNo[4] = (byte)0x38;
    smsDestPhoneNo[5] = (byte)0x60;
    smsDestPhoneNo[6] = (byte)0xF8;
    //todo correct values
    MenuRecord smsMenu = new MenuRecord(3, 2, MenuRecord.MENU_TYPE_SMS,
            new SmsMenuTypeValue(smsDestPhoneNo, smscPhoneNo, "This is example SMS message!"));
    menu.addRecord(smsMenu);*/

    //BALANCE MENU
    MenuRecord ussdMenu = new MenuRecord(10, 1, MenuRecord.MENU_TYPE_USSD,
            new UssdMenuTypeValue("*138*#"));
    menu.addRecord(ussdMenu);

    //RECHARGE Voucher MENU
    MenuRecord ussdReqMenu = new MenuRecord(21, 1, MenuRecord.MENU_TYPE_USSD_REQ,
            new UssdReqMenuTypeValue(18, "*139*", "#"));
    menu.addRecord(ussdReqMenu);

    return menu;
  }

}
