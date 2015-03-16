package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Term extends Model {
	
	@Id
	public Long id;
	
	@Constraints.Required
	public String name;

    @Constraints.Required
    public String url;

	public static Finder<Long,Term> find = new Finder<Long,Term>(Long.class, Term.class);

    public static Term findUnique(String name) {
        return find.where().eq("name", name).findUnique();
    }
}