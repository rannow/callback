package callback.menu;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:10:02
 */
public class MenuRecord implements RecordStructure {

  public static final int MAX_LENGTH_OF_BYTES = 204;

  public static final int MENU_TYPE_PHONE_NO = 0;
  public static final int MENU_TYPE_SMS = 1;
  public static final int MENU_TYPE_USSD = 2;
  public static final int MENU_TYPE_USSD_REQ = 3;

  private byte menuTitleLangNo;  //1 byte
  private byte menuPosition; //1 byte
  private byte menuType; //1 byte
  private byte menuTypeValueLength; //1 byte
  private MenuTypeValue menuTypeValue; //200 bytes

 byte[] menuTypeValueContent;

  public MenuRecord(int menuTitleLangNo, int menuPosition, int menuType, MenuTypeValue menuTypeValue) {
    this.menuTitleLangNo = (byte)menuTitleLangNo;
    this.menuPosition = (byte)menuPosition;
    this.menuType = (byte)menuType;
    this.menuTypeValue = menuTypeValue;
    menuTypeValueContent = this.menuTypeValue.getByteContent();
    this.menuTypeValueLength = (byte)menuTypeValueContent.length;
  }

  public byte[] getByteContent() {
    byte[] content = new byte[MAX_LENGTH_OF_BYTES];
    content[0] = menuTitleLangNo;
    content[1] = menuPosition;
    content[2] = menuType;
    content[3] = menuTypeValueLength;
    System.arraycopy(menuTypeValueContent, 0, content, 4, menuTypeValueContent.length);
    return content;
  }

}
