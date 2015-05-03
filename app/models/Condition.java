package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

import play.db.ebean.Model;

@Entity
@Table(name="book_condition")
public class Condition extends Model {

	@Id
	public Long id;

	public String name;

	public static Finder<Long,Condition> find = new Finder<Long,Condition>(Long.class, Condition.class);

	 public static List<Condition> findAll() {
	        return find.all();
    }
	 
	 public static Condition findById(long id) {
		 return find.byId(id);
	 }
}
