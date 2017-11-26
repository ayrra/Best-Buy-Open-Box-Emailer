import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import org.json.JSONObject;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OpenBoxEmailer {

	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	
	public static void main(String[] args) throws IOException, AddressException, MessagingException, InterruptedException {
		
		String key = ""; //SET YOUR BESTBUY API KEY HERE
		String sku = "5761701";					 //SET THE SKU FOR 
		int resultCount = 0;
		int queryCount = 0;
		
		URL url = new URL("https://api.bestbuy.com/beta/products/" + sku + "/openBox?apiKey=" + key);
		JSONObject obj = new JSONObject();
			
			while (resultCount == 0) {		
				
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				int status = con.getResponseCode();
				//status code for debugging, we have to handle != 200
				System.out.println("HTTP Status Code is: " + status);
				System.out.println("QueryCount: " + queryCount);
				queryCount++;
				if (status == 200) {
					Scanner scan = new Scanner(con.getInputStream());
					String str = new String();
					while (scan.hasNext()) 
						str += scan.nextLine();
					scan.close();
										
					obj = new JSONObject(str);				
					
					resultCount = (int) obj.getJSONObject("metadata").getJSONObject("resultSet").get("count");
				}
				Thread.sleep(15000);
			}
			
			JSONObject results = obj.getJSONArray("results").getJSONObject(0);
			JSONObject link = results.getJSONObject("links");		
			
						
			String buyLink = (String)link.get("addToCart");
			String product = (String)results.getJSONObject("names").get("title");
			Double currentPrice = (Double)results.getJSONArray("offers").getJSONObject(0).getJSONObject("prices").get("current");
			
			
			DateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm:ss a");
			Date d = new Date();
			String convertedDate = df.format(d.getTime());
			Details dets = new Details(buyLink, currentPrice, product, convertedDate);
			generateMail(dets);
			
	}
	
	public static void generateMail(Details d) throws AddressException, MessagingException {
		
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("EMAILTOSENDTO")); //CHANGE THIS
		//generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("EMAILTOSENDTO2")); //UNCOMMENT AND CHANGE IF YOU WANT TO EMAIL MORE THAN ONE PERSON
		generateMailMessage.setSubject("Bestbuy Open Box Item Available.");
		String emailBody = "Item: " + d.getDetails() + "<br>" + "Available on: " + d.getDate() + "<br>" + "Price: " + d.getPrice() + "<br>" + "Add to cart: " + d.getUrl();
		generateMailMessage.setContent(emailBody, "text/html");
		
		Transport transport = getMailSession.getTransport("smtp");
		
		transport.connect("smtp.gmail.com", "EMAIL", "PASSWORD"); //change these
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
		
	}

}
