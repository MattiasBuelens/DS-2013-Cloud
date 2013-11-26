package ds.gae.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;

@Entity(name = CarType.KIND)
@NamedQueries({ @NamedQuery(name = "CarType.namesInCompany",
		query = "SELECT ct.name FROM CarType ct WHERE company = :companyName") })
public class CarType {

	public static final String KIND = "CarType";

	// TODO Fix me!
	@Id
	private Key key;

	@ManyToOne
	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private String company;

	@Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true")
	private String name;

	private int nbOfSeats;
	private boolean smokingAllowed;
	private double rentalPricePerDay;
	// trunk space in liters
	private float trunkSpace;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected CarType() {
	}

	public CarType(String company, String name, int nbOfSeats, float trunkSpace,
			double rentalPricePerDay, boolean smokingAllowed) {
		this.company = company;
		this.name = name;
		this.nbOfSeats = nbOfSeats;
		this.trunkSpace = trunkSpace;
		this.rentalPricePerDay = rentalPricePerDay;
		this.smokingAllowed = smokingAllowed;
	}

	public Key getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public int getNbOfSeats() {
		return nbOfSeats;
	}

	public boolean isSmokingAllowed() {
		return smokingAllowed;
	}

	public double getRentalPricePerDay() {
		return rentalPricePerDay;
	}

	public float getTrunkSpace() {
		return trunkSpace;
	}

	/*************
	 * TO STRING *
	 *************/

	@Override
	public String toString() {
		return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]",
				getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(),
				getTrunkSpace());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarType other = (CarType) obj;
		if (company == null) {
			if (other.company != null)
				return false;
		} else if (!company.equals(other.company))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}