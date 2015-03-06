package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name="book_price")
public class Price extends Model {
	@Id
	public Long id;
	
	@OneToOne
	@JoinColumn(name = "book_id", referencedColumnName = "id")
	public Book book;
	
	public Double buy_new_price;
	public Double buy_used_price;
	public Double rent_new_price;
	public Double rent_used_price;
	
	public String url;
}
