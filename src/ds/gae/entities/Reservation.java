package ds.gae.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;

@Entity(name = Reservation.KIND)
public class Reservation extends Quote {

	public static final String KIND = "Reservation";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key carKey;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected Reservation() {
	}

	public Reservation(Quote quote, Car car) {
		super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), quote.getRentalCompany(), quote
				.getCarType(), quote.getRentalPrice());
		this.carKey = car.getKey();
	}

	/******
	 * ID *
	 ******/

	public Key getCarKey() {
		return carKey;
	}

	/*************
	 * TO STRING *
	 *************/

	@Override
	public String toString() {
		return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f",
				getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarKey(),
				getRentalPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		Reservation other = (Reservation) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}