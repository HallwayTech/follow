// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
Extension of {@link java.util.Properties} which allows one to specify
property values which are Lists of Strings.

@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class EnumeratedProperties extends Properties {
  
  /**
  Returns the List value of the property with the supplied key. Note that one
  can call getEnumeratedProperty() for a given key successfully if and only if
  setEnumeratedProperty() for that key was called some time beforehand. All
  members of the list returned will be Strings.
  @param key lookup of the enumerated property to be retrieved.
  @return list containing String values
  */
  public List getEnumeratedProperty (String key) {
    List values = new ArrayList();
    int i = 0;
    String value;
    while ((value = this.getProperty(key + delimiter + i++)) != null) {
      values.add(value);
    }
    return values;
  }

  /**
  Assigns the supplied array of String values to the supplied key.
  @param key property lookup
  @param values values to be associated with the property lookup
  */  
  public void setEnumeratedProperty (String key, String[] values) {
    int i;
    for (i=0; i < values.length; i++) {
      this.setProperty(key + delimiter + i, values[i]);
    }
    while (this.getProperty(key + delimiter + i) != null) {
      this.remove(key + delimiter + i);
      i++;
    }
  }
  
  /**
  Convenience method; equivalent to calling
  setEnumeratedProperty(key, (String[])values.toArray(new String[] {}));
  */
  public void setEnumeratedProperty (String key, List values) {
    this.setEnumeratedProperty(key, (String[])values.toArray(new String[] {}));
  }

  /** Delimiter between property name & list member index */
  protected static char delimiter = '.';
  
}

