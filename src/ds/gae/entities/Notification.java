package ds.gae.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.Key;

@Entity(name = Notification.KIND)
@NamedQueries({ @NamedQuery(name = "Notification.byRenter",
		query = "SELECT n FROM Notification n WHERE n.renterKey = :renterKey "
				+ "ORDER BY n.timestamp DESC") })
public class Notification {

	public static final String KIND = "Notification";

	/**
	 * Notification is identified by (Renter, NotificationID).
	 * 
	 * Since this is a child object, we cannot use an {@code @Id long}. Thus, we
	 * have to use a {@link Key}. Luckily, GAE can still auto-generate one for
	 * us.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String encodedKey;

	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key renterKey;

	private String message;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	protected Notification() {
	}

	public Notification(String renter, String message, Date timestamp) {
		this(Renter.getKey(renter), message, timestamp);
	}

	public Notification(Key renterKey, String message, Date timestamp) {
		this.renterKey = renterKey;
		this.message = message;
		this.timestamp = timestamp;
	}

	public Key getRenterKey() {
		return renterKey;
	}

	public String getRenterName() {
		return getRenterKey().getName();
	}

	public String getMessage() {
		return message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
