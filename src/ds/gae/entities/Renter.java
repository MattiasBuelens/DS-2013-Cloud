package ds.gae.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Entity(name = Renter.KIND)
public class Renter {

	public static final String KIND = "Renter";

	@Id
	private String renter;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Notification> notifications = new ArrayList<>();

	protected Renter() {
	}

	public Renter(String renter) {
		this.renter = renter;
	}

	public String getRenter() {
		return renter;
	}

	public Key getKey() {
		return getKey(getRenter());
	}

	public static Key getKey(String renter) {
		return KeyFactory.createKey(KIND, renter);
	}

	public List<Notification> getNotifications() {
		return Collections.unmodifiableList(notifications);
	}

	public void addNotification(String message, Date timestamp) {
		addNotification(new Notification(getRenter(), message, timestamp));
	}

	public void addNotification(Notification notification) {
		notifications.add(notification);
	}

	public void removeNotification(Notification notification) {
		notifications.remove(notification);
	}

}
