package callback;

/*
 * Imported packages
 */
import sim.toolkit.*;
import sim.access.*;
import staticlibs.*;
import javacard.framework.*;
import libinterface.*;

/**
 * This is a main applet installed on a UnitedMobile SIM cards.
 * 
 * <ul>
 * It defines following functions:
 * <li>Callback initiated during Call Control triggers
 * <li>Dial menu - enabling to dial a custom number
 * <li>Phonebook supporting pagination of results
 * <li>Languages menu enabling to choose the display language for the
 * application
 * </ul>
 * 
 * TODO Zweites Applet mit Call Control USSD und nicht call verbot, Test von
 * USSD by Start und Location Update (Nur bei Netzewegsel) TODO Rufnummern in
 * last Dailed Numbers aufnehmen (Refresh) TODO Last Dailed Number in Menue
 * einbauen  TODO Call throu einbauen, Nach Netz, Nach Destination
 * (Kontroliert über Liste auf SIM oder über USSD/SMS)
 * 
 * @author Holger Rannow
 * @author Artur Witek
 */
public class CallbackApplet extends javacard.framework.Applet implements
		ToolkitInterface, ToolkitConstants, LanguageInterface {

	private static final byte PROFILE_SEND_USSD = (byte) 27;
	private static final byte PROFILE_LOCATION_STATUS = (byte) 36;

	/**
	 * Holds bit index in MEProfile for checking if Mobile supports Call
	 * control.
	 */
	private static final byte PROFILE_CALL_CONTROL = (byte) 9;
	private static final byte PROFILE_MENU_SELECTION = (byte) 3;
	private static final byte PROFILE_DISPLAY_TEXT = (byte) 16;
	private static final byte PROFILE_PROVIDE_LOCAL_INFORMATION = (byte) 30;
	private static final byte PROFILE_REFRESH = (byte) 23;

	// Events
	public static final byte EVENT_LOCATION_STATUS = (byte) 36;

	private static final byte ALLOWED = (byte) 0x00;
	private static final byte NOT_ALLOWED = (byte) 0x01;
	private static final byte MODIFIED = (byte) 0x02;

	private static final byte EXIT_REQUESTED_BY_USER = (byte) 0x10;

	// address to send SMS
	// TP-MTI (01) + TP-RD(00) + TP-VPF (00) + TP-RP (00) + TP-UDHI (00) +
	// TP-SRR (00)

	private static final byte FIRST_BYTE = (byte) 0x01;

	// TP-Message-Reference
	private static final byte TP_MR = (byte) 0x00;

	// TP-Protocol-Identifier
	private static final byte TP_PID = (byte) 0x00;

	// TP-Data-Coding-Scheme (byte)0x00 7 Bit
	private static final byte TP_DCS_7BIT = (byte) 0x00;

	// (byte)0xF6 = 8Bit
	private static final byte TP_DCS_8BIT = (byte) 0xF6;

	private static final byte USSD_DEFAULT_ALPHABET = (byte) 0x0F;
	private static final byte USSD_8_BIT_DATA = (byte) 0x44;

	/** Determines max length of the menu string. */
	private static final byte MAX_MENU_LENGTH = 20;

	// Show Message by time
	private static final byte TAG_SHOW_BY_TIME = (byte) 0x00; // Show Message

	// Show Message till user action
	private static final byte TAG_SHOW_TILL_AKTION = (byte) 0x80;

	// constants for Phone books
	private static final byte ADN = (byte) 0x01;
	private static final byte LND = (byte) 0x02;

	// config FILE constants
	private static final short FID_DF_STK = (short) 0x7F9A;
	private static final short FID_EF_CONFIG = (short) 0x6FA0;
	private static final short FID_EF_IMSI_NR = (short) 0x6FB0;

	// config constants
	private static final byte BYTE_CONFIG1 = (byte) 0;
	private static final byte BYTE_CONFIG2 = (byte) 1;
	private static final byte BYTE_CONFIG3 = (byte) 2;
	private static final byte BYTE_POS_SMS_TEXT = (byte) 3;
	private static final byte BYTE_POS_SMS_DEST_NR = (byte) 7;
	private static final byte BYTE_POS_SMSC = (byte) 11;
	private static final byte BYTE_POS_USSD_START = (byte) 15;
	private static final byte BYTE_POS_USSD_END = (byte) 19;
	private static final byte BYTE_POS_USSD_DUMMY = (byte) 23;
	private static final byte BYTE_POS_MCCMNC_LIST = (byte) 27;
	private static final byte BYTE_POS_DEST_LIST = (byte) 31;
	private static final byte BYTE_POS_PREFIX_LSIT = (byte) 35;
	private static final byte BYTE_SEPERATOR_USSD = (byte) 39;
	private static final byte BYTE_SEPERATOR_SMS = (byte) 40;
	private static final byte BYTE_SHORT_CODE_LEN = (byte) 41;
	private static final byte BYTE_PHONE_NR_LEN = (byte) 42;
	private static final byte BYTE_MAX_USSD_RETRY = (byte) 43;

	// config IMSI NR
	private static final byte BYTE_IMSI_NR = (byte) 7;

	// config constants CONFIG1 Bits
	private static final byte SHOW_CALLBACK_MENU = (byte) 1;
	private static final byte SHOW_PHONEBOOK = (byte) 2;
	private static final byte SHOW_LAST_DAILED_NUMBERS = (byte) 4;
	private static final byte DISABLE_CALLBACK = (byte) 8;
	private static final byte SHOW_CALLBACK_MENU_IF_NO_CC = (byte) 16; // 1
	private static final byte SHOW_PHONEBOOK_IF_NO_CC = (byte) 32; // 2
	private static final byte SHOW_LAST_DAILED_NUMBERS_IF_NO_CC = (byte) 64; // 4
	private static final byte SHOW_WELCOME_MESSAGE = (byte) 128; // 8
	// config constants CONFIG2 Bits
	private static final byte DISABLE_USSD_CALLBACK = (byte) 1;
	private static final byte DISABLE_SMS_CALLBACK = (byte) 2;
	private static final byte DISABLE_CALLTHROU = (byte) 4;
	private static final byte SEND_IMSI_NR = (byte) 8;
	private static final byte SEND_LOCATION = (byte) 16;
	private static final byte SEND_DUMMY_USSD = (byte) 32;
	private static final byte USE_HOME_ROUTING = (byte) 64;
	private static final byte USE_NULL_ALPHA = (byte) 128;
	// config constants CONFIG3 Bits
	private static final byte USSD_8BIT = (byte) 1;
	private static final byte SMS_8BIT = (byte) 2;
	private static final byte SOW_SMS_TRIGGER_OK = (byte) 4;
	private static final byte SOW_SMS_TRIGGER_ERROR = (byte) 8;
	private static final byte USE_SS = (byte) 16;
	private static final byte SHOW_CALLBACK_ACTIVE_MENU = (byte) 32;
	private static final byte USE_ALPHA_TEXT = (byte) 64;
	private static final byte SEND_CHANGE_TO_USSD = (byte) 128;

	private static final byte[] NULL_BYTE_ARRAY = {};
	private static final byte[] DEFAULT_MENU_TEXT = { (byte) 'I', (byte) 't', (byte) 'e', (byte) 'm'};
	private static final byte[] LND_FILE = { (byte) 0x3F, (byte) 0x00, (byte) 0x71, (byte) 0x00, (byte) 0x6F, (byte) 0x44,};

	private static CallbackLanguage lang;

	/* Applet tools */
	private ToolkitRegistry reg;

	/** A buffer for storing length */
	private short length;
	/** A buffer for storing action results */
	private byte result;

	// Temporary buffer for storing intermediate data and results
	private byte[] tempBuffer;
	private byte[] tempBuffer2;
	private final byte[] inAddress = new byte[20];
	private final byte[] ussd = new byte[40];
	private final byte[] tpduSubmit = new byte[190];
	private byte tpduSubmitLength;

	// Main Menu - menu items titles and their id's
	private byte idMenuCallback;
	private byte idMenuPhonebook;
	private byte idMenuDialedNumbers;
	private byte idMenuAcivateCallback;

	/** Buffer for the FCI (File Control Information) */
	private final byte[] EFFciBuffer = new byte[22];

	/** SIM file access interface */
	private SIMView gsmFile;

	/** Defines how many phonebook entries should be shown on a single page. */
	private static final byte MAX_PHONEBOOK_ITEMS_PER_PAGE = 10;
	
	/** Holds the number of current phonebook page. */
	private byte phoneBookPageNo;

	/** Holds the number of pages to be considered while displaying phonebook. */
	private byte phoneBookPages;

	/**
	 * Constructor of the applet
	 */
	public CallbackApplet() {
		// Get the reference of the applet ToolkitRegistry object
		reg = ToolkitRegistry.getEntry();
		tempBuffer = JCSystem.makeTransientByteArray((short) 255,
				JCSystem.CLEAR_ON_RESET);
		tempBuffer2 = JCSystem.makeTransientByteArray((short) 255,
				JCSystem.CLEAR_ON_RESET);

		// Define the Profile Download event that trigger the applet
		reg.setEvent(EVENT_PROFILE_DOWNLOAD);
		// Define the Call Control event that trigger the applet
		reg.setEvent(EVENT_CALL_CONTROL_BY_SIM);

		gsmFile = SIMSystem.getTheSIMView();

		// Initialize menu
		idMenuCallback = reg.initMenuEntry(DEFAULT_MENU_TEXT, (short) 0,
				(short) DEFAULT_MENU_TEXT.length, PRO_CMD_SELECT_ITEM, false, (byte) 0,
				(short) 0);

		idMenuPhonebook = reg.initMenuEntry(DEFAULT_MENU_TEXT,
				(short) 0, (short) DEFAULT_MENU_TEXT.length, PRO_CMD_SELECT_ITEM, false,
				(byte) 0, (short) 0);

		idMenuDialedNumbers = reg.initMenuEntry(DEFAULT_MENU_TEXT,
				(short) 0, (short) DEFAULT_MENU_TEXT.length, PRO_CMD_SELECT_ITEM, false,
				(byte) 0, (short) 0);	
		
		idMenuAcivateCallback = reg.initMenuEntry(DEFAULT_MENU_TEXT,
				(short) 0, (short) DEFAULT_MENU_TEXT.length, PRO_CMD_SELECT_ITEM, false,
				(byte) 0, (short) 0);	
	}

	/**
     * Method called by the JCRE, to get Shareable Interface
     */

    public Shareable getShareableInterfaceObject (AID client_aid, byte
    		parameter) {
    	// get Interface back
    	return this;
    }

	/**
	 * updates the menu.
	 * 
	 */
	public void updateMenu() {
		// INITIALIZE DIAL MENU
		lang.readText(CallbackLanguage.MNU_DIAL);
		reg.changeMenuEntry(idMenuCallback, lang.getText(tempBuffer),
				(short) 0, lang.getTextLength(), PRO_CMD_SELECT_ITEM, false,
				(byte) 0, (short) 0);

		// INITIALIZE PHONEBOOK MENU
		lang.readText(CallbackLanguage.MNU_PHONEBOOK);
		reg.changeMenuEntry(idMenuPhonebook, lang.getText(tempBuffer),
				(short) 0, lang.getTextLength(), (byte) 0, false, (byte) 0,
				(short) 0);

		// INITIALIZE LAST DIALED NUMBERS MENU
		lang.readText(CallbackLanguage.MNU_LAST_DIALED_NUMBERS);
		reg.changeMenuEntry(idMenuDialedNumbers, lang.getText(tempBuffer),
				(short) 0, lang.getTextLength(), (byte) 0, false, (byte) 0,
				(short) 0);

		// INITIALIZE ACTIVATE CALLBACK MENU
		if ((getConfig(BYTE_CONFIG2) & DISABLE_CALLTHROU) == DISABLE_CALLTHROU) {
			lang.readText(CallbackLanguage.MNU_DE_ACTIVATE_CALLBACK);
		} 
		else 
		{
			lang.readText(CallbackLanguage.MNU_ACTIVATE_CALLBACK);			
		};
		reg.changeMenuEntry(idMenuAcivateCallback, lang.getText(tempBuffer),
				(short) 0, lang.getTextLength(), (byte) 0, false, (byte) 0,
				(short) 0);

		if (MEProfile.check(PROFILE_MENU_SELECTION)
				&& (((getConfig(BYTE_CONFIG1) & SHOW_CALLBACK_MENU) == SHOW_CALLBACK_MENU) || (!MEProfile
						.check(PROFILE_CALL_CONTROL) && ((getConfig(BYTE_CONFIG1) & SHOW_CALLBACK_MENU_IF_NO_CC) == SHOW_CALLBACK_MENU_IF_NO_CC))))
			// deactivate callback menu if special flag is set in menu config
			reg.enableMenuEntry(idMenuCallback);
		else
			reg.disableMenuEntry(idMenuCallback);

		if (MEProfile.check(PROFILE_MENU_SELECTION)
				&& (((getConfig(BYTE_CONFIG1) & SHOW_PHONEBOOK) == SHOW_PHONEBOOK) || (!MEProfile
						.check(PROFILE_CALL_CONTROL) && ((getConfig(BYTE_CONFIG1) & SHOW_PHONEBOOK_IF_NO_CC) == SHOW_PHONEBOOK_IF_NO_CC))))
			// deactivate callback menu if special flag is set in menu config
			reg.enableMenuEntry(idMenuPhonebook);
		else
			reg.disableMenuEntry(idMenuPhonebook);

		if (MEProfile.check(PROFILE_MENU_SELECTION)
				&& (((getConfig(BYTE_CONFIG1) & SHOW_LAST_DAILED_NUMBERS) == SHOW_LAST_DAILED_NUMBERS) || (!MEProfile
						.check(PROFILE_CALL_CONTROL) && ((getConfig(BYTE_CONFIG1) & SHOW_LAST_DAILED_NUMBERS_IF_NO_CC) == SHOW_LAST_DAILED_NUMBERS_IF_NO_CC))))
			// deactivate callback menu if special flag is set in menu config
			reg.enableMenuEntry(idMenuDialedNumbers);
		else
			reg.disableMenuEntry(idMenuDialedNumbers);
		
		if (MEProfile.check(PROFILE_MENU_SELECTION)
				&& (((getConfig(BYTE_CONFIG3) & SHOW_CALLBACK_ACTIVE_MENU) == SHOW_CALLBACK_ACTIVE_MENU) ))
			// deactivate callback menu if special flag is set in menu config
			reg.enableMenuEntry(idMenuAcivateCallback);
		else
			reg.disableMenuEntry(idMenuAcivateCallback);
}

	/**
	 * 
	 * Method called by the JCRE at the installation of the applet
	 * 
	 * @param bArray
	 *            the byte array containing the AID bytes
	 * @param bOffset
	 *            the start of AID bytes in bArray
	 * @param bLength
	 *            the length of the AID bytes in bArray
	 */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// Create the Java SIM toolkit applet
		CallbackApplet appletInstance = new CallbackApplet();
		// Register this applet
		appletInstance.register(bArray, (short) (bOffset + 1),
				(byte) bArray[bOffset]);
		
		lang = new CallbackLanguage();
	}

	/**
	 * Method called by the SIM Toolkit Framework
	 * 
	 * @param event
	 *            the byte representation of the event triggered
	 */
	public void processToolkit(byte event) {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();

		// Manage the request following the menu selection event type
		if (event == EVENT_MENU_SELECTION) {
			onMenuSelection(proHdlr);
		}

		// Manage the request following the profile download event type
		if (event == EVENT_PROFILE_DOWNLOAD) {
			onProfileDownload(proHdlr);
		}

		// Manage the request following the Call Control event type
		if (event == EVENT_CALL_CONTROL_BY_SIM) {
			callControlService(proHdlr);
		}
	}

	/**
	 * Called on EVENT_MENU_SELECTION event.
	 * 
	 * @throws ToolkitException
	 *             in case of an error during accesing item identifier.
	 */
	private void onMenuSelection(ProactiveHandler proHdlr)
			throws ToolkitException {
		EnvelopeHandler envHdlr = EnvelopeHandler.getTheHandler();

		// Get the selected item
		byte selectedItemId = envHdlr.getItemIdentifier();

		// Perform the required service following the Menu1 selected item
		if (selectedItemId == idMenuCallback) {
			menuActionCallback(proHdlr);
		}
		if (selectedItemId == idMenuPhonebook) {
			menuActionPhonebook(proHdlr);
		}
		if (selectedItemId == idMenuDialedNumbers) {
			menuActionDialedNumbers(proHdlr);
		}
		if (selectedItemId == idMenuAcivateCallback) {
			menuActionActivateCallback(proHdlr);
		}
	}

	/**
	 * Executed on PROFILE_DOWNLOAD event.
	 */
	private void onProfileDownload(ProactiveHandler proHdlr) {

		updateMenu();
		if ((getConfig(BYTE_CONFIG1) & DISABLE_CALLBACK) == DISABLE_CALLBACK)
			reg.clearEvent(EVENT_CALL_CONTROL_BY_SIM);
		else
			reg.setEvent(EVENT_CALL_CONTROL_BY_SIM);

		// display welcome message
		if (MEProfile.check(PROFILE_DISPLAY_TEXT)
				&& ((getConfig(BYTE_CONFIG1) & SHOW_WELCOME_MESSAGE) == SHOW_WELCOME_MESSAGE)) 
		{
			lang.readText(CallbackLanguage.TXT_WELCOME);
			proHdlr.initDisplayText((byte) TAG_SHOW_TILL_AKTION,
					DCS_8_BIT_DATA, lang.getText(tempBuffer), (short) 0, lang
							.getTextLength());
			proHdlr.send();
		}		
	}

	/**
	 * Manage the Menu Callback selection
	 */
	private void menuActionCallback(ProactiveHandler proHdlr) {
		// get language text that will be displayed for user before input data
		lang.readText(CallbackLanguage.GI_ENTER_NR);
		// ask user for custom input
		proHdlr.initGetInput((byte) 0x00, DCS_8_BIT_DATA, lang
				.getText(tempBuffer), (byte) 0, lang.getTextLength(),
				(short) 1, (short) 20);

		result = proHdlr.send(); // get input data
		ProactiveResponseHandler rspHdlr = ProactiveResponseHandler
				.getTheHandler();

		tempBuffer[0] = (byte) rspHdlr.getTextStringLength();
		rspHdlr.copyTextString(tempBuffer, (short) 1);

		// convert string to address
		Utils.convertTextToAddress(tempBuffer, inAddress);
		sendCallbackInfo(proHdlr, inAddress);
	}

	/**
	 * Manage the Menu ADN Phonebook selection.
	 * 
	 * @param pageNo
	 *            phonebook page number to initialize the menu with.
	 */

	private void menuActionPhonebook(ProactiveHandler proHdlr) {
		selectPhonebook(proHdlr, (byte) 1, ADN);
	}

	/**
	 * Manage the Menu Last dialed numbers selection
	 */
	private void menuActionDialedNumbers(ProactiveHandler proHdlr) {
		selectPhonebook(proHdlr, (byte) 1, LND);
	}

	/**
	 * Manage the Menu Activate Callback selection
	 */
	private void menuActionActivateCallback(ProactiveHandler proHdlr) {
		byte value = getConfig(BYTE_CONFIG2);
		if ((value & DISABLE_CALLTHROU) == DISABLE_CALLTHROU) {
			setConfig(BYTE_CONFIG2,  (byte) (value - DISABLE_CALLTHROU));
		} else {
			setConfig(BYTE_CONFIG2,  (byte) (value + DISABLE_CALLTHROU));
		}  
		updateMenu();
	}
	
	/**
	 * Manage the Menu Phonebook selection.
	 * 
	 * NOTE: THIS METHOD IS INVOKED RECURSIVELY ON MENU SUB-SELECTION.
	 * 
	 * @param pageNo
	 *            phonebook page number to initialize the menu with.
	 */

	private void selectPhonebook(ProactiveHandler proHdlr, byte pageNo, byte book) {
		// reset pagination settings
		phoneBookPageNo = pageNo;
		phoneBookPages = -1;

		// Prepare the Phonebook STK Menu
		if (book == ADN) {
			lang.readText(CallbackLanguage.MNU_PHONEBOOK);			
		}else {
			lang.readText(CallbackLanguage.MNU_LAST_DIALED_NUMBERS);
		}
		proHdlr.init(PRO_CMD_SELECT_ITEM, (byte) 0x00,
				(byte) ToolkitConstants.DEV_ID_ME);
		proHdlr.appendTLV((byte) (TAG_ALPHA_IDENTIFIER), lang
				.getText(tempBuffer), (short) 0, lang.getTextLength());

		if (book == ADN) {
			// open the ADN file, the EFFciBuffer will be initialized
			prepareADNFile();
		}else {
			// open the LDN file, the EFFciBuffer will be initialized
			prepareLNDFile();
		}

		// Get the number of records and the length of each record
		short recCount = FileAccessLibrary
				.getLinearFileRecordNumber(EFFciBuffer);

		// prepare pagination data
		phoneBookPages = (byte) (recCount / MAX_PHONEBOOK_ITEMS_PER_PAGE);
		if ((phoneBookPages * MAX_PHONEBOOK_ITEMS_PER_PAGE) > recCount) {
			phoneBookPages = (byte) (phoneBookPages + 1);
		}

		// get the ADN EF record length
		short recLength = FileAccessLibrary
				.getLinearFileRecordLength(EFFciBuffer);

		// Reset buffer
		Utils.resetBuffer(tempBuffer, (byte) 0xFF);

		// Append sub menu item for each record that not empty
		short lastRecordIndex = 0;
		short pageEndIdx = (short) (phoneBookPageNo * MAX_PHONEBOOK_ITEMS_PER_PAGE);
		short pageStartIdx = (short) (pageEndIdx - MAX_PHONEBOOK_ITEMS_PER_PAGE + 1);

		// append the "UP" navigation item
		if (phoneBookPageNo > 1) {
			lang.readText(CallbackLanguage.MNU_UP);
			proHdlr.appendTLV((byte) (TAG_ITEM), (byte) (lastRecordIndex + 1),
					lang.getText(tempBuffer), (short) 0, lang.getTextLength());
			lastRecordIndex = (short) (lastRecordIndex + 1);
			/*
			 * language library operates on files and sometimes can change file
			 * selection, so we have to make sure that ADN file is opened.
			 */
			if (book == ADN) {
				// open the ADN file, the EFFciBuffer will be initialized
				prepareADNFile();
			}else {
				// open the LDN file, the EFFciBuffer will be initialized
				prepareLNDFile();
			}
		}

		// append the ADN entries
		short recordIdx = pageStartIdx;
		byte recordsAdded = 0;

		while ((recordsAdded < MAX_PHONEBOOK_ITEMS_PER_PAGE) && (recordIdx < recCount)) {
			try {
				FileAccessLibrary.readRecord(gsmFile, recordIdx, tempBuffer,
						(short) recLength);
			} catch (SIMViewException e) {
				if (e.getReason() == SIMViewException.RECORD_NUMBER_NOT_AVAILABLE) {
					break; // finish file scanning at this point - there is no
					// more records
				} else {
					throw e;
				}
			}
			// if record is not empty, then add the menu entry
			if (!FileAccessLibrary.isEmptyRecord(tempBuffer, (short) recLength)) {
				Utils.resetBuffer(tempBuffer2);
				short nameLength = FileAccessLibrary.getNameFromAdnRecord(
						tempBuffer, recLength, tempBuffer2, tpduSubmit); // tpduSubmit = Used as tempBuffer
				proHdlr.appendTLV((byte) (TAG_ITEM), (byte) recordIdx,
						tempBuffer2, (short) 0x0000, (short) nameLength);
				lastRecordIndex = recordIdx;
				recordsAdded += 1;
			}
			recordIdx += 1;
		}

		// append the "DOWN" navigation item
		if (phoneBookPageNo < phoneBookPages
				&& recordsAdded == MAX_PHONEBOOK_ITEMS_PER_PAGE) {
			lang.readText(CallbackLanguage.MNU_DOWN);
			proHdlr.appendTLV((byte) (TAG_ITEM), (byte) (lastRecordIndex + 1),
					lang.getText(tempBuffer), (short) 0, lang.getTextLength());
		}

		// select the MF to avoid locking the EF ADN
		FileAccessLibrary.selectMF(gsmFile);

		// send the proactive command
		result = proHdlr.send();

		if (result == RES_CMD_PERF) {
			// SelectItem response handling
			ProactiveResponseHandler rspHdlr = ProactiveResponseHandler
					.getTheHandler();

			// get the user selection in sub-menu
			byte selItemId = rspHdlr.getItemIdentifier();

			// check the selection
			if (phoneBookPageNo > 1 && selItemId == (byte) 1) {
				// first menu option selected (UP)
				selectPhonebook(proHdlr, (byte) (phoneBookPageNo - 1), book);
			} else if (selItemId == (byte) (lastRecordIndex + 1)) {
				// last menu option selected (DOWN)
				selectPhonebook(proHdlr, (byte) (phoneBookPageNo + 1), book);
			} else {
				if (book == ADN) {
					// open the ADN file, the EFFciBuffer will be initialized
					prepareADNFile();
				}else {
					// open the LDN file, the EFFciBuffer will be initialized
					prepareLNDFile();
				}
				// Reset buffer
				Utils.resetBuffer(tempBuffer, (byte) 0x00);
				// Read ADN record
				FileAccessLibrary.readRecord(gsmFile, selItemId, tempBuffer,
						(short) recLength);
				// set up call
				setupCallByAdnRecord(proHdlr, tempBuffer, recLength);
				// Select MF
				FileAccessLibrary.selectMF(gsmFile);
			}
		}
		return;
	}

	/**
	 * Opens the ADN EF, prepares the file info - stored in EFFciBuffer.
	 */
	private void prepareADNFile() {
		// Reset the FCI Buffer
		Utils.resetBuffer(EFFciBuffer, (byte) 0x00);
		// Select ADN file and store ADN file properties in the FCI Buffer
		FileAccessLibrary.selectDF_telecom(gsmFile);
		FileAccessLibrary.selectEF(gsmFile, FileAccessLibrary.FID_EF_ADN,
				EFFciBuffer);
	}

	/**
	 * Opens the LND EF, prepares the file info - stored in EFFciBuffer.
	 */
	private void prepareLNDFile() {
		// Reset the FCI Buffer
		Utils.resetBuffer(EFFciBuffer, (byte) 0x00);
		// Select ADN file and store ADN file properties in the FCI Buffer
		FileAccessLibrary.selectDF_telecom(gsmFile);
		FileAccessLibrary.selectEF(gsmFile, FileAccessLibrary.FID_EF_LND,
				EFFciBuffer);
	}

	/**
	 * Set up a call according to the ADN record
	 * 
	 * @param proHdlr
	 *            the proactive command handler
	 * @param recData
	 *            the ADN record data
	 * @param recLength
	 *            the length of ADN record
	 */
	private void setupCallByAdnRecord(ProactiveHandler proHdlr, byte[] recData,
			short recLength) {

		// append the address
		short len = FileAccessLibrary.getNumberFromAdnRecord(recData, recLength, inAddress);
		inAddress[0] = (byte) (len - (byte) 1);
		// send it
		sendCallbackInfo(proHdlr, inAddress);
	}

	/**
	 * Method called by the JCRE, once selected
	 * 
	 * @param apdu
	 *            the incoming APDU object
	 */
	public void process(APDU apdu) {
		// ignore the applet select command dispached to the process
		if (selectingApplet()) {
			return;
		}
	}

	/**
	 * Method illustrating the use of the Call Control event.
	 */
	private void callControlService(ProactiveHandler proHdlr) {
		EnvelopeHandler envHdlr = EnvelopeHandler.getTheHandler();
		EnvelopeResponseHandler envRspHdlr = EnvelopeResponseHandler
				.getTheHandler();

		// If the current MCC/MNC is call through or it is SS or USSD
		if (((getConfig(BYTE_CONFIG1) & DISABLE_CALLBACK) == DISABLE_CALLBACK)
				|| (envHdlr.findTLV(TAG_ADDRESS, (byte) 1) == TLV_NOT_FOUND)) {
			envRspHdlr.postAsBERTLV(SW1_RP_ACK, ALLOWED);
			return;
		}
		// Clear the temporary data buffer
		Utils.resetBuffer(inAddress);

		// Get the length of the dailled Number
		length = envHdlr.getValueLength();

		// save dailed Number in inAddress
		inAddress[0] = (byte) length;
		envHdlr.copyValue((short) 0, inAddress, (short) 1, length);

		if (checkCallThrou(inAddress, envHdlr)) {
			// Allow the call with no modification
			envRspHdlr.postAsBERTLV(SW1_RP_ACK, ALLOWED);
			return;
		} else {
			if ((getConfig(BYTE_CONFIG2) & USE_NULL_ALPHA ) == USE_NULL_ALPHA) envRspHdlr.appendTLV(TAG_ALPHA_IDENTIFIER, NULL_BYTE_ARRAY, (short)0, (short) 0);// Alpher Identifier length = 00
			if ((getConfig(BYTE_CONFIG3) & USE_ALPHA_TEXT ) == USE_ALPHA_TEXT) 
			{
				lang.readText(CallbackLanguage.TXT_CALL_CONTROLL);
				envRspHdlr.appendTLV(TAG_ALPHA_IDENTIFIER, lang.getText(tempBuffer2), (short) 0, lang.getTextLength());
			}
			if ((getConfig(BYTE_CONFIG2) & USE_HOME_ROUTING ) == USE_HOME_ROUTING){
				// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				// TODO change to Home Routing
				// modify to home Routing Number
				// getHomeroutingnumber(tempBuffer);
				envRspHdlr.appendTLV(TAG_ADDRESS, tempBuffer, (short)1, (short)tempBuffer[0]);// Home Routing Address
				envRspHdlr.postAsBERTLV((byte) 0x9F, MODIFIED);
				sendCallbackInfo(proHdlr, inAddress);			
			} else if ((getConfig(BYTE_CONFIG2) & SEND_DUMMY_USSD ) == SEND_DUMMY_USSD)
			{
				// modify to dummy USSD Str.
				getConfigString(BYTE_POS_USSD_DUMMY, ussd);
				Util.arrayCopy(ussd, (short) 2, tempBuffer, (short) 1, Util.getShort(ussd, (short) 0));
				tempBuffer[0] = (byte) (Util.getShort(ussd, (short) 0));
				Util.arrayCopy(tempBuffer, (short) 0, ussd, (short) 0, (short)(tempBuffer[0] + (short) 1));
				Utils.convertTextToAddress(ussd, tempBuffer);
				Util.arrayCopy(tempBuffer, (short) 0, ussd, (short) 0,(short)(tempBuffer[0] + (short) 1));
				envRspHdlr.appendTLV((byte) (TAG_SS_STRING | TAG_SET_CR), ussd, (short)1, (short) ussd[0]);// change to USSD Dummy Message
				envRspHdlr.postAsBERTLV((byte) 0x9F, MODIFIED);
				sendCallbackInfo(proHdlr, inAddress);							
			} else if ((getConfig(BYTE_CONFIG3) & SEND_CHANGE_TO_USSD ) == SEND_CHANGE_TO_USSD)
			{
				if (!checkNumber(proHdlr, inAddress, tempBuffer)) return;
				// modify to USSD Str.
				generateUssd(proHdlr, tempBuffer, ussd);
				tempBuffer[0] = (byte) (ussd[0] - (byte) 1);
				Util.arrayCopy(ussd, (short) 2, tempBuffer, (short) 1,(short)(ussd[0] - (short) 1));
				Utils.convertTextToAddress(tempBuffer, ussd);
				envRspHdlr.appendTLV((byte) (TAG_SS_STRING | TAG_SET_CR), ussd, (short)1, (short) ussd[0]);// change to USSD Message
				envRspHdlr.postAsBERTLV((byte) 0x9F, MODIFIED);
			} else
			{
				envRspHdlr.postAsBERTLV((byte) 0x9F, NOT_ALLOWED);
				//saveAddressInLastDailedNumbers(proHdlr, inAddress);
				sendCallbackInfo(proHdlr, inAddress);			
			};
				
			return;
		}
	}

	/**
	 * check Number
	 */
	private boolean checkNumber(ProactiveHandler proHdlr, byte[] inAddress, byte[] outAddress) {

		Utils.resetBuffer(outAddress);
		//  + is set to 00
		Utils.convertAddressToText(inAddress, outAddress);

		// Internationales Format (spezielle Vorwahlen z.b. aus USA)

		// ** Prüfen auf short Number, min < lenth < max, started ohne +
		// ** 00 löschen
		// ** püfen auf min länge
		 if (!((outAddress[0] <= getConfig(BYTE_SHORT_CODE_LEN)) || ((outAddress[0] >= getConfig(BYTE_PHONE_NR_LEN)) && isInternationalNumber(outAddress))))
		 { 
			lang.readText(CallbackLanguage.TXT_INVALID_NR);
		  	proHdlr.initDisplayText(TAG_SHOW_TILL_AKTION, DCS_8_BIT_DATA,
		  			lang.getText(tempBuffer2), (short) 0, lang.getTextLength());
		 	proHdlr.send(); 
		 	return false; 
		 } else
		 {
			return true; 
		 }
	}

	/**
	 * generate USSD Message
	 */
	private void generateUssd(ProactiveHandler proHdlr, byte[] inAddress, byte[] ussdMessage) {
		byte sep = getConfig(BYTE_SEPERATOR_USSD);

		// generate USSD Message
		// Clear the temporary data buffer
		Utils.resetBuffer(ussdMessage);
		short ussdLength = 1;
		getUssdStart(tempBuffer2);
		ussdLength += Util.getShort(tempBuffer2, (short)0);
		Util.arrayCopy(tempBuffer2, (short) 2, ussdMessage, (short) 1,
				Util.getShort(tempBuffer2, (short) 0));
		Util.arrayCopy(inAddress, (short) 1, ussdMessage, (short) ussdLength,
				(short) inAddress[0]);
		ussdLength += (short) inAddress[0];
		if ((getConfig(BYTE_CONFIG2) & SEND_IMSI_NR ) == SEND_IMSI_NR)
		{
			if (sep > (byte) 0) ussdMessage[ussdLength++] = sep;
			ussdMessage[ussdLength++] = getImsiNr();
		}
		if ((getConfig(BYTE_CONFIG2) & SEND_LOCATION ) == SEND_LOCATION)
		{
			if (sep > (byte) 0) ussdMessage[ussdLength++] = sep;
			getLocation(proHdlr, tempBuffer2);
			Util.arrayCopy(tempBuffer2, (short) 1, ussdMessage, ussdLength, tempBuffer2[0]);
			ussdLength += tempBuffer2[0];
		}
		getUssdEnd(tempBuffer2);
		Util.arrayCopy(tempBuffer2, (short) 2, ussdMessage, (short) ussdLength,
				Util.getShort(tempBuffer2, (short) 0));
		ussdLength += Util.getShort(tempBuffer2, (short) 0);
		Utils.resetBuffer(tempBuffer2);
		ussdMessage[0] = (byte) (ussdLength - (byte) 1);
		if (((getConfig(BYTE_CONFIG3) & USSD_8BIT ) == USSD_8BIT) | ((getConfig(BYTE_CONFIG3) & USE_SS ) == USE_SS) | ((getConfig(BYTE_CONFIG3) & SEND_CHANGE_TO_USSD ) == SEND_CHANGE_TO_USSD)) {
			ussdMessage[0] = USSD_8_BIT_DATA;
			Util.arrayCopy(ussdMessage, (short) 0, tempBuffer2, (short) 0, ussdLength);
		} else {
			Utils.convertText8bitTo7bit(ussdMessage, tempBuffer2);
			ussdLength = (short) ((short) tempBuffer2[0] + (byte) 1 );
			tempBuffer2[0] = USSD_DEFAULT_ALPHABET;
			Utils.resetBuffer(ussdMessage);
		};
		Util.arrayCopy(tempBuffer2, (short) 0, ussdMessage, (short) 1, ussdLength);
		ussdMessage[0] = (byte) (ussdLength) ;
	}
	
	/**
	 * send SMS Callback Trigger
	 */
	private boolean sendSmsCallabckTrigger(ProactiveHandler proHdlr, byte[] inAddress) {
		
		if (((getConfig(BYTE_CONFIG2) & DISABLE_SMS_CALLBACK) == DISABLE_SMS_CALLBACK)) return false;
		
		// send Triger by SMS
		tpduSubmit[0] = FIRST_BYTE;
		tpduSubmit[1] = TP_MR; 

		byte sep = getConfig(BYTE_SEPERATOR_SMS);
		getConfigString(BYTE_POS_SMS_DEST_NR,tempBuffer2);
		boolean useDesNr = false;
		byte destNumberLength;
		if (Util.getShort(tempBuffer2, (short)0) > (short) 0 ){
			useDesNr = true;
			Util.arrayCopy(tempBuffer2, (short) 2, tpduSubmit, (short) 2, Util.getShort(tempBuffer2, (short)0));
			destNumberLength = (byte) Util.getShort(tempBuffer2, (short)0);
		} else 
		{
			tpduSubmit[2] = (byte) (((byte) inAddress[0]) * (byte) 2 - (byte) 2);
			if ((byte) (inAddress[inAddress[0]] & (byte) 0xF0) == (byte) 0xF0)
			--tpduSubmit[2];

			inAddress[1] = (byte) 0x81; // TON ISDN Unknown
			for (byte i = 0; i < inAddress[0]; i++) {
				tpduSubmit[(byte) (i + 3)] = inAddress[(byte) (i + (byte) 1)];
			}
			destNumberLength = (byte) (inAddress[0] + (byte) 1);
		}

		tpduSubmit[(byte) (destNumberLength + 2)] = TP_PID;
		byte packing;
		if ((getConfig(BYTE_CONFIG3) & SMS_8BIT ) == SMS_8BIT)  {
			packing = (byte) 0x00;
			tpduSubmit[(byte) (destNumberLength + 3)] = TP_DCS_8BIT;
		} else {
			//packing = (byte) 0x01;
			packing = (byte) 0x00;
			tpduSubmit[(byte) (destNumberLength + 3)] = TP_DCS_7BIT;
		};
		tpduSubmitLength = (byte) (destNumberLength + 4);
		byte udl_byte = tpduSubmitLength;
		tpduSubmitLength++;
		byte messsageLength = (byte) 1;
		
		// send triger Message
		// add Message
		///--------------------------------------------------------------------------
		getSmsText(tempBuffer2);
		if (Util.getShort(tempBuffer2, (short) 0) > (short) 0)
		{
			Util.arrayCopy(tempBuffer2, (short) 2, tempBuffer, messsageLength, Util.getShort(tempBuffer2, (short) 0));
			messsageLength += (byte) Util.getShort(tempBuffer2, (short) 0);
		}
		// copy Called Number in SMS Text if Dest Number is used
		if (useDesNr)
		{
			Utils.convertAddressToText(inAddress, tempBuffer2);
			Util.arrayCopy(tempBuffer2, (short) 0, inAddress, (short) 0, (short) (tempBuffer2[0] + (short) 1));
			
			isInternationalNumber(inAddress);
			Util.arrayCopy(inAddress, (short) 1, tempBuffer, messsageLength, (short) inAddress[0]);
			messsageLength += inAddress[0];
			if (sep > (byte) 0) tempBuffer[++messsageLength] = sep;
		}
		
		if ((getConfig(BYTE_CONFIG2) & SEND_IMSI_NR ) == SEND_IMSI_NR)
		{
			tempBuffer[++messsageLength] = getImsiNr();
			if (sep > (byte) 0) tempBuffer[++messsageLength] = sep;
		}

		if ((getConfig(BYTE_CONFIG2) & SEND_LOCATION ) == SEND_LOCATION)
		{
			getLocation(proHdlr, tempBuffer2);
			Util.arrayCopy(tempBuffer2, (short) 1, tempBuffer, messsageLength, (short) tempBuffer2[0]);
			messsageLength += tempBuffer2[0];
		}
		tempBuffer[0] = (byte) (messsageLength - (byte) 1);
		if ((getConfig(BYTE_CONFIG3) & SMS_8BIT ) == SMS_8BIT)  {
			Util.arrayCopy(tempBuffer, (short) 1, tpduSubmit, (short) (udl_byte + (short) 1), (short) tempBuffer[0]);			
			tpduSubmitLength += tempBuffer[0];
		} else {
			Utils.convertText8bitTo7bit(tempBuffer, tempBuffer2);
			Util.arrayCopy(tempBuffer2, (short) 1, tpduSubmit, (short) (udl_byte + (short) 1), (short) tempBuffer2[0]);			
			tpduSubmitLength += tempBuffer2[0];
		}
		
		// ------------------------------------------------------------------------
		// save UDL
		tpduSubmit[udl_byte] = (byte) (messsageLength - (byte) 1); //(byte) (tpduSubmitLength - udl_byte);

		// Initialize Send short message command with "SMS packing by the ME
		// required" qualifier = 0x01
		proHdlr.init(PRO_CMD_SEND_SHORT_MESSAGE, packing,
				DEV_ID_NETWORK);

		// Append optional "Alpha identifier" to the Send short message
		// command
		// lang.readText(CallbackLanguage.TXT_CALLING_NR);
		// proHdlr.appendTLV((byte) (TAG_ALPHA_IDENTIFIER),
		// lang.getText(tempBuffer),
		// (short) 0x0000, lang.getTextLength());
		
		getConfigString(BYTE_POS_SMSC, tempBuffer2);
		if (Util.getShort(tempBuffer2, (short)0) > (short) 0) proHdlr.appendTLV(TAG_ADDRESS, tempBuffer2, (short) 0x0002, (short)
				Util.getShort(tempBuffer2, (short)0));
		proHdlr.appendTLV(TAG_SMS_TPDU, tpduSubmit, (short) 0,
				(short) tpduSubmitLength);

		if (proHdlr.send() == RES_CMD_PERF) {
			if ((getConfig(BYTE_CONFIG3) & SOW_SMS_TRIGGER_OK ) == SOW_SMS_TRIGGER_OK){
				// write OK on Display
				lang.readText(CallbackLanguage.TXT_CALLING_NR);
				proHdlr.initDisplayText(TAG_SHOW_BY_TIME, DCS_8_BIT_DATA, lang
						.getText(tempBuffer), (short) 0, lang.getTextLength());
				proHdlr.send();					
			};
			return true;
		} else {
			if ((getConfig(BYTE_CONFIG3) & SOW_SMS_TRIGGER_ERROR ) == SOW_SMS_TRIGGER_ERROR){
				// write error on Display
				lang.readText(CallbackLanguage.TXT_MSG_NOT_SENT);
				proHdlr.initDisplayText(TAG_SHOW_TILL_AKTION, DCS_8_BIT_DATA,
					lang.getText(tempBuffer), (short) 0, lang
							.getTextLength());
				proHdlr.send();
			};
			return false;
		}
	}

	/**
	 * sendsCallabackInfo
	 */
	private void sendCallbackInfo(ProactiveHandler proHdlr, byte[] inAddress) {
		ProactiveResponseHandler rspHdlr;

		byte [] address = tempBuffer;
		
		if (!checkNumber(proHdlr, inAddress, address)) return;

		if (!((getConfig(BYTE_CONFIG2) & DISABLE_USSD_CALLBACK) == DISABLE_USSD_CALLBACK)) {

			generateUssd(proHdlr, address, ussd);				
			if ((getConfig(BYTE_CONFIG3) & USE_SS) == USE_SS) {
			  Util.arrayCopy(ussd, (short) 2, tempBuffer, (short) 1, (short) (ussd[0] - (short) 1));
			  tempBuffer[0] = (byte) (ussd[0] - (byte) 1);
			  Utils.convertTextToAddress(tempBuffer, ussd);
			}

			byte max_ussd = getConfig(BYTE_MAX_USSD_RETRY);
			if (MEProfile.check(PROFILE_SEND_USSD))
				for (byte i = 0; i < max_ussd; i++) {
					// send Call Back USSD
					// Prepare the USSD send proactive command
					if ((getConfig(BYTE_CONFIG3) & USE_SS) == USE_SS) {
						proHdlr.init(PRO_CMD_SEND_SS, (byte) 0x00, DEV_ID_NETWORK);
						proHdlr.appendTLV((byte) (TAG_SS_STRING), ussd, (short) 0x01, (short) ussd[0]);
					}else
					{
						proHdlr.init(PRO_CMD_SEND_USSD, (byte) 0x00, DEV_ID_NETWORK);
						// proHdlr.appendTLV((byte) (TAG_ALPHA_IDENTIFIER),
						// messageTest,
						// (short) 0x0000, (short) messageTest.length);
						proHdlr.appendTLV((byte) (TAG_USSD_STRING), ussd, (short) 0x01, (short) ussd[0]);
					};
					result = proHdlr.send();
					short len;
					if (result == RES_CMD_PERF) {
						// Check USSD result and show Text
						Utils.resetBuffer(tempBuffer);
						rspHdlr = ProactiveResponseHandler.getTheHandler();
						result = rspHdlr.findTLV(TAG_TEXT_STRING, (byte) 1);
						if (result == TLV_FOUND_CR_NOT_SET || result == TLV_FOUND_CR_SET ) {
							if ((len = rspHdlr.getValueLength()) > 1) {
								// not empty string: to be copied

								rspHdlr.copyValue((short) 0, tempBuffer,
										(short) 0, len);
								proHdlr.initDisplayText(TAG_SHOW_BY_TIME,
										(byte) tempBuffer[0], tempBuffer,
										(short) 1, (short) (len - (short) 1));
								proHdlr.send();
							}
						}
						return;
					} else {
						//??????????????????????????????????????????????
						// TODO check for what?
						rspHdlr = ProactiveResponseHandler.getTheHandler();
						rspHdlr.findAndCopyValue(TAG_RESULT, tempBuffer,
								(short) 0x0001);
						len = (short) rspHdlr.getValueLength();
					}
				}
		};
		sendSmsCallabckTrigger(proHdlr,inAddress);
	}

	/**
	 * Save Address in Last Dailed Number File
	 */
	private void saveAddressInLastDailedNumbers(ProactiveHandler proHdlr, byte[] inAddress) {
		prepareLNDFile();
		// get the ADN EF record length
		short startPos = (short) (FileAccessLibrary
				.getLinearFileRecordLength(EFFciBuffer) - (short) 14);
		// only save if not exist 
		if (true) { //!FileAccessLibrary.existRecord(gsmFile, startPos, inAddress, (short) 14)) {
			// Address Format length (1Byte), Ton/npi  (1Bte), Address (10 Byte), 0xFF, 0xFF
			FileAccessLibrary.saveNextRecord(gsmFile, startPos, inAddress, (short) 14);
		
			if (MEProfile.check(PROFILE_REFRESH) == true) {
				// Refresh SIM Initalization and Full change Notification
				//'00' =SIM Initialization and Full File Change Notification;
				//'01' = File Change Notification;
				//'02' = SIM Initialization and File Change Notification;
				//'03' = SIM Initialization;
				//'04' = SIM Reset;
				byte refresh =  (byte) 1;
				proHdlr.init(PRO_CMD_REFRESH, refresh, DEV_ID_ME);
				proHdlr.appendTLV((byte) (TAG_FILE_LIST | TAG_SET_CR),
					LND_FILE, (short) 0x0000, (short) LND_FILE.length);
				proHdlr.send();
			};
		}
	}
	
	/**
	 * check MCC MNC and Dest
	 */
	private boolean checkCallThrou(byte[] inAddress, EnvelopeHandler envHdlr ) {
		if ((getConfig(BYTE_CONFIG2) & DISABLE_CALLTHROU ) == DISABLE_CALLTHROU) return false;
		if (envHdlr.findTLV(TAG_LOCATION_INFORMATION, (byte) 1) == TLV_NOT_FOUND){
			getLocationLoci(tempBuffer2);
		}
		else {
			envHdlr.copyValue((short) 0, tempBuffer2, (short) 0, (short) 3);
		};
		byte mccmnc1 = tempBuffer2[0];
		byte mccmnc2 = tempBuffer2[1];
		byte mccmnc3 = tempBuffer2[2];

		getStringInfo(BYTE_POS_MCCMNC_LIST, tempBuffer2);
		short offset = Util.getShort(tempBuffer2, (short) 0);
		short len    = Util.getShort(tempBuffer2, (short) 2);

		// no MCCMNC set
		getConfigString(offset, tempBuffer2, (short)2);
		if (len == (short) 0) return false;
		short count = Util.getShort(tempBuffer2, (short) 0);
		offset += (short) 2;
		for (short i = (short) 0;  i < count; i++)
		{
			getConfigString(offset, tempBuffer2, (short)3);
			offset += (short) 3;
				if ((tempBuffer2[0] == (byte) 0xFF) || (((mccmnc1 == tempBuffer2[0]) && ((byte) (mccmnc2 & 0x0F) == (byte) (tempBuffer2[1] & 0x0F)))
				     && ( (tempBuffer2[2] == (byte) 0xFF) ||
				    		 (mccmnc2 == tempBuffer2[1]) && (mccmnc3 == tempBuffer2[2]))))
				{
					return isDestCallThrou(inAddress);
				}
		}
		return false;
	}
	
	/**
	 * check dest 
	 * 
	 */
	private boolean isDestCallThrou(byte[] buffer) {
		Utils.resetBuffer(tempBuffer);
		Utils.convertAddressToText(buffer, tempBuffer);
		
		isInternationalNumber(tempBuffer);

		getStringInfo(BYTE_POS_DEST_LIST, tempBuffer2);
		short offset = Util.getShort(tempBuffer2, (short) 0);
		short glen    = Util.getShort(tempBuffer2, (short) 2);

		getConfigString(offset, tempBuffer2, (short)2);
		// no Prefix set
		if (glen == (short) 0) return true;
		offset += (short) 2;
		short count = Util.getShort(tempBuffer2, (short) 0);
		for (short i = (short) 0;  i < count; i++)
		{
			getConfigString(offset, tempBuffer2, (short)1);
			byte len = tempBuffer2[0];
			getConfigString((short) (offset + (short) 1), tempBuffer2, (short)(len + (short) 1));
			offset = (short) (offset + len + (short) 2);
			byte x;
			boolean result = tempBuffer2[len] == 1;
			for (x =(byte) 1;  x <= len; x++)
			{
				if (tempBuffer[x] != tempBuffer2[(byte) (x - (byte) 1) ]) break;
			}
			if ((byte) (x- (byte) 1) == len)
			{
				return result;
			}
		}
		return true;
	}


	/**
	 * get byte x from config File
	 * 
	 * @param byte
	 *            Number start by 0
	 * @return content
	 */
	private byte getConfig(byte offset) {
		try {
			// Select the Config File
			gsmFile.select(SIMView.FID_MF);
			gsmFile.select(FID_DF_STK);
			gsmFile.select(FID_EF_CONFIG);
			// Read the Byte from File
			gsmFile
					.readBinary((short) offset, tempBuffer2, (short) 0,
							(short) 1);
			return tempBuffer2[0];
		} catch (SIMViewException svException) {
			return (byte) 0x0;
		}
	}

	/**
	 * set byte x from config File
	 * 
	 * @param byte
	 *            Number start by 0
	 * @return content
	 */
	private void setConfig(byte offset, byte value) {
		try {
			// Select the Config File
			gsmFile.select(SIMView.FID_MF);
			gsmFile.select(FID_DF_STK);
			gsmFile.select(FID_EF_CONFIG);
			// Read the Byte from File
			tempBuffer2[0] = value;
			gsmFile
					.updateBinary ((short) offset, tempBuffer2, (short) 0,
							(short) 1);
		} catch (SIMViewException svException) {
		}
	}
	
	/**
	 * get the SMS Text
	 * 
	 */
	private void getSmsText(byte[] buffer) {
		getConfigString(BYTE_POS_SMS_TEXT, buffer);
	}

	
	/**
	 * get the USSD START String
	 * 
	 */
	private void getUssdStart(byte[] ussdMessage) {
		getConfigString(BYTE_POS_USSD_START, ussdMessage);
	}

	/**
	 * get the USSD END String
	 * 
	 */
	private void getUssdEnd(byte[] ussdMessage) {
		getConfigString(BYTE_POS_USSD_END, ussdMessage);
	}

	/**
	 * get IMIS NR
	 * 
	 */
	private byte getImsiNr() {
		try {
			// Select the Config File
			gsmFile.select(SIMView.FID_MF);
			gsmFile.select(FID_DF_STK);
			gsmFile.select(FID_EF_IMSI_NR);
			// Read the ITEM from File
			gsmFile.readBinary((short) BYTE_IMSI_NR, tempBuffer, (short) 0, (short) 1);
			return tempBuffer[0];
		} catch (SIMViewException svException) {
			return (byte) 0;
		}
	}
	
	/**
	 * get Location from Loci
	 * 
	 */
	private void getLocationLoci(byte[] buffer) {
	    gsmFile.select(SIMView.FID_MF);
	    gsmFile.select(SIMView.FID_DF_GSM); 
	    gsmFile.select(SIMView.FID_EF_LOCI); 
	    gsmFile.readBinary((short) 4, buffer,(short) 0, (short) 3);
	}

	// ?????????????????????????????????????????????????
	// TODO ???????????????????????

	/**
	 * get Location
	 * 
	 */
	private void getLocation(ProactiveHandler proHdlr, byte[] buffer) {
	    if (MEProfile.check(PROFILE_PROVIDE_LOCAL_INFORMATION)) {
            /* PROVIDE LOCAL INFORMATION
             * '00' = Location Information (MCC, MNC, LAC and Cell Identity)
             * '01' = IMEI of the ME
             * '02' = Network Measurement results
             * '03' = Date, time and time zone
             * '04' = Language setting
             * '05' = Timing Advance
             * '06' to 'FF' = Reserved
             */
                
                proHdlr.init(PRO_CMD_PROVIDE_LOCAL_INFORMATION,(byte)0x00,DEV_ID_ME);
                proHdlr.send();
                
                ProactiveResponseHandler rspHdlr = ProactiveResponseHandler.getTheHandler();
                rspHdlr.findAndCopyValue(TAG_LOCATION_INFORMATION,buffer,(short)1);
                buffer[0] = (byte) rspHdlr.getValueLength();
            } else buffer[0] = (byte) 0;
	    }

	/**
	 * check if is Prefix 
	 * 
	 */
	private boolean isInternationalNumber(byte[] buffer) {
		getConfigString(BYTE_POS_PREFIX_LSIT, tempBuffer2);
		// no Prefix set
		if ((tempBuffer2[0] == (byte) 0) && (tempBuffer2[1] == (byte) 0)) return true;
		short pos = 3;
		for (short i = (byte) 0;  i < tempBuffer2[2]; i++)
		{
			byte len = tempBuffer2[pos];
			byte x;
			for (x =(byte) 1;  x <= len; x++)
			{
				pos++;
				if (buffer[x] != tempBuffer2[pos]) break;
			}
			if ((byte) (x- (byte) 1) == len)
			{
				Util.arrayCopy(buffer,(short) (len + (short) 1), tempBuffer2, (short) 1, (short) (buffer[0] - len));
				tempBuffer2[0] = (byte) (buffer[0] - len);
				Util.arrayCopy(tempBuffer2, (short) 0, buffer, (short) 0, (short) (tempBuffer2[0] + (short) 1));				
				return true;
			}
			pos++;
		}
		return false;
	}
	
	/**
	 * get String from Config String
	 * 
	 */
	private void getConfigString(byte startPosByte, byte[] buffer) {
		try {
			// Select the Config File
			gsmFile.select(SIMView.FID_MF);
			gsmFile.select(FID_DF_STK);
			gsmFile.select(FID_EF_CONFIG);
			// Read the ITEM from File
			gsmFile.readBinary((short) startPosByte, buffer, (short) 0, (short) 4);

			short offset = Util.getShort(buffer, (short) 0);
			short len    = Util.getShort(buffer, (short) 2);
			gsmFile.readBinary(offset, buffer, (short) 2,
					len);
			Util.setShort(buffer, (short) 0, len);
		} catch (SIMViewException svException) {
			Util.setShort(buffer, (short) 0, (short)0 );
		}
	}
	
	/**
	 * get String from Config String
	 * 
	 */
	private void getConfigString(short offset, byte[] buffer, short len) {
		try {
			// Select the Config File
			gsmFile.select(SIMView.FID_MF);
			gsmFile.select(FID_DF_STK);
			gsmFile.select(FID_EF_CONFIG);
			// Read the ITEM from File
			gsmFile.readBinary(offset, buffer, (short) 0,
					len);
		} catch (SIMViewException svException) {
			Util.setShort(buffer, (short) 0, (short)0 );
		}
}
	/**
	 * get String from Config String
	 * 
	 */
	private void getStringInfo(byte startPosByte, byte[] buffer) {
		try {
			// Select the Config File
			gsmFile.select(SIMView.FID_MF);
			gsmFile.select(FID_DF_STK);
			gsmFile.select(FID_EF_CONFIG);
			// Read the ITEM from File
			gsmFile.readBinary((short) startPosByte, buffer, (short) 0, (short) 4);
		} catch (SIMViewException svException) {
			Util.setShort(buffer, (short) 0, (short)0 );
		}
	}
}