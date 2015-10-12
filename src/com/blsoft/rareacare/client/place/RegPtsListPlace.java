package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

public class RegPtsListPlace extends SecuredPlace {
	private String regPtsList;
	
	/** Token zawiera id rejestru, który chcemy edytować*/
	public RegPtsListPlace(String token)
	{
		super();
		this.regPtsList = token;
	}

	public String getRegPtsListName()
	{
		return regPtsList;
	}
	
	public int getRegId() {
		return Integer.parseInt(regPtsList);
	}

	public static class Tokenizer implements PlaceTokenizer<RegPtsListPlace>
	{
		@Override
		public String getToken(RegPtsListPlace place)
		{
			return place.getRegPtsListName();
		}

		@Override
		public RegPtsListPlace getPlace(String token)
		{
			return new RegPtsListPlace(token);
		}
	}
}
