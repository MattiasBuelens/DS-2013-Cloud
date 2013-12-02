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
		// get renter name
		String renter = req.getParameter("renter");
		
		// deserialize quotes
		ArrayList<Quote> quotes = SerializationUtils.deserialize(req.getInputStream());
		
		// try to confirm quotes
		try{
			CarRentalModel.get().confirmQuotes(quotes);
		} catch (ReservationException e) {
			// TODO
//			req.getSession().setAttribute("errorMsg", ViewTools.encodeHTML(e.getMessage()));
//			resp.sendRedirect(JSPSite.RESERVATION_ERROR.url());
			e.printStackTrace();
		}
		
	}
}
