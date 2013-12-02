package ds.gae;

import java.io.Serializable;
import java.util.ArrayList;

import ds.gae.entities.Quote;

public class ConfirmQuotesParams implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ArrayList<Quote> quotes;
	private final String renter;

	public ConfirmQuotesParams(ArrayList<Quote> quotes, String renter) {
		super();
		this.quotes = quotes;
		this.renter = renter;
	}

	public ArrayList<Quote> getQuotes() {
		return quotes;
	}

	public String getRenter() {
		return renter;
	}

}
