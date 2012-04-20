 package callback;
/**
 * This class contains utility methods for accessing texts from different
 * language files. <p/>
 *
 * Created on: 2008-11-27 <br/>
 *
 * @author Holger Rannow
 */

import libinterface.*;
import staticlibs.*;

public class CallbackLanguage {

	  private static final byte APPLET_LANGUAGE_ID = (byte) 2; // 0-8 Unique for every Applet

	  public static final short TXT_MSG_NOT_SENT 			= 3;
	  public static final short TXT_CALLING_NR 				= 4;
	  public static final short TXT_INVALID_NR 				= 5;
	  public static final short MNU_STK_MENU 				= 6;
	  public static final short MNU_DIAL 					= 7;
	  public static final short GI_ENTER_NR 				= 8;
	  public static final short MNU_PHONEBOOK 				= 9;
	  public static final short MNU_ACTIVATE_CALLBACK 		= 10;
	  public static final short MNU_DE_ACTIVATE_CALLBACK 	= 11;	  
	  public static final short MNU_UP 						= 13;
	  public static final short MNU_DOWN 					= 14;
	  public static final short TXT_WELCOME 				= 15;
	  public static final short TXT_MENU_UNAVAILABLE 		= 16;
	  public static final short TXT_PHONEBOOK_NOT_AVAILABLE = 17;
	  public static final short TXT_CALL_CONTROLL			= 18;
	  public static final short MNU_LAST_DIALED_NUMBERS 	= 19;
	  
	  /* LIST OF DEFAULT(ENGLISH) TEXT MESSAGES */
	  /** LV (txt_MsgNotSent) */
	  public static final byte[] DEFAULT_TEXT_MSG_NOT_SENT = new byte[]{
	    (byte)'M', (byte)'e', (byte)'s', (byte)'s', (byte)'a', (byte)'g', (byte)'e',
	    (byte)' ', (byte)'n', (byte)'o', (byte)'t', (byte)' ', (byte)'s', (byte)'e',
	    (byte)'n', (byte)'t'
	  }; 
	  
	  /** LV (txt_CallingNr) */
	  public static final byte[] DEFAULT_TEXT_CALLING_NO = new byte[]{
	    (byte)'C', (byte)'a', (byte)'l', (byte)'l', (byte)'i', (byte)'n', (byte)'g',
	    (byte)' ', (byte)'n', (byte)'u', (byte)'m', (byte)'b', (byte)'e', (byte)'r'
	  }; 
	  
	  /** LV (txt_InvalidNr) */
	  public static final byte[] DEFAULT_TEXT_INVALID_NO = new byte[]{
	    (byte)'I', (byte)'n', (byte)'v', (byte)'a', (byte)'l', (byte)'i', (byte)'d',
	    (byte)' ', (byte)'n', (byte)'u', (byte)'m', (byte)'b', (byte)'e', (byte)'r'
	  };
	  
	  /** LV(mnu_STKMenu) */
	  public static final byte[] DEFAULT_TEXT_MENU_STK = new byte[]{
	    (byte)'S', (byte)'T', (byte)'K', (byte)' ', (byte)'m', (byte)'e', (byte)'n', (byte)'u'
	  };
	  
	  /** LV(mnu_Dial) */
	  public static final byte[] DEFAULT_TEXT_MENU_DIAL = new byte[]{
	    (byte)'D', (byte)'i', (byte)'a', (byte)'l'
	  };
	  
	  /** LV(gi_ EnterNr) */
	  public static final byte[] DEFAULT_TEXT_ENTER_NO = new byte[]{
	    (byte)'E', (byte)'n', (byte)'t', (byte)'e', (byte)'r', (byte)' ', (byte)'n', (byte)'o', (byte)':'
	  }; 
	  	  
	  /** LV(mnu_Phonebook) */
	  public static final byte[] DEFAULT_TEXT_MENU_PHONEBOOK = new byte[]{
	    (byte)'P', (byte)'h', (byte)'o', (byte)'n', (byte)'e', (byte)'b', (byte)'o', (byte)'o', (byte)'k'
	  };
	  
	  /** LV(mnu_LastDialedNumbers) */
	  public static final byte[] DEFAULT_TEXT_LAST_DIALED_NUMBERS = new byte[]{
	    (byte)'L', (byte)'a', (byte)'s', (byte)'t', (byte)' ', (byte)'d', (byte)'i', (byte)'a', 
	    (byte)'l', (byte)'e', (byte)'d'
	  };
	  	  	  
	  /** LV(mnu_PhonebookUnavailable) */
	  public static final byte[] DEFAULT_TEXT_PHONEBOOK_NOT_AVAILABLE = new byte[]{
	    (byte)'P', (byte)'h', (byte)'o', (byte)'n', (byte)'e', (byte)'b', (byte)'o', (byte)'o',
	    (byte)'k', (byte)' ', (byte)'u', (byte)'n', (byte)'a', (byte)'v', (byte)'a', (byte)'i',
	    (byte)'l', (byte)'a', (byte)'b', (byte)'l', (byte)'e'
	  }; 
	  
	  /** LV(mnu_MenuUnavailable) */
	  public static final byte[] DEFAULT_TEXT_MENU_NOT_AVAILABLE = new byte[]{
	    (byte)'M', (byte)'e', (byte)'n', (byte)'u', (byte)' ', (byte)'u', (byte)'n', (byte)'a', 
	    (byte)'v', (byte)'a', (byte)'i', (byte)'l', (byte)'a', (byte)'b', (byte)'l', (byte)'e'
	  }; 
	  	  
	  /** LV(txt_Welcome) */
	  public static final byte[] DEFAULT_TEXT_WELCOME = new byte[]{
	    (byte)'W', (byte)'e', (byte)'l', (byte)'c', (byte)'o', (byte)'m', (byte)'e',
	  };
	  
	  /** LV(mnu_Up) */
	  public static final byte[] DEFAULT_TEXT_MENU_UP = new byte[]{
	    (byte)'U', (byte)'p'
	  };
	  
	  /** LV(mnu_Down) */
	  public static final byte[] DEFAULT_TEXT_MENU_DOWN = new byte[]{
	    (byte)'D', (byte)'o', (byte)'w', (byte)'n'
	  };

	  /** LV(mnu_MenuAkitvateCallback) */
	  public static final byte[] DEFAULT_TEXT_MNU_ACTIVATE_CALLBACK = new byte[]{
	    (byte)'A', (byte)'c', (byte)'t', (byte)'i', (byte)'v', (byte)'a', (byte)'t', (byte)' ', 
	    (byte)'C', (byte)'a', (byte)'l', (byte)'l', (byte)'b', (byte)'a', (byte)'c', (byte)'k',
	    (byte)' ', (byte)'a', (byte)'l', (byte)'w', (byte)'a', (byte)'i', (byte)'s' 
	  }; 

	  /** LV(mnu_MenuAkitvateCallback) */
	  public static final byte[] DEFAULT_TEXT_MNU_DE_ACTIVATE_CALLBACK = new byte[]{
		    (byte)'D', (byte)'e', (byte)'a', (byte)'c', (byte)'t', (byte)'i', (byte)'v', (byte)'a', (byte)'t', (byte)' ', 
		    (byte)'C', (byte)'a', (byte)'l', (byte)'l', (byte)'b', (byte)'a', (byte)'c', (byte)'k',
		    (byte)' ', (byte)'a', (byte)'l', (byte)'w', (byte)'a', (byte)'i', (byte)'s' 
	  }; 


	  /** LV(mnu_MenuAkitvateCallback) */
	  public static final byte[] DEFAULT_TXT_CALL_CONTROLL = new byte[]{
		    (byte)'W', (byte)'a', (byte)'i', (byte)'t', (byte)' ', (byte)'f', (byte)'o', (byte)'r', (byte)' ', (byte)'c', 
		    (byte)'a', (byte)'l', (byte)'l' 
	  }; 

	private boolean isDefault = true;
	private short textId = 0;
	private LanguageLibInterface lang;
	
	public CallbackLanguage()
	{
		lang = TextObject.getLanguageLibInterface();
		lang.registerAppletToMenuUpdate(APPLET_LANGUAGE_ID);
	}
			
	// --------------------- LanguageTexts Interface ------------------------------
	  /**
	   * Reads a text with the given number and stores it in the variable, it will
	   * be available to be obtained via method getText();
	   *
	   * @param textNo
	   *          text number in the ordered list stored in language file, starting from 1.
	   * @throws LanguageException
	   *           if the text with the given number could not be read from the language
	   *           file or set the setLanguage() method was not yet invoked nor there is not a default
	   *           value for given text. This exception will also be thrown if the requested text number is invalid.
	   * @see #getText()
	   * @see #setLanguage(byte[])
	   */

	public void readText(short textNo) {
		isDefault = false; 
		if (!lang.readText(textNo)) isDefault = true;    
		textId = textNo;
	 }

	 /**
	 * Returns a text for a given language.<br/> The following preconditions
	 * must be met:<br/> 1. {@link #setLanguage(byte[])} method was executed
	 * <br/> 2. {@link #readText(short)} method was executed <br/> 3. Given
	 * language file must exist <br/> 4. If the language file does not exist,
	 * then default value for the text will be returned.<br/>
	 * 
	 * @return a language text.
	 * @see #setLanguage(byte[])
	 * @see #readText(short)
	 */
public byte[] getText(byte[] textBuffer) {
	  if (isDefault) {
		  javacard.framework.Util.arrayCopy( getDefaultText(textId),
				(short) 0, textBuffer, (short) 0,
				(short) getDefaultText(textId).length);
		  return textBuffer;
	  }
	  else 
		  return TextObject.readText(lang, textBuffer);
}

/**
	 * Returns the text length in bytes. The same preconditions as for
	 * {@link #getText()} method must be met.
	 * 
	 * @return text length in bytes.
	 * @see #getText()
	 */
public short getTextLength() {
	  if (isDefault) {
		  return (short) getDefaultText(textId).length;
	  }else
      return lang.getTextLength();
}

	// ------------------------------------------- private ---------------------------------------------
	 /**
	   * Gets the default text for given number.
	   *
	   * @param textNo text number to obtain, starting from 1.
	   * @return text value.
	   * @throws LanguageException if text number is invalid (exceeded no of default lang texts = 14).
	   */
    
  private static byte[] getDefaultText(short textNo) {
	    switch (textNo) {
	      case TXT_MSG_NOT_SENT:
	        return DEFAULT_TEXT_MSG_NOT_SENT;
	      case TXT_CALLING_NR:
	        return DEFAULT_TEXT_CALLING_NO;
	      case TXT_INVALID_NR:
	        return DEFAULT_TEXT_INVALID_NO;
	      case MNU_STK_MENU:
	        return DEFAULT_TEXT_MENU_STK;
	      case MNU_DIAL:
	        return DEFAULT_TEXT_MENU_DIAL;
	      case GI_ENTER_NR:
	        return DEFAULT_TEXT_ENTER_NO;
	      case MNU_PHONEBOOK:
	        return DEFAULT_TEXT_MENU_PHONEBOOK;
	      case MNU_UP:
	        return DEFAULT_TEXT_MENU_UP;
	      case MNU_DOWN:
	        return DEFAULT_TEXT_MENU_DOWN;
	      case TXT_WELCOME:
	        return DEFAULT_TEXT_WELCOME;
	      case TXT_MENU_UNAVAILABLE:
	        return DEFAULT_TEXT_MENU_NOT_AVAILABLE;
	      case TXT_PHONEBOOK_NOT_AVAILABLE:
	        return DEFAULT_TEXT_PHONEBOOK_NOT_AVAILABLE;      
	      case MNU_LAST_DIALED_NUMBERS:
	        return DEFAULT_TEXT_LAST_DIALED_NUMBERS;
	      case MNU_ACTIVATE_CALLBACK:
		        return DEFAULT_TEXT_MNU_ACTIVATE_CALLBACK;
	      case MNU_DE_ACTIVATE_CALLBACK:
		        return DEFAULT_TEXT_MNU_DE_ACTIVATE_CALLBACK;
	      case TXT_CALL_CONTROLL:		        
		        return DEFAULT_TXT_CALL_CONTROLL;
	    }
	    return null;
	  }
}
