package ds.gae.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import ds.gae.ConfirmQuotesParams;
import ds.gae.SerializationUtils;
import ds.gae.entities.Quote;
import ds.gae.view.JSPSite;

@SuppressWarnings("serial")
public class ConfirmQuotesServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		HashMap<String, ArrayList<Quote>> allQuotes = (HashMap<String, ArrayList<Quote>>) session
				.getAttribute("quotes");

		String renter = (String) session.getAttribute("renter");

		// collect quotes here and in session
		ArrayList<Quote> qs = new ArrayList<Quote>();

		for (String crcName : allQuotes.keySet()) {
			qs.addAll(allQuotes.get(crcName));
		}

		session.setAttribute("quotes", new HashMap<String, ArrayList<Quote>>());

		ConfirmQuotesParams params = new ConfirmQuotesParams(qs, renter);

		// serialize quotes
		byte[] serializedParams = SerializationUtils.serialize(params);

		// create task and add it to the (default) queue
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions options = TaskOptions.Builder.withUrl("/worker").payload(
				serializedParams);

		queue.add(options);

		// TODO what to do when the confirmation was queued?
		// If you wish confirmQuotesReply.jsp to be shown to the client as
		// a response of calling this servlet, please replace the following line
		// with:
		// resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
		resp.sendRedirect(JSPSite.CREATE_QUOTES.url());
	}
}
