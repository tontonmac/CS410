package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import models.Book;
import models.Listing;
import models.User;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class BookController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result newBook() {
		return ok(newBook.render());
	}

	public static Result submitNewBook() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String title = requestData.get("title");
		String isbn = requestData.get("isbn");
		String condition = requestData.get("condition");
		String author = requestData.get("author");
		String publisher = requestData.get("publisher");
		String edition = requestData.get("edition");
		String description = requestData.get("description");
		String str_price = requestData.get("price");
		Double d_price = Double.valueOf(str_price);

		System.out.println(str_price);

		Listing.createOrEdit(null, description, d_price, title, author, isbn,
				publisher, edition);

		// return redirect(
		// routes.Application.index()
		// );
		return ok(newBook.render());
	}

	@Security.Authenticated(Secured.class)
	public static Result editBook(Long id) {
		String userid = session("userid");

		Listing editList = Listing.editListing(userid, id);
		if (editList != null) {
			return ok(editBook.render(editList));
		} else {
			return ok(index.render(null));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result showBook(Long bookId) {

//		Book book = Book.findById(bookId);
		Listing listing = Listing.findById(bookId);
		return ok(showBook.render(listing));

	}

	public static Result sendEmail() {
		DynamicForm requestData = Form.form().bindFromRequest();
		long bookId = Long.parseLong(requestData.get("id"));
		String reason = requestData.get("reason");
		String message = requestData.get("message");

//		Book book = Book.findById(bookId);
		Listing listing = Listing.findById(bookId);
		try{
			Email email = new SimpleEmail();
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(465);
			email.setAuthenticator(new DefaultAuthenticator("mcamara9@gmail.com", "mypassword"));
			email.setSSLOnConnect(true);
			email.setFrom("mcamara9@gmail.com");
			email.addTo("mcamara9@gmail.com");
			email.setSubject(reason +" "+ "id#"+bookId);
			email.setMsg(message);
			email.send();
				
				
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}

		// send email
		return ok(showBook.render(listing));
//		return ok("email sent");

	}

	@Security.Authenticated(Secured.class)
	public static Result deleteBook(Long id) {

		if (id != null) {
			String userid = session("userid");
			boolean flag = Listing.deleteListing(userid, id);
			if (flag) {
				// List<Listing> newListing=Listing.editListing(userid,id);
				System.out.println("wowww");
				List<Listing> afterDelete = Listing
						.findListingsBySeller(userid);
				return ok(listings.render(afterDelete));
			} else {
				return ok(index.render(null));
			}
		}
		return ok(index.render(null));
	}
}
