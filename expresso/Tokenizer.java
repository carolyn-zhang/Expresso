import java.io.*;

public class Tokenizer
{
    private String text;
    private int index;  //index of char to process next

    public Tokenizer(String text)
    {
        this.text = text;
        index = 0;
    }

    public String next()
    {
        skipWhitespace();
        skipComment();

        if (index == text.length())
            return "";  //indicates end of program

        String token = "";

        if (isAlphaNum(text.charAt(index)))
        {
            //consists of a-z, A-Z, 0 - 9, or _.
            //this code assumes any string of such characters is a single token.
            while (index < text.length() && isAlphaNum(text.charAt(index)))
            {
                token += text.charAt(index);
                index++;
            }
        }
        
        //new code to hanlde quote
         else if (text.charAt(index)=='"')
        {
             
            //double quote - put "text inside" as one token
          
              int i=text.indexOf('"',index+1); //end of comment
              if (i==-1)  throw new RuntimeException("expected end of quote, but not found " );
            
              token = text.substring(index,i+1);
              index=i+1;
            
        }

         //new code
         else if (text.length()>index+1&&text.substring(index,index+2).equals("=="))
        {
            //not alphanumeric.  therefore, this symbol is assumed to be a one character token.
           
            token += "==";
            index=index+2;
        }
         
         else if (text.length()>index + 1 && text.substring(index, index + 2).equals("&&"))
         {
           token = "&&";
           index=index + 2;
         }
         else if (text.length()>index + 1 && text.substring(index, index + 2).equals("||"))
         {
           token = "||";
           index=index+2;
         }
         
         else if (text.length()>index+1&&text.substring(index,index+2).equals(">="))
         {
           //not alphanumeric.  therefore, this symbol is assumed to be a one character token.
           
           token = ">=";
           index=index+2;
        }

         else if (text.length()>index+1&&text.substring(index,index+2).equals("<="))
        {
            //not alphanumeric.  therefore, this symbol is assumed to be a one character token.
           
            token = "<=";
            index=index+2;
        }
         //to distinguish negative sign from substraction, we look at what comes before it
         
        else if ( text.charAt(index)=='-'&&isNum(text.charAt(index+1)))
        {
            
            
          int p=index-1;
          
          //skip space backwards
          while (p > 0 && isWhitespace(text.charAt(p)))
            p--;
          
          
          if(text.charAt(p)=='='||text.charAt(p)=='(' || text.charAt(p)=='+' || text.charAt(p)=='-'|| text.charAt(p)=='*'
            || text.charAt(p)=='/')
          {
             token += text.charAt(index);
             index++;
             while (index < text.length() && isNum(text.charAt(index)))
            {
                token += text.charAt(index);
                index++;
            }
             
          }
          // - means substraction here
          else {
               token += text.charAt(index);
               index++;
               
             }
        }
        //single character token
        else
        {
            //not alphanumeric.  therefore, this symbol is assumed to be a one character token.
           
            token += text.charAt(index);
            index++;
        }

        skipWhitespace();
        skipComment();
        return token;
    }

     private void skipComment()
    {
       if (text.length()>index+1 && text.substring(index,index+2).equals("//"))
       { 
          
          while (index < text.length() && text.charAt(index)!='\n' && text.charAt(index)!='\r')
            index++;
          
          //now we are at the end of line
          index=index++;        
       }
       
       else if (text.length()>index+1&&text.substring(index,index+2).equals("/*"))
       { 
          
          int i=text.indexOf("*/",index+1); //end of comment
          if (i==-1)  {
            throw new RuntimeException("expected end of comment */, but not found " );
            
          }
          else index=i+2;
       }
       
    }

    private void skipWhitespace()
    {
        while (index < text.length() && isWhitespace(text.charAt(index)))
            index++;
    }
    
    private void skipWhitespaceBackward()
    {
        while (index > 0 && isWhitespace(text.charAt(index)))
            index--;
    }


    private boolean isWhitespace(char ch)
    {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
    }

    //true if 0-9, a-z, A-Z, or _
    private boolean isAlphaNum(char ch)
    {
        return '0' <= ch && ch <= '9'
        || 'a' <= ch && ch <= 'z'
        || 'A' <= ch && ch <= 'Z'
        || ch == '_'||ch == '.'/* ||
          ch == '-'*/;
    }
    
        private boolean isNum(char ch)
    {
        return '0' <= ch && ch <= '9'||ch=='.'
        ;
    }

}
