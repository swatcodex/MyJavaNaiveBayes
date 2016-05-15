package nbpckg;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.hadoop.fs.Path;


public class tok {
  
  public static void main(String[] args) {
    tok t = new tok();
    t.getFiles("spam");
    t.getFiles("easy_ham");
  }

  void getFiles(String label){
    try {
      File dir=new File(".");
      File directory = new File(dir.getCanonicalPath()+Path.SEPARATOR+label);
      File[] trainDataFiles = directory.listFiles();
      for(File data:trainDataFiles){
        Properties props = System.getProperties();
        Session mailSession = Session.getInstance(props, null);
        InputStream source = new FileInputStream(data);
        MimeMessage message = new MimeMessage(mailSession,source);
        String sub = message.getSubject();
        //System.out.println(sub.toString());
        String[] str = message.getContentType().split(";");  
        if(str[0].equals("text/plain")){
          //System.out.println(message.getContent().toString());
          String body = message.getContent().toString();
          tokenizeBody(sub,body,directory.getName().toString());
        }
        else
        {
          Object msg = message.getContent();
          if(msg instanceof String){
            String body = (String) msg;
            tokenizeBody(sub,body,directory.getName().toString());
          }
          else if(msg instanceof Multipart){
            Multipart mp = (Multipart) msg;
            int count = mp.getCount();
            for(int i=0;i<count;i++){
            BodyPart bp = mp.getBodyPart(i);
            if(Pattern.compile(Pattern.quote("text/html"),Pattern.CASE_INSENSITIVE).matcher(bp.getContentType()).find())
            {
              String body = bp.getContent().toString();
              tokenizeBody(sub,body,directory.getName().toString());
            }
            else {
              String body = bp.getContent().toString();
              tokenizeBody(sub,body,directory.getName().toString());
            }
          }
        }
      }
    }
  }
  catch (IOException e) {
    e.printStackTrace();
  } catch (Exception e) {
    e.printStackTrace();
  }
}


void tokenizeBody(String sub, String body,String dir){
  try
  {
    BufferedReader sw = new BufferedReader(new FileReader("StopWord.txt"));
    BufferedWriter bw = new BufferedWriter(new FileWriter("corpus.csv",true));
    /* reading all stop words*/
    Set<String> stopWords = new LinkedHashSet<String>();
    for(String line;(line = sw.readLine()) != null;)
      stopWords.add(line.trim().toLowerCase());
      sw.close();
    
      body = sub+" "+body;
      if(dir.toString().equals("spam")){
        bw.append("spam");
      }
      else if(dir.toString().equals("easy_ham")){
        bw.append("ham");
      }
      bw.append(",");
      body = body.replace(',', ' ');
      bw.append(body);
      bw.newLine();
      bw.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
