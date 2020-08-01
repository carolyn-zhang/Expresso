import java.io.*;
import java.util.*;

public class Interpreter
{
  private static Scanner in = new Scanner(System.in);
  
  public static void main(String[] args)
  {
    Parser parser = new Parser();

    parser.setPrefix("System.out.println", 1);
    parser.setPrefix("if", 2);
    parser.setPrefix("elseif", 2);
    parser.setPrefix("else", 1);
    parser.setPrefix("while", 2);
    parser.setPrefix("function", 3);
    parser.setPrefix("call", 2);
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
    parser.setInfix("&&", 2);
    parser.setInfix("||", 2);
    parser.setPrefix("!", 1);
    parser.setPrefix("abs", 1);
    parser.setPrefix("cos", 1);
    parser.setPrefix("sin", 1);
    
    //Object program = parser.parse("print 7 + 2 + 1");
    //Object program = parser.parse(input());
    Object program = parser.parse(load("program.txt"));
    //System.out.println("parsed:  " + program + "(" + program.getClass() + ")");
    State state = new State();
    eval(program, state);
  }
  
  public static Object eval(Object exp, State state)
  {
    //System.out.println("eval(" + exp + ")");
    if (exp instanceof Double)
    {
      return exp;
    }
    else if (exp instanceof String && (exp.equals("true")||exp.equals("false")))
    {
      //System.out.println("inside instance Boolean");
      if(exp.equals("true"))
        return true;
      else
        return false;
    }
    else if (exp instanceof String)
    {
      //System.out.println("line 47:"+exp);
      return state.getVariableValue((String)exp);
    }
    else
    {
      //must be a List
      List<Object> list = (List<Object>)exp;
      if(list.get(0).equals("function"))
       {
         List<Object> list2 = (List<Object>)list.get(2);
         
         String functionName = (String) list.get(1);
         String variableName = (String) list2.get(1);
         
         state.setVariableValue(functionName,list);
         //System.out.println("functioname:" + functionName);
         //System.out.println("functioncode:" + functionCode);
        return ":f";
       }
      else if(list.get(0).equals("call"))
       {
         
         String functionName = (String) list.get(1);
         
         List<Object> functionCode=(List)state.getVariableValue((String)functionName);
         List<Object> listParameters = (List<Object>)functionCode.get(2);
         for(int i = 1; i < listParameters.size(); i = i + 2)
         {
           String parameterName = (String)listParameters.get(i); //gives x
           //state.setVariableValue(parameterName, ((List)list.get(2)).get(i));
            Object x=((List)list.get(2)).get(i);
            if( x instanceof String&&((String)x).charAt(0)=='"')
                 state.setVariableValue(parameterName,x);
            else state.setVariableValue(parameterName,  eval(x, state));
         }
         
         return eval(functionCode.get(3),state);
       }

      else if (list.get(0).equals("{"))
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
        return eval(list2, state);  //this line is very important       
      }
      
      else if (list.get(0).equals("System.out.println"))
      {
        //System.out.println("here" +list.get(0));
        //System.out.println("here2"+list.get(1));
        
          List<Object> list2 = (List<Object>)list.get(1);
          
          //if literal value, print out directly
          if(list2.get(1) instanceof String && ((String)list2.get(1)).charAt(0) == '"')
          {
             String tt=(String) list2.get(1);
             
             System.out.println(tt.replace("\"",""));
          }
          //otherwise, evaluate first before print out
          
          else {//System.out.println(list2.get(1));
               Object tt=eval(list2.get(1),state);
               
               if(tt instanceof Double)
                  System.out.println(tt);
               else if(tt instanceof Boolean )
                 System.out.println(tt);
               else if(tt instanceof String) //variable name without quotations
                 System.out.println(((String)tt).replace("\"",""));                       
               //System.out.println(eval(list2.get(1), state));
               else  System.out.println(tt);
          }
        
        return ":p";
      }
       else if (list.get(0).equals("if"))
      {
        //System.out.println("here" +list.get(0));
        //System.out.println("inside of if"+list.get(1));
        List<Object> list2 = (List<Object>)list.get(1);
        if((Boolean)eval(list2, state)) 
        {
          //System.out.println("if condition is true");
          
          eval((List<Object>)list.get(2),state);
          state.setVariableValue("ifFlag", true);
        }
        else { //System.out.println("if condition is false");
        state.setVariableValue("ifFlag", false);
        }
        return ":i";
      }
       else if (list.get(0).equals("elseif"))
       {
         if((Boolean)state.getVariableValue("ifFlag")==true)
         return ":ei";
         //System.out.println("IN ELSE IF");
         List<Object> list2 = (List<Object>)list.get(1);
         if((Boolean)eval(list2, state)) 
         {
           //System.out.println("elseif condition is true");
           state.setVariableValue("ifFlag", true);
           eval((List<Object>)list.get(2),state);
         }/////////////////////////////////////////////////////////////////////////////////
         //else  System.out.println("elseif condition is false");
         return ":ei";
       }
       else if (list.get(0).equals("else"))
       {
         if((Boolean)state.getVariableValue("ifFlag")==true)
           return ":e";
         //System.out.println("IN ELSE");
         eval((List<Object>)list.get(1),state);
         
         return ":e";
       }
       else if(list.get(0).equals("while"))
       {
         List<Object> list2 = (List<Object>)list.get(1);
        while((Boolean)eval(list2, state)) 
        {
          eval((List<Object>)list.get(2),state);
        }
        return ":w";
       }
       /*
      else if (list.get(0).equals("-"))
      {
        return -(Integer)eval(list.get(1), state);
      }*/
       
      else if (list.get(1).equals("-"))
      {
        return (Double)eval(list.get(0), state) -
          (Double)eval(list.get(2), state);
      }
      else if (list.get(1).equals("*"))
      {
        return (Double)eval(list.get(0), state) *
          (Double)eval(list.get(2), state);
      }
      else if (list.get(1).equals("/"))
      {
        return (Double)eval(list.get(0), state) /
        (Double)eval(list.get(2), state);
         
      }
      else if (list.get(1).equals("+"))
      {
        //add new lines to allow string addition, for example "abc"+"def"="abcdef"
        Boolean a=list.get(0) instanceof String&&((String)list.get(0)).charAt(0)=='"';
        Boolean b=list.get(2) instanceof String&&((String)list.get(2)).charAt(0)=='"';
        
        if(a==true && b==true)
            return (String)list.get(0)+(String)list.get(2);
        else if(a==true && b==false) 
          return (String)list.get(0)+String.valueOf(eval(list.get(2), state));
        else if(a=false && b==true) 
          return String.valueOf(eval(list.get(0), state))+(String)list.get(2);
        else 
        {
          Object a1 = eval(list.get(0), state);
          Object b1 = eval(list.get(2), state);
          if(a1 instanceof Double && b1 instanceof Double)
            return (Double) a1 + (Double) b1;
          else
            return (String) a1 + (String) b1;
        }
      }
      else if (list.get(1).equals("^"))
      {
        
        Double count = (Double)eval(list.get(0), state);
        Double origNum = (Double)eval(list.get(0), state);
        Double expo = (Double)eval(list.get(2), state);
        
        for(double i = expo; i > 1; i--)
        {
          count = count * origNum;
        }
        return count;
      }
      else if (list.get(1).equals("%"))
      {
        return (Double)eval(list.get(0), state) %
          (Double)eval(list.get(2), state);
      }
      //added to evaluate logical equal condition
      else if (list.get(1).equals("=="))
      {
        /*if(list.get(0) instanceof String)
        {*/
          if(eval(list.get(0), state).equals(eval(list.get(2), state)))
            return true;
          else
            return false;
        //}
        //System.out.println("inside ==");
        /*if(list.get(0).equals(list.get(2)))
          return true;
        else
          return false;*/
      }
      else if (list.get(1).equals("="))
      {
        //2 lines of new code to set literal value to string variable
        if(list.get(2) instanceof String&&((String)list.get(2)).charAt(0)=='"')
          state.setVariableValue((String)list.get(0),list.get(2));
        else state.setVariableValue((String)list.get(0),
                               eval(list.get(2), state));
        return "=)";
      }
      else if (list.get(1).equals(">="))
      {
        if(((Double)eval(list.get(0), state)).doubleValue() >= ((Double)eval(list.get(2), state)).doubleValue())
          return true;
        else
          return false;
      }
      else if (list.get(1).equals("<="))
      {
        if(((Double)eval(list.get(0), state)).doubleValue() <= ((Double)eval(list.get(2), state)).doubleValue())
          return true;
        else
          return false;
      }
      else if (list.get(1).equals(">"))
      {
        if(((Double)eval(list.get(0), state)).doubleValue() > ((Double)eval(list.get(2), state)).doubleValue())
          return true;
        else
          return false;
      }
      else if (list.get(1).equals("<"))
      {
        if(((Double)eval(list.get(0), state)).doubleValue() < ((Double)eval(list.get(2), state)).doubleValue())
          return true;
        else
          return false;
      }
      else if(list.get(0).equals("!"))
      {
        
        //if(eval(list.get(1), state) instance of String)
          
        return !((Boolean)eval(list.get(1), state));
      }
      else if (list.get(1).equals("&&"))
      {
        Boolean b1=(Boolean)eval(list.get(0), state);
        Boolean b2=(Boolean)eval(list.get(2), state);
        //System.out.println("b1:"+b1);
        //System.out.println("b2:"+b2);
        if(b1 && b2)
          return true;
        else
          return false;
      }
      else if (list.get(1).equals("||"))
      {
        if((Boolean)eval(list.get(0), state) || (Boolean)eval(list.get(2), state))
          return true;
        else
          return false;
      }
      else if (list.get(0).equals("cos"))
      {
        List<Object> list2 = (List<Object>)list.get(1);
          return java.lang.Math.cos((Double)eval(list2.get(1), state));
      }
      else if (list.get(0).equals("sin"))
      {
        List<Object> list2 = (List<Object>)list.get(1);
          return java.lang.Math.sin((Double)eval(list2.get(1), state));
      }
      
      else if (list.get(0).equals("abs"))
      {
          List<Object> list2 = (List<Object>)list.get(1);
          return java.lang.Math.abs((Double)eval(list2.get(1), state));
        
          //return ":a";
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