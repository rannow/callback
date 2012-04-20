package callback.menu;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:22:51
 */
public class UssdReqMenuTypeValue implements MenuTypeValue{

  private byte langTextNo;//1 byte
  private byte ussdStringBeginLength; //1 byte
  private byte[] ussdStringBeginContent; //x bytes
  private byte ussdStringEndLength; //1 byte
  private byte[] ussdStringEndContent; //y bytes
  // x+y <= 197 bytes

  public UssdReqMenuTypeValue(int langTextNo, String ussdStringBeginContent, String ussdStringEndContent) {
    this.langTextNo = (byte)langTextNo;
    this.ussdStringBeginLength = (byte)ussdStringBeginContent.getBytes().length;
    this.ussdStringBeginContent = ussdStringBeginContent.getBytes();
    this.ussdStringEndLength = (byte)ussdStringEndContent.getBytes().length;
    this.ussdStringEndContent = ussdStringEndContent.getBytes();

    if( (ussdStringBeginLength + ussdStringEndLength) > 197 ) {
      throw new IllegalStateException("Length of ussdStringBegin + ussdStringEnd should not exceed 197 bytes!");
    }
  }

  public byte[] getByteContent() {
    byte[] content = new byte[MAX_LENGTH_OF_BYTES];
    content[0] = langTextNo;
    content[1] = ussdStringBeginLength;
    System.arraycopy(ussdStringBeginContent, 0, content, 2, ussdStringBeginContent.length);
    content[2+ussdStringBeginContent.length] = ussdStringEndLength;
    System.arraycopy(ussdStringEndContent, 0, content, 3+ussdStringBeginContent.length, ussdStringEndContent.length);
    return content;
  }
}
