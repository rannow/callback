package callback.menu;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:17:34
 */
public class PhoneNoMenuTypeValue implements MenuTypeValue{

  private byte phoneNoLength; //1 byte
  private byte[] phoneAddress = new byte[20]; //20 bytes

  public PhoneNoMenuTypeValue(byte[] phoneAddress) {
    this.phoneNoLength = (byte) phoneAddress.length;
    this.phoneAddress = new byte[20];
    System.arraycopy(phoneAddress, 0, this.phoneAddress, 0, phoneAddress.length);
  }

  public byte[] getByteContent() {
    byte[] content = new byte[MAX_LENGTH_OF_BYTES];
    content[0] = phoneNoLength;
    System.arraycopy(phoneAddress, 0, content, 1, phoneAddress.length);
    return content;
  }
}
