package controllers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.support;



public class Support extends Controller {
	@Security.Authenticated(Secured.class)
	public static Result support() {
		return ok(support.render());
	}
	@Security.Authenticated(Secured.class)
    public static Result submitSupport() {
    	DynamicForm requestData = Form.form().bindFromRequest();
		String email1 = requestData.get("email");
		String message = requestData.get("message");
		try{
		Email email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("nullterminatorscs410@gmail.com", "software123"));
		email.setSSLOnConnect(true);
		email.setFrom("nullterminatorscs410@gmail.com");
		email.addTo("nullterminatorscs410@gmail.com");
		email.setSubject(requestData.get("category"));
		email.setMsg("From: " + email1 + "\n\n" + message);
		email.send();
			
			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
        return ok(support.render());
    }

}