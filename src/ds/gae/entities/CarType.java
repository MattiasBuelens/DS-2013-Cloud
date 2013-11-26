package ds.gae.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Entity(name = CarType.KIND)
@NamedQueries({ @NamedQuery(name = "CarType.namesInCompany",
		query = "SELECT ct.name FROM CarType ct WHERE companyKey = :companyKey") })
public class CarType {

	public static final String KIND = "CarType";

	/*
	 * CarType is identified by (CarRentalCompany, carTypeName).
	 * CarRentalCompany is identified by (companyName).
	 * 
	 * Since all these values are known at construction, the complete key can be
	 * created right away.
	 * 
	 * As we still want to query on the company, we use the extensions to
	 * decompose the key into fields. These fields can then be used in JPQL
	 * queries.
	 */
	@Id
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String encodedKey;

	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key companyKey;

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

	public CarType(String companyName, String name, int nbOfSeats, float trunkSpace,
			double rentalPricePerDay, boolean smokingAllowed) {
		this.companyKey = CarRentalCompany.getKey(companyName);
		this.name = name;
		this.encodedKey = KeyFactory.keyToString(getKey(companyName, name));
		this.nbOfSeats = nbOfSeats;
		this.trunkSpace = trunkSpace;
		this.rentalPricePerDay = rentalPricePerDay;
		this.smokingAllowed = smokingAllowed;
	}

	public Key getKey() {
		return KeyFactory.stringToKey(encodedKey);
	}

	public static Key getKey(String companyName, String name) {
		return KeyFactory.createKey(CarRentalCompany.getKey(companyName), KIND, name);
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
		result = prime * result + ((encodedKey == null) ? 0 : encodedKey.hashCode());
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
		if (encodedKey == null) {
			if (other.encodedKey != null)
				return false;
		} else if (!encodedKey.equals(other.encodedKey))
			return false;
		return true;
	}
}