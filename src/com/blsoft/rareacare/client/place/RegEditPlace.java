package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

public class RegEditPlace extends SecuredPlace {
	private String regsEditName;  // Zawiera id_rejestru + '-' + id_instancji rejestru 
	
	public RegEditPlace(String token)
	{
		super();
		this.regsEditName = token;
	}

	public String getRegEditName()
	{
		return regsEditName;
	}
	
	public Integer getRegId() {
		int i = regsEditName.indexOf('_');
		if (i>0) {
			return Integer.parseInt(regsEditName.substring(0, i));
		}
		else 
			return 0;		
	}

	public Integer getInstId() {
		int i = regsEditName.indexOf('_');
		if (i>0) {
			return Integer.parseInt(regsEditName.substring(i+1));
		}
		else 
			return 0;		
	}

	public static class Tokenizer implements PlaceTokenizer<RegEditPlace>
	{
		@Override
		public String getToken(RegEditPlace place)
		{
			return place.getRegEditName();
		}

		@Override
		public RegEditPlace getPlace(String token)
		{
			return new RegEditPlace(token);
		}
	}
}
