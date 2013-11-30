package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

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

	protected Set<String> getCarTypesNames(EntityManager em, String crcName) {
		List<String> carTypeNames = em.createNamedQuery("CarType.namesByCompany", String.class)
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

	/**
	 * Get the rental company with the given name.
	 * 
	 * @param crcName
	 *            name of the car rental company
	 * @return The car rental company, or null if not found.
	 */
	public CarRentalCompany getRentalCompany(String crcName) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getRentalCompany(em, crcName);
		} finally {
			em.close();
		}
	}

	protected CarRentalCompany getRentalCompany(EntityManager em, String company) {
		return em.find(CarRentalCompany.class, company);
	}

	/**
	 * Register a car rental company.
	 * 
	 * @param company
	 *            new car rental company
	 */
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
			if (crc != null) {
				return crc.createQuote(constraints, renterName);
			} else {
				throw new ReservationException("CarRentalCompany not found.");
			}
		} finally {
			em.close();
		}
	}

	/**
	 * Confirm the given quote.
	 * 
	 * @param q
	 *            Quote to confirm
	 * @throws ReservationException
	 *             Confirmation of given quote failed.
	 */
	public Reservation confirmQuote(Quote q) throws ReservationException {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return confirmQuote(em, q);
		} finally {
			em.close();
		}
	}

	protected Reservation confirmQuote(EntityManager em, Quote q) throws ReservationException {
		CarRentalCompany crc = getRentalCompany(em, q.getRentalCompany());
		Reservation res = crc.confirmQuote(q);
		return res;
	}

	/**
	 * Confirm the given list of quotes.
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
		// Group the quotes by company
		Map<String, List<Quote>> groupedQuotes = groupQuotesByCompany(quotes);
		List<Reservation> reservations = new ArrayList<Reservation>();
		try {
			// Confirm each group (in a transaction)
			for (List<Quote> group : groupedQuotes.values()) {
				reservations.addAll(confirmQuotesInCompany(group));
			}
			return reservations;
		} catch (ReservationException e) {
			// Cancel the committed reservations (outside of a transaction)
			for (Reservation res : reservations) {
				cancelReservation(res);
			}
			throw e;
		}
	}

	/**
	 * Confirm the given list of quotes <strong>for one company</strong>.
	 * 
	 * @param quotes
	 *            the quotes to confirm
	 * @return The list of reservations, resulting from confirming all given
	 *         quotes.
	 * @throws ReservationException
	 *             One of the quotes cannot be confirmed. Therefore none of the
	 *             given quotes is confirmed.
	 */
	protected List<Reservation> confirmQuotesInCompany(List<Quote> quotes)
			throws ReservationException {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction t = em.getTransaction();
		try {
			// Confirm the quotes inside a transaction
			// This is allowed, as all the reservations belong to the same
			// entity group (with the owning company as root entity)
			t.begin();
			List<Reservation> reservations = new ArrayList<>();
			for (Quote q : quotes) {
				reservations.add(confirmQuote(em, q));
			}
			t.commit();
			return reservations;
		} catch (ReservationException e) {
			// Roll back the transaction
			t.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	/**
	 * Group the given quotes by company.
	 * 
	 * @param quotes
	 *            the quotes to group
	 * @return A map of grouped quotes with the company name as keys.
	 */
	protected Map<String, List<Quote>> groupQuotesByCompany(Collection<Quote> quotes) {
		Map<String, List<Quote>> groupedQuotes = new HashMap<>();
		for (Quote quote : quotes) {
			List<Quote> group = groupedQuotes.get(quote.getRentalCompany());
			if (group == null) {
				group = new ArrayList<>();
				groupedQuotes.put(quote.getRentalCompany(), group);
			}
			group.add(quote);
		}
		return groupedQuotes;
	}

	/**
	 * Cancel the given reservation.
	 * 
	 * @param res
	 *            the reservation to confirm
	 */
	public void cancelReservation(Reservation res) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			cancelReservation(em, res);
		} finally {
			em.close();
		}
	}

	protected void cancelReservation(EntityManager em, Reservation res) {
		CarRentalCompany crc = getRentalCompany(em, res.getRentalCompany());
		crc.cancelReservation(res);
	}

	/**
	 * Get all reservations made by the given car renter.
	 * 
	 * @param renter
	 *            name of the car renter
	 * @return the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getReservations(em, renter);
		} finally {
			em.close();
		}
	}

	protected List<Reservation> getReservations(EntityManager em, String renter) {
		return em.createNamedQuery("Reservation.byRenter", Reservation.class)
				.setParameter("renter", renter).getResultList();
	}

	/**
	 * Get the car types available in the given car rental company.
	 * 
	 * @param crcName
	 *            the given car rental company
	 * @return The list of car types in the given car rental company.
	 */
	public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
		return getCarTypesOfCarRentalCompany_Query(crcName);
	}

	protected Collection<CarType> getCarTypesOfCarRentalCompany_Query(String crcName) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getCarTypesOfCarRentalCompany(em, crcName);
		} finally {
			em.close();
		}
	}

	protected Collection<CarType> getCarTypesOfCarRentalCompany(EntityManager em, String crcName) {
		return em.createNamedQuery("CarType.byCompany", CarType.class)
				.setParameter("companyKey", CarRentalCompany.getKey(crcName)).getResultList();
	}

	/*
	 * TODO Better? Two finds are usually faster than one query.
	 * 
	 * See: http://goo.gl/aEBVEC
	 */
	protected Collection<CarType> getCarTypesOfCarRentalCompany_TwoFinds(String crcName) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return new ArrayList<CarType>(getRentalCompany(crcName).getAllCarTypes());
		} finally {
			em.close();
		}
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
	/*
	 * TODO This breaks the signature (was Collection<Integer>), are we allowed
	 * to do this?
	 * 
	 * The Datastore uses 64-bit numerical IDs. We cannot fully represent these
	 * in a 32-bit integer without losing information. Would this cause problems
	 * for automated tests to be run on our code?
	 */
	public Collection<Long> getCarIdsByCarType(String crcName, CarType carType) {
		Collection<Key> keys = getCarKeysByCarType(crcName, carType);
		Collection<Long> out = new ArrayList<Long>();
		for (Key key : keys) {
			out.add(key.getId());
		}
		return out;
	}

	protected Collection<Key> getCarKeysByCarType(String crcName, CarType carType) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getCarKeysByCarType(em, crcName, carType);
		} finally {
			em.close();
		}
	}

	protected Collection<Key> getCarKeysByCarType(EntityManager em, String crcName, CarType carType) {
		List<String> encodedKeys = em.createNamedQuery("Car.keyByType", String.class)
				.setParameter("carTypeKey", carType.getKey()).getResultList();
		List<Key> keys = new ArrayList<>();
		for (String encodedKey : encodedKeys) {
			keys.add(KeyFactory.stringToKey(encodedKey));
		}
		return keys;
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
		EntityManager em = EMF.get().createEntityManager();
		try {
			return (int) getAmountOfCarsByCarType(em, crcName, carType);
		} finally {
			em.close();
		}
	}

	protected long getAmountOfCarsByCarType(EntityManager em, String crcName, CarType carType) {
		return em.createNamedQuery("Car.countByType", Long.class)
				.setParameter("carTypeKey", carType.getKey()).getSingleResult();
	}

	/**
	 * Get the list of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName
	 *            name of the car rental company
	 * @param carType
	 *            the given car type
	 * @return List of cars of the given car type (over all car rental
	 *         companies)
	 */
	protected Collection<Car> getCarsByCarType(String crcName, CarType carType) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return getCarsByCarType(em, crcName, carType);
		} finally {
			em.close();
		}
	}

	protected Collection<Car> getCarsByCarType(EntityManager em, String crcName, CarType carType) {
		return em.createNamedQuery("Car.byType", Car.class)
				.setParameter("carTypeKey", carType.getKey()).getResultList();
	}

	/**
	 * Get the amount of reservations by the given car renter.
	 * 
	 * @param renter
	 *            the car renter
	 * @return A number, representing the amount of reservations by the given
	 *         car renter.
	 */
	public int getAmountOfReservations(String renter) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return (int) getAmountOfReservations(em, renter);
		} finally {
			em.close();
		}
	}

	protected long getAmountOfReservations(EntityManager em, String renter) {
		return em.createNamedQuery("Reservation.countByRenter", Long.class)
				.setParameter("renter", renter).getSingleResult();
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
		return getAmountOfReservations(renter) > 0;
	}

}
