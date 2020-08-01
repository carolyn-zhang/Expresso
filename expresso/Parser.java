import java.util.*;

public class Parser
{
    private Map<String, Integer> prefixMap;
    private Map<String, Integer> infixMap;
    private Tokenizer tokenizer;
    private String token;

    public Parser()
    {
        prefixMap = new TreeMap<String, Integer>();
        infixMap = new TreeMap<String, Integer>();
    }

    public void setPrefix(String key, int numArgs)
    {
        prefixMap.put(key, numArgs);
    }

    public void setInfix(String key, int numArgs)
    {
        infixMap.put(key, numArgs);
    }

    //text is the entire code, not just one line
    public Object parse(String text)
    {
        //System.out.println("text:"+text);
        tokenizer = new Tokenizer(text);
        token = tokenizer.next();
        return parse();
    }

    private void eat(String s)
    {
        
        if (token.equals(s))
            token = tokenizer.next();
        else
            throw new RuntimeException("expected " + s + " but found " + token);
    }
    
    private Object parse()
    {
        Object parsed = atom();
        if (infixMap.containsKey(token))
        {
            int numArgs = infixMap.get(token) - 1;
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(parsed);
            
            list.add(token);
            
            eat(token);
            while (numArgs > 0)
            {
                list.add(parse());
                numArgs--;
            }
            parsed = list;
        }
        return parsed;
    }

    //very important - build multi-level embeded arraylist based on (),[] and {}
    private Object atom()
    {
      
        if (token.equals("("))
            return parseList("(", ")");
        if (token.equals("["))
            return parseList("[", "]");
        if (token.equals("{"))
            return parseList("{", "}");
        if (prefixMap.containsKey(token))
        {
            int numArgs = prefixMap.get(token);
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(token);
            eat(token);
            while (numArgs > 0)
            {
                list.add(parse());
                numArgs--;
            }
            return list;
        }
        String literal = token;
        eat(literal);
        try
        {
          //change one line here to parse numbers into doubles instead of integers
            return Double.parseDouble(literal);
        }
        catch(NumberFormatException e)
        {
            return literal;
        }
    }

    private ArrayList<Object> parseList(String open, String close)
    {
        ArrayList<Object> list = new ArrayList<Object>();
        
        list.add(open);
        eat(open);
        while (!token.equals(close))
            list.add(parse());
       
          
        list.add(close);
        eat(close);
        
        return list;
    }
}