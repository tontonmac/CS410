package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name="book_condition")
public class Condition extends Model {
	
	@Id
	public Long id;
	
	public String name;
}
