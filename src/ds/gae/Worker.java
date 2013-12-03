package ds.gae;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ds.gae.entities.Quote;

public class Worker extends HttpServlet {
	private static final long serialVersionUID = -7058685883212377590L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Deserialize quotes
		ConfirmQuotesParams params = SerializationUtils.deserialize(req.getInputStream());
		ArrayList<Quote> quotes = params.getQuotes();
		String renter = params.getRenter();

		try {
			// Try to confirm quotes
			CarRentalModel.get().confirmQuotes(quotes);
			// Success
			String message = String.format("%d quote(s) successfully confirmed", quotes.size());
			CarRentalModel.get().addNotification(params.getRenter(), message);
		} catch (ReservationException e) {
			// Failure
			// e.printStackTrace();
			String message = String.format("Could not confirm all %d quote(s). %s", quotes.size(),
					e.getMessage());
			CarRentalModel.get().addNotification(renter, message);
		}
	}
}
