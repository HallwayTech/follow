package ghm.follow.search;

public abstract class SearchStrategy
{
  public static final byte CASE_INSENSITIVE = 0;
  public static final byte CASE_SENSITIVE = 1;
  public static final byte REGULAR_EXPRESSION = 2;
  
  protected String text;

  public static SearchStrategy getInstance(String text) {
    return getInstance(CASE_INSENSITIVE, text);
  }

  public static SearchStrategy getInstance(byte type, String text) {
    if (type == CASE_INSENSITIVE) {
      return new BasicSearchStrategy(text);
    }
    else if (type == CASE_SENSITIVE) {
      return new BasicSearchStrategy(text);
    }
    else if (type == REGULAR_EXPRESSION) {
      return new BasicSearchStrategy(text);
    }
    else {
      throw new IllegalArgumentException("Unknown search strategy requested [type=" + type);
    }
  }

  public SearchStrategy(String text) {
    this.text = text;
  }

  /**
   * Search <code>text</code> for <code>term</code>. 
   * @param term
   * @param text
   * @return An array of found positions of term
   */
  public abstract Result[] search(String term);
  
  /**
   * Container for positions of found terms.
   * 
   * @author chall
   */
  public class Result {
    public int start;
    public int end;
    public String term;

    public Result(int start, int end, String term) {
      this.start = start;
      this.end = end;
    }
  }
}