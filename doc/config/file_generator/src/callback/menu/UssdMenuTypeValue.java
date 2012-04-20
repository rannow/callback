package callback.menu;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:21:46
 */
public class UssdMenuTypeValue implements MenuTypeValue{

  private byte length; //1 byte
  private byte[] message = new byte[199]; //199 bytes

  public UssdMenuTypeValue(String message) {
    this.length = (byte)message.getBytes().length;
    System.arraycopy(message.getBytes(), 0, this.message, 0, message.getBytes().length);
  }

  public byte[] getByteContent() {
    byte[] content = new byte[MAX_LENGTH_OF_BYTES];
    content[0] = length;
    System.arraycopy(message, 0, content, 1, message.length);
    return content;
  }
}
