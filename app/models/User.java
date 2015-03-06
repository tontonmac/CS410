package models;

import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;

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
	
    @OneToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    public Department department;
    
    public String photo_url;
}
