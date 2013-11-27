package ds.gae.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity(name = Car.KIND)
public class Car {

	public static final String KIND = "Car";

	/**
	 * Car is identified by (CarType, CarID).
	 * 
	 * Since this is a child object, we cannot use an {@code @Id long}. Thus, we
	 * have to use a {@link Key}. Luckily, GAE can still auto-generate one for
	 * us.
	 * 
	 * The parent key is set through the owned many-to-one relation with
	 * {@link CarType}.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String encodedKey;

	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key carTypeKey;

	/*
	 * TODO Can we fetch the parent entity from our own key instead of needing
	 * an extra field?
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@Unowned
	private CarType carType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
	private Set<Reservation> reservations = new HashSet<Reservation>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected Car() {
	}

	public Car(CarType type) {
		setType(type);
		this.reservations = new HashSet<Reservation>();
	}

	/******
	 * ID *
	 ******/

	public Key getKey() {
		return KeyFactory.stringToKey(encodedKey);
	}

	public long getId() {
		return getKey().getId();
	}

	/************
	 * CAR TYPE *
	 ************/

	public CarType getType() {
		return carType;
	}

	protected void setType(CarType type) {
		carType = type;
		carTypeKey = type.getKey();
	}

	/****************
	 * RESERVATIONS *
	 ****************/

	public Set<Reservation> getReservations() {
		return reservations;
	}

	public boolean isAvailable(Date start, Date end) {
		if (!start.before(end))
			throw new IllegalArgumentException("Illegal given period");

		for (Reservation reservation : reservations) {
			if (reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
				continue;
			return false;
		}
		return true;
	}

	public void addReservation(Reservation res) {
		reservations.add(res);
	}

	public void removeReservation(Reservation reservation) {
		// equals-method for Reservation is required!
		reservations.remove(reservation);
	}

	@Override
	public String toString() {
		return "Car [carType=" + carType + ", reservations=" + reservations + "]";
	}

}