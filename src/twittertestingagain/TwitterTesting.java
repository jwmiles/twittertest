/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twittertestingagain;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import twitter4j.*;
import java.util.Map;
import twitter4j.conf.ConfigurationBuilder;


/**
 *
 * @author admin
 */
public class TwitterTesting {
  
    private static final int tweetsPerQuery	= 10;
    private static final int maxQueries	= 5;
    
    
    
    public static String cleanText(String text) {
        text = text.replace("\n", "\\n"); 
        text = text.replace("\t", "\\t"); 
        return text; 
    } 
    
    
        
        

    /**
     * @param args the command line arguments
     * @throws twitter4j.TwitterException
     * @throws java.io.IOException
     */
    public static void main (String[] args) throws TwitterException, IOException {     
        
     
       /* ---------------------------Setting up twitter account authentication-------------------------------*/       
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
            .setOAuthConsumerKey("YjICBJeNlnxAf3tFw7awLaCzS")
            .setOAuthConsumerSecret("8IfPzkr4opePnhCLLloKMP6X44IeNav0fLDrmtBrPbaHoxd1nO")
            .setOAuthAccessToken("4146680697-oOEPVezvvZ82vB7iP9HSbkoTG9ze9gH69XLrSCP")
            .setOAuthAccessTokenSecret("HZjsaabmVjeSkSX6vvVFdT3GWZek8xJ9RKfwaR57RDyEG");
    
   /* ---------------------------------File Writing Variables------------------------------------------------*/
    
    File outfile = new File ("output.txt");
    FileWriter fwriter = new FileWriter (outfile);
   
        try (PrintWriter pWriter = new PrintWriter (fwriter)) {    
    
   /*----------------------------------Search Parameters-------------------------------------*/ 
            
            String search = "food";
            String lang = "en";           
   /*------------------------End Search Parameters----------------------------------------*/
            
            int numTweets = 0;
            long maxID = -1;
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            try {
                
                Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
                //System.out.println(rateLimitStatus);
                RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
                
                /*System.out.printf("You have %d calls remaining out of %d, Limit resets in %d seconds\n",
                        searchTweetsRateLimit.getRemaining(),
                        searchTweetsRateLimit.getLimit(),
                        searchTweetsRateLimit.getSecondsUntilReset());
                */
                
                
                for (int queryNumber=0; queryNumber < maxQueries; queryNumber++){
                    
                    System.out.printf("\n\n!!! Starting loop %d\n\n", queryNumber);
                    pWriter.println("\n\n!!! Starting iteration #" + queryNumber + "\n\n");
                    
                    if(searchTweetsRateLimit.getRemaining()== 0) {
                        System.out.printf("!!! Sleeping for %d seconds due to rate limits\n",
                                searchTweetsRateLimit.getSecondsUntilReset());
                        Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset()+2) * 1000l);
                    }
                    //here is where we can send an object to the query
                    Query query = new Query(search);
                    query.setCount(tweetsPerQuery);
                    query.resultType(Query.ResultType.recent);
                    query.setLang(lang);
                    
                    
                    
                    if(maxID != -1){
                        query.setMaxId(maxID - 1);
                    }
                    
                    QueryResult result = twitter.search(query);
                    
                    if (result.getTweets().size()==0){
                        break;
                    }
                    
                    for (Status s: result.getTweets()){
                        
                        numTweets++;
                        
                        if (maxID == -1 || s.getId() < maxID){
                            maxID = s.getId();
                        }
                        
                        
                        System.out.printf("On %s, @%-20s said: %s\n",
                                s.getCreatedAt().toString(),
                                s.getUser().getScreenName(),
                                cleanText(s.getText()));
                        
                        pWriter.println("On " + s.getCreatedAt().toString()+ " @" + s.getUser().getScreenName()+ " " + cleanText(s.getText()));
                        
                        
                    }
                    
                    searchTweetsRateLimit = result.getRateLimitStatus();
                    
                    
                }
                
                
            }
            
            catch (Exception e) {
                
                System.out.println("Broken");
                
                e.printStackTrace();
            }
            System.out.printf("\n\nA total of %d tweets retrieved\n", numTweets);
            pWriter.println("\n\nA total of " + numTweets + " tweets retrieved\n\n");
     pWriter.close();
    
    
   /*while (result.hasNext()){
       numTweets += result.getCount();
   System.out.println(numTweets);
    for (Status status : result.getTweets()) {
        System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
    }  
    result.nextQuery();
   }
      
        
    
    
   /*
    QueryForm qf = new QueryForm();
		qf.show();
   */ 
   
    
}    
    
}
    
}
