package controllers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.support;



public class Support extends Controller {
	public static Result support() {
		return ok(support.render());
	}
    public static Result submitSupport() {
    	DynamicForm requestData = Form.form().bindFromRequest();
		String email1 = requestData.get("email");
		String message = requestData.get("message");
		try{
		Email email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("harshu.160392@gmail.com", ""));
		email.setSSLOnConnect(true);
		email.setFrom("harshu.160392@gmail.com");
		email.addTo("harshu.160392@gmail.com");
		email.setSubject("Forget-Password");
		email.setMsg(email1+message);
		email.send();
			
			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
        return ok(support.render());
    }

}