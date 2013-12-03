package ds.gae;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Worker extends HttpServlet {
	private static final long serialVersionUID = -7058685883212377590L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// deserialize quotes
		ConfirmQuotesParams params = SerializationUtils.deserialize(req
				.getInputStream());

		// try to confirm quotes
		try {
			CarRentalModel.get().confirmQuotes(params.getQuotes());
		} catch (ReservationException e) {
			// TODO what to do when a confirmation fails?
			// req.getSession().setAttribute("errorMsg",
			// ViewTools.encodeHTML(e.getMessage()));
			// resp.sendRedirect(JSPSite.RESERVATION_ERROR.url());
			e.printStackTrace();
		}

		// TODO what to do when a confirmation was handled?
		// add data to datastore for generating notifications?
		// redirect to new page?
	}
}
