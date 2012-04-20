package callback.menu;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:20:19
 */
public class SmsMenuTypeValue implements MenuTypeValue{

  private byte[] destinationNo = new byte[20]; //20 bytes
  private byte[] smsc = new byte[20];//20 bytes
  private byte[] message = new byte[160]; //160 bytes

  public SmsMenuTypeValue(byte[] destinationNoAddress, byte[] smscNoAddress, String message) {
    System.arraycopy(destinationNoAddress, 0, this.destinationNo, 0, destinationNoAddress.length);
    System.arraycopy(smscNoAddress, 0, this.smsc, 0, smscNoAddress.length);
    System.arraycopy(message.getBytes(), 0, this.message, 0, message.getBytes().length);
  }

  public byte[] getByteContent() {
    byte[] content = new byte[MAX_LENGTH_OF_BYTES];
    System.arraycopy(destinationNo, 0, content, 0, destinationNo.length);
    System.arraycopy(smsc, 0, content, destinationNo.length, smsc.length);
    System.arraycopy(message, 0, content, destinationNo.length + smsc.length, message.length);
    return content;
  }
}
