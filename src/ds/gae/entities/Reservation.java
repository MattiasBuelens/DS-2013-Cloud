package ds.gae.entities;

import java.util.Date;

import javax.jdo.annotations.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;

@Entity(name = Reservation.KIND)
@NamedQueries({
		@NamedQuery(name = "Reservation.byRenter",
				query = "SELECT res FROM Reservation res WHERE res.quote.carRenter = :renter"),
		@NamedQuery(
				name = "Reservation.countByRenter",
				query = "SELECT COUNT(res) FROM Reservation res WHERE res.quote.carRenter = :renter") })
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
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String encodedKey;

	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key carKey;

	@Embedded
	@OneToOne(fetch = FetchType.EAGER)
	private Quote quote;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected Reservation() {
	}

	public Reservation(Quote quote, Key carKey) {
		this.quote = quote;
		this.carKey = carKey;
	}

	public Reservation(Quote quote, Car car) {
		this(quote, car.getKey());
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
				getCarId(), getRentalPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((encodedKey == null) ? 0 : encodedKey.hashCode());
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
		if (encodedKey == null) {
			if (other.encodedKey != null)
				return false;
		} else if (!encodedKey.equals(other.encodedKey))
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