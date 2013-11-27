package ds.gae.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unowned;

import ds.gae.ReservationException;

@Entity(name = CarRentalCompany.KIND)
@NamedQueries({
		@NamedQuery(name = "CarRentalCompany.all", query = "SELECT crc FROM CarRentalCompany crc"),
		@NamedQuery(name = "CarRentalCompany.allNames",
				query = "SELECT crc.name FROM CarRentalCompany crc") })
public class CarRentalCompany {

	public static final String KIND = "CarRentalCompany";

	private static final Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

	/**
	 * CarRentalCompany is identified by (companyName).
	 * 
	 * Since this is a root entity, a simple {@code @Id String} does the trick.
	 */
	@Id
	private String name;

	/**
	 * All car types in this company.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	private Set<CarType> carTypes = new HashSet<CarType>();

	@Transient
	private transient Map<String, CarType> carTypeMap = null;

	/**
	 * All cars in this company.
	 * 
	 * This relation is unowned, as the {@link CarType} already owns the
	 * {@link Car}s. Since we already own {@link CarType}s, we indirectly also
	 * own the {@link Car}s anyway.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@Unowned
	private Set<Car> cars = new HashSet<Car>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, Set<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		for (Car car : cars) {
			addCar(car);
		}
	}

	/********
	 * NAME *
	 ********/

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public Key getKey() {
		return getKey(name);
	}

	public static Key getKey(String companyName) {
		return KeyFactory.createKey(KIND, companyName);
	}

	/*************
	 * CAR TYPES *
	 *************/

	public Collection<CarType> getAllCarTypes() {
		return Collections.unmodifiableSet(carTypes);
	}

	protected Map<String, CarType> getCarTypeMap() {
		if (carTypeMap == null) {
			// Lazily fill map from set
			carTypeMap = new HashMap<String, CarType>();
			for (CarType carType : getAllCarTypes()) {
				carTypeMap.put(carType.getName(), carType);
			}
		}
		return carTypeMap;
	}

	public boolean hasCarType(String carTypeName) {
		return getCarTypeMap().containsKey(carTypeName);
	}

	public CarType getCarType(String carTypeName) {
		if (hasCarType(carTypeName))
			return getCarTypeMap().get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name "
				+ carTypeName);
	}

	public boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[] { name,
				carTypeName });
		if (hasCarType(carTypeName))
			return getAvailableCarTypes(start, end).contains(getCarTypeMap().get(carTypeName));
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name "
				+ carTypeName);
	}

	public Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}

	protected void addCarType(CarType carType) {
		if (!hasCarType(carType.getName())) {
			getCarTypeMap().put(carType.getName(), carType);
			carTypes.add(carType);
		}
	}

	protected void removeCarType(CarType carType) {
		carTypes.remove(carType);
		getCarTypeMap().remove(carType.getName());
	}

	/*********
	 * CARS *
	 *********/

	public Set<Car> getCars() {
		return cars;
	}

	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	protected void addCar(Car car) {
		addCarType(car.getType());
		cars.add(car);
	}

	protected void removeCar(Car car) {
		cars.remove(car);
	}

	/****************
	 * RESERVATIONS *
	 ****************/

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
				new Object[] { name, client, constraints.toString() });

		CarType type = getCarType(constraints.getCarType());

		if (!isAvailable(constraints.getCarType(), constraints.getStartDate(),
				constraints.getEndDate()))
			throw new ReservationException("<" + name
					+ "> No cars available to satisfy the given constraints.");

		double price = calculateRentalPrice(type.getRentalPricePerDay(),
				constraints.getStartDate(), constraints.getEndDate());

		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(),
				constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay
				* Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24D));
	}

	public Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[] { name, quote.toString() });
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(),
				quote.getEndDate());
		if (availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type "
					+ quote.getCarType() + " are unavailable from " + quote.getStartDate() + " to "
					+ quote.getEndDate());
		Car car = availableCars.get((int) (Math.random() * availableCars.size()));

		Reservation res = new Reservation(quote, car);
		car.addReservation(res);
		return res;
	}

	public void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}",
				new Object[] { name, res.toString() });
		res.getCar().removeReservation(res);
	}

}