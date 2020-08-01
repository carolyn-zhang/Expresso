import java.util.*;

public class State
{
  private Map<String, Object> varMap;
  
  public State()
  {
    varMap = new TreeMap<String, Object>();
  }
  
  public void setVariableValue(String varName, Object value)
  {
    varMap.put(varName, value);
  }
  
  public Object getVariableValue(String varName)
  {
    return varMap.get(varName);
  }
}
