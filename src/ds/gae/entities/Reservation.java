package ds.gae.entities;

import java.util.Date;

import javax.jdo.annotations.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.appengine.api.datastore.Key;

@Entity(name = Reservation.KIND)
public class Reservation {

	public static final String KIND = "Reservation";

	/**
	 * Reservation is identified by (Car, ReservationID).
	 * 
	 * Since this is a child object, we cannot use an {@code @Id long}. Thus, we
	 * have to use a {@link Key}. Luckily, GAE can still auto-generate one for
	 * us.
	 * 
	 * The parent key is set through the owned many-to-one relation with
	 * {@link Car}.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	@ManyToOne(fetch = FetchType.EAGER)
	private Car car;

	@Embedded
	private Quote quote;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected Reservation() {
	}

	public Reservation(Quote quote, Car car) {
		this.quote = quote;
		this.car = car;
	}

	/******
	 * ID *
	 ******/

	public Car getCar() {
		return car;
	}

	public long getCarId() {
		return getCar().getId();
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
				getCarId(), getRentalPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((car == null) ? 0 : car.hashCode());
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
		if (car == null) {
			if (other.car != null)
				return false;
		} else if (!car.equals(other.car))
			return false;
		if (quote == null) {
			if (other.quote != null)
				return false;
		} else if (!quote.equals(other.quote))
			return false;
		return true;
	}

}