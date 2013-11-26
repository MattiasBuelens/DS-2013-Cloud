package ds.gae.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;

@Entity(name = Car.KIND)
public class Car {

	public static final String KIND = "Car";

	/*
	 * Car is identified by (CarType, CarID).
	 * 
	 * Since this is a child object, we cannot use an @Id long. Thus, we have to
	 * use a Key. Luckily, GAE can still auto-generate one for us.
	 * 
	 * The parent key is set through the owned many-to-one relation with
	 * CarType.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	@ManyToOne
	private CarType carType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
	private Set<Reservation> reservations;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	protected Car() {
	}

	public Car(CarType type) {
		this.carType = type;
		this.reservations = new HashSet<Reservation>();
	}

	/******
	 * ID *
	 ******/

	public Key getKey() {
		return key;
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