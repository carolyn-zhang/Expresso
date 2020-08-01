import java.io.*;
import java.util.*;

public class InterpreterInteger
{
  private static Scanner in = new Scanner(System.in);
  
  public static void main(String[] args)
  {
    Parser parser = new Parser();

    parser.setPrefix("System.out.println", 1);
    parser.setPrefix("if", 1);
    
    //parser.setPrefix("-", 1); 
   
    parser.setInfix("-", 2);
    parser.setInfix("+", 2);
    parser.setInfix("==", 2);
    parser.setInfix("=", 2);
    parser.setInfix("*", 2);
    parser.setInfix("/", 2);
    parser.setInfix("^", 2);
    parser.setInfix("%", 2);
    parser.setInfix(">=", 2);
    parser.setInfix("<=", 2);
    parser.setInfix(">", 2);
    parser.setInfix("<", 2);
    parser.setPrefix("while", 1);
    //Object program = parser.parse("print 7 + 2 + 1");
    //Object program = parser.parse(input());
    Object program = parser.parse(load("program.txt"));
    System.out.println("parsed:  " + program + " (" + program.getClass() + ")");
    State state = new State();
    eval(program, state);
  }
  
  public static Object eval(Object exp, State state)
  {
    //System.out.println("eval(" + exp + ")");
    if (exp instanceof Integer)
    {
      return exp;
    }
    else if (exp instanceof String)
    {
      System.out.println(exp);
      return state.getVariableValue((String)exp);
    }
    else
    {
      //must be a List
      List<Object> list = (List<Object>)exp;
      
      if (list.get(0).equals("{"))
      {
        for (int i = 1; i < list.size() - 1; i++)
          eval(list.get(i), state);
        return ":}";
      }
      
      //below 5 lines added to handle () in the arithmatic calculations
      else if (list.get(0).equals("("))
      {
        List<Object> list2 = (List<Object>)list.get(1);
        //System.out.println("line 60:"+list2);
        return eval(list2, state);  //this line very important       
      }
      
      else if (list.get(0).equals("System.out.println"))
      {
        //System.out.println("here" +list.get(0));
        //System.out.println("here2"+list.get(1));
        List<Object> list2 = (List<Object>)list.get(1);
        System.out.println(eval(list2.get(1), state));
        return ":p";
      }
       else if (list.get(0).equals("if"))
      {
        //System.out.println("here" +list.get(0));
        System.out.println("inside of if"+list.get(1));
        List<Object> list2 = (List<Object>)list.get(1);
        if((Boolean)eval(list2, state)) 
        {
          System.out.println("if is true");
        }
        return ":p";
      }
       /*
      else if (list.get(0).equals("-"))
      {
        return -(Integer)eval(list.get(1), state);
      }*/
       
      else if (list.get(1).equals("-"))
      {
        return (Integer)eval(list.get(0), state) -
          (Integer)eval(list.get(2), state);
      }
      else if (list.get(1).equals("*"))
      {
        
        return (Integer)eval(list.get(0), state) *
          (Integer)eval(list.get(2), state);
      }
      else if (list.get(1).equals("/"))
      {
        return (Integer)eval(list.get(0), state) /
        (Integer)eval(list.get(2), state);
         
      }
      else if (list.get(1).equals("+"))
      {
        return (Integer)eval(list.get(0), state) +
          (Integer)eval(list.get(2), state);
      }
      else if (list.get(1).equals("^"))
      {
        int count = (Integer)eval(list.get(0), state);
        int origNum = (Integer)eval(list.get(0), state);
        int expo = (Integer)eval(list.get(2), state);
        
        for(int i = expo; i > 1; i--)
        {
          count = count * origNum;
        }
        return count;
      }
      else if (list.get(1).equals("%"))
      {
        return (Integer)eval(list.get(0), state) %
          (Integer)eval(list.get(2), state);
      }
      //added to evaluate logical equal condition
      else if (list.get(1).equals("=="))
      {
        //System.out.println("inside ==");
        if(list.get(0) == list.get(2))
          return true;
        else
          return false;
      }
      else if (list.get(1).equals("="))
      {
        state.setVariableValue((String)list.get(0),
                               eval(list.get(2), state));
        return "=)";
      }
      else if (list.get(1).equals(">="))
      {
        if(((Integer)list.get(0)).doubleValue() >= ((Integer)list.get(2)).doubleValue())
          return true;
        else
          return false;
      }
      else if (list.get(1).equals("<="))
      {
        if(((Integer)list.get(0)).doubleValue() <= ((Integer)list.get(2)).doubleValue())
          return true;
        else
          return false;
      }
      else if (list.get(1).equals(">"))
      {
        if(((Integer)list.get(0)).doubleValue() > ((Integer)list.get(2)).doubleValue())
          return true;
        else
          return false;
      }
      else if (list.get(1).equals("<"))
      {
        if(((Integer)list.get(0)).doubleValue() < ((Integer)list.get(2)).doubleValue())
          return true;
        else
          return false;
      }
      else
        throw new RuntimeException("unable to evaluate:  " + exp);
    }
  }
  
  public static String input()
  {
    return in.nextLine();
  }
  
  public static String load(String fileName)
  {
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null)
      {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
      return sb.toString();
    }
    catch(IOException e)
    {
      throw new RuntimeException(e);
    }
  }
}