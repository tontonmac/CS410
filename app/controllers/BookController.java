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
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeCodabar;
import com.itextpdf.text.pdf.BarcodeDatamatrix;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BarcodeEANSUPP;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.BarcodePostnet;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import models.Condition;

public class BookController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result newBook() {
		return ok(newBook.render());
	}

	public static Result submitNewBook() throws IOException, DocumentException{
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
		Listing.create(Long.parseLong(session().get("userid")), description, d_price, title, author, isbn,
                publisher, edition);

		// return redirect(
		// routes.Application.index()
		// );

/*		      final String RESULT = "C:\\barcode.pdf";

		        // step 1
		        Document document = new Document(new Rectangle(340, 842));
		        // step 2
		        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
		        // step 3
		        document.open();
		        // step 4
		        PdfContentByte cb = writer.getDirectContent();

		        document.add(new Paragraph("Beacon Books"));
		        document.add(new Paragraph("ISBN BARCODE"));
		        BarcodeEAN codeEAN = new BarcodeEAN();
		        codeEAN.setCodeType(com.itextpdf.text.pdf.Barcode.EAN13);
		        if(isbn.length()==13)
		        {
		        codeEAN.setCode(isbn);
		        BarcodeEAN codeSUPP = new BarcodeEAN();
		        codeSUPP.setCodeType(com.itextpdf.text.pdf.Barcode.SUPP5);
		        codeSUPP.setCode("55999");
		        codeSUPP.setBaseline(-2);
		        BarcodeEANSUPP eanSupp = new BarcodeEANSUPP(codeEAN, codeSUPP);
		        document.add(eanSupp.createImageWithBarcode(cb, null, BaseColor.BLUE));

		        }
		        document.close();*/

        flash("success", "Listing created successfully");
        return redirect( controllers.routes.Selling.listings() );
	}

	@Security.Authenticated(Secured.class)
	public static Result editBook(Long id) {
		String userid = session("userid");

		Listing editList = Listing.editListing(userid, id);
		if (editList != null) {
			List<Condition> conditions = Condition.findAll();
			return ok(editBook.render(editList,conditions));
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
                flash("success", "Your listing was removed successfully");
				// List<Listing> newListing=Listing.editListing(userid,id);
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
