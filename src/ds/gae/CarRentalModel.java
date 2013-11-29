package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;

public class CarRentalModel {

	private static CarRentalModel instance;

	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}

	/**
	 * Get the car types available in the given car rental company.
	 * 
	 * @param crcName
	 *            the car rental company
	 * @return The list of car types (i.e. name of car type), available in the
	 *         given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getCarTypesNames(em, crcName);
		} finally {
			em.close();
		}
	}

	public Set<String> getCarTypesNames(EntityManager em, String crcName) {
		List<String> carTypeNames = em.createNamedQuery("CarType.namesInCompany", String.class)
				.setParameter("companyKey", CarRentalCompany.getKey(crcName)).getResultList();
		return new HashSet<String>(carTypeNames);
	}

	/**
	 * Get all registered car rental companies
	 * 
	 * @return the list of car rental companies
	 */
	public Collection<String> getAllRentalCompanyNames() {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getAllRentalCompanyNames(em);
		} finally {
			em.close();
		}
	}

	protected Collection<String> getAllRentalCompanyNames(EntityManager em) {
		return em.createNamedQuery("CarRentalCompany.allNames", String.class).getResultList();
	}

	public Collection<CarRentalCompany> getAllRentalCompanies() {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getAllRentalCompanies(em);
		} finally {
			em.close();
		}
	}

	protected Collection<CarRentalCompany> getAllRentalCompanies(EntityManager em) {
		return em.createNamedQuery("CarRentalCompany.all", CarRentalCompany.class).getResultList();
	}

	public CarRentalCompany getRentalCompany(String company) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getRentalCompany(em, company);
		} finally {
			em.close();
		}
	}

	protected CarRentalCompany getRentalCompany(EntityManager em, String company) {
		return em.find(CarRentalCompany.class, company);
	}

	public void addRentalCompany(CarRentalCompany company) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			addRentalCompany(em, company);
		} finally {
			em.close();
		}
	}

	protected void addRentalCompany(EntityManager em, CarRentalCompany company) {
		em.persist(company);
	}

	/**
	 * Create a quote according to the given reservation constraints (tentative
	 * reservation).
	 * 
	 * @param company
	 *            name of the car renter company
	 * @param renterName
	 *            name of the car renter
	 * @param constraints
	 *            reservation constraints for the quote
	 * @return The newly created quote.
	 * 
	 * @throws ReservationException
	 *             No car available that fits the given constraints.
	 */
	public Quote createQuote(String company, String renterName, ReservationConstraints constraints)
			throws ReservationException {
		EntityManager em = EMF.get().createEntityManager();
		try {
			CarRentalCompany crc = getRentalCompany(em, company);
			Quote out = null;
			if (crc != null) {
				out = crc.createQuote(constraints, renterName);
			} else {
				throw new ReservationException("CarRentalCompany not found.");
			}
			em.persist(crc);
			return out;
		} finally {
			em.close();
		}
	}

	/**
	 * Confirm the given quote.
	 * 
	 * @param q
	 *            Quote to confirm
	 * 
	 * @throws ReservationException
	 *             Confirmation of given quote failed.
	 */
	public void confirmQuote(Quote q) throws ReservationException {
		EntityManager em = EMF.get().createEntityManager();
		try {
			CarRentalCompany crc = getRentalCompany(em, q.getRentalCompany());
			crc.confirmQuote(q);
			em.persist(crc);
		} finally {
			em.close();
		}
	}

	/**
	 * Confirm the given list of quotes
	 * 
	 * @param quotes
	 *            the quotes to confirm
	 * @return The list of reservations, resulting from confirming all given
	 *         quotes.
	 * 
	 * @throws ReservationException
	 *             One of the quotes cannot be confirmed. Therefore none of the
	 *             given quotes is confirmed.
	 */
	public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {
		// TODO add implementation
		return null;
	}

	/**
	 * Get all reservations made by the given car renter.
	 * 
	 * @param renter
	 *            name of the car renter
	 * @return the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		// FIXME: use persistence instead

		EntityManager em = EMF.get().createEntityManager();
		try {
			List<Reservation> out = new ArrayList<Reservation>();
			for (CarRentalCompany crc : getAllRentalCompanies(em)) {
				for (Car c : crc.getCars()) {
					for (Reservation r : c.getReservations()) {
						if (r.getCarRenter().equals(renter)) {
							out.add(r);
						}
					}
				}
			}
			return out;
		} finally {
			em.close();
		}
	}

	/**
	 * Get the car types available in the given car rental company.
	 * 
	 * @param crcName
	 *            the given car rental company
	 * @return The list of car types in the given car rental company.
	 */
	public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
		// FIXME: use persistence instead

		CarRentalCompany crc = getRentalCompany(crcName);
		Collection<CarType> out = new ArrayList<CarType>(crc.getAllCarTypes());
		return out;
	}

	/**
	 * Get the list of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName
	 *            name of the car rental company
	 * @param carType
	 *            the given car type
	 * @return A list of car IDs of cars with the given car type.
	 */
	public Collection<Integer> getCarIdsByCarType(String crcName, CarType carType) {
		Collection<Integer> out = new ArrayList<Integer>();
		for (Car c : getCarsByCarType(crcName, carType)) {
			out.add((int) c.getId());
		}
		return out;
	}

	/**
	 * Get the amount of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName
	 *            name of the car rental company
	 * @param carType
	 *            the given car type
	 * @return A number, representing the amount of cars of the given car type.
	 */
	public int getAmountOfCarsByCarType(String crcName, CarType carType) {
		return this.getCarsByCarType(crcName, carType).size();
	}

	/*
	 * Get the list of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName name of the car rental company
	 * 
	 * @param carType the given car type
	 * 
	 * @return List of cars of the given car type (over all car rental
	 * companies)
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {
		// FIXME: use persistence instead

		EntityManager em = EMF.get().createEntityManager();
		try {
			List<Car> out = new ArrayList<Car>();
			for (CarRentalCompany crc : getAllRentalCompanies(em)) {
				for (Car c : crc.getCars()) {
					if (c.getType().equals(carType)) {
						out.add(c);
					}
				}
			}
			return out;
		} finally {
			em.close();
		}
	}

	/**
	 * Check whether the given car renter has reservations.
	 * 
	 * @param renter
	 *            the car renter
	 * @return True if the number of reservations of the given car renter is
	 *         higher than 0. False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;
	}

}