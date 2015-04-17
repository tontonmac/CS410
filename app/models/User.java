package models;

import java.net.URL;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class User extends Model {

	@Id
	public Long id;
	
	@Constraints.Required
	public String email;
	
	@Constraints.Required
	public String password;
	
	public String first_name;
	public String last_name;
	
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    public Department department;
    
    public String photo_url;
    
    public static User authenticate(String email, String password) {
    	User user = findUserByEmail(email);
    	if (user.password.equals(password)) {
    		return user;
    	}
    	
    	return null;
    }
    
    public static Finder<Long,User> find = new Finder<Long,User>(Long.class, User.class);
    
    public static User findUserByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }
    
    public static User createUser(String firstName, String lastName, String password, String email) {
    	User user = new User();
    	user.first_name = firstName;
    	user.last_name = lastName;
    	user.email = email;
    	user.password = password;
    	
    	user.save();
		return user;
    }
}
