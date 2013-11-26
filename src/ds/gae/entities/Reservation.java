package ds.gae.entities;

import java.util.Date;

import javax.jdo.annotations.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;

@Entity(name = Reservation.KIND)
public class Reservation {

	public static final String KIND = "Reservation";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	@ManyToOne
	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key carKey;

	@Embedded
	private Quote quote;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected Reservation() {
	}

	public Reservation(Quote quote, Car car) {
		this.quote = quote;
		this.carKey = car.getKey();
	}

	/******
	 * ID *
	 ******/

	public Key getCarKey() {
		return carKey;
	}

	public long getCarId() {
		return getCarKey().getId();
	}

	public Quote getQuote() {
		return quote;
	}

	public Date getStartDate() {
		return getQuote().getStartDate();
	}

	public Date getEndDate() {
		return getQuote().getEndDate();
	}

	public String getCarRenter() {
		return getQuote().getCarRenter();
	}

	public String getRentalCompany() {
		return getQuote().getRentalCompany();
	}

	public double getRentalPrice() {
		return getQuote().getRentalPrice();
	}

	public String getCarType() {
		return getQuote().getCarType();
	}

	/*************
	 * TO STRING *
	 *************/

	@Override
	public String toString() {
		return String.format(
				"Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f",
				getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(),
				getCarKey(), getRentalPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((carKey == null) ? 0 : carKey.hashCode());
		result = prime * result + ((quote == null) ? 0 : quote.hashCode());
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
		Reservation other = (Reservation) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (carKey == null) {
			if (other.carKey != null)
				return false;
		} else if (!carKey.equals(other.carKey))
			return false;
		if (quote == null) {
			if (other.quote != null)
				return false;
		} else if (!quote.equals(other.quote))
			return false;
		return true;
	}

}