package callback.menu;

import java.util.List;
import java.util.ArrayList;

/**
 * User: Artur WItek
 * Date: 2008-10-22
 * Time: 21:09:49
 */
public class Menu {

  private List<RecordStructure> records = new ArrayList<RecordStructure>();

  public void addRecord(RecordStructure r) {
    records.add(r);
  }

  public List<RecordStructure> getRecords() {
    return records;
  }
  
}
