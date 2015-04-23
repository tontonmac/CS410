package controllers;

import java.sql.Date;
import java.util.List;

import models.Department;
import models.Listing;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
//import play.api.mvc.*;
import views.html.listings;

public class UpdateData extends Controller{

	public static class Update {


	    public Long id;

	    public User listedBy;

	    public Department department;

	    public String description;
	    public Double price;
	    public String title;
	    public String author;
	    public String isbn;
	    public Date copyright_date;
	    public String publisher;
	    public String image_path;
	    public String edition;
	    public Integer num_views;

	    
	  
	}

    
    
    public static Result updateForm() {
    	
    	DynamicForm requestData = Form.form().bindFromRequest();
	        /*System.out.println();
	        
	        System.out.println();
	        System.out.println();
	        System.out.println();
	        System.out.println();
	        System.out.println(requestData.get("copyright_date"));
	        System.out.println();
	        
	        System.out.println();
	        
	        System.out.println(requestData.get("condition"));
	        
	        */
        Long id=Long.parseLong(requestData.get("id"));
        
        
        String description=requestData.get("description");
        Double price=Double.parseDouble(requestData.get("price"));
        String title=requestData.get("title");
        String author=requestData.get("author");
        String isbn=requestData.get("isbn");
        Date copyright_date=null;
        String publisher=requestData.get("publisher");
        String edition=requestData.get("edition");
    Listing listing=Listing.Edit(id,description,price,title,author,isbn,copyright_date,publisher,edition);
	    if(listing!=null)
	    {
	    	String listedBy=session().get("userid");
	    	List<Listing> listings12 = Listing.findListingsBySeller(listedBy);
	        return ok(listings.render(listings12));
	    }
	        
     	return null;
       
    }

	
    
    
	
}







