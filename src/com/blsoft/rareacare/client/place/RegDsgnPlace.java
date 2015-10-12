package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

public class RegDsgnPlace extends SecuredPlace {
	private String regsDsgnName;
	
	public RegDsgnPlace(String token)
	{
		super();
		this.regsDsgnName = token;
	}

	public String getRegDsgnName()
	{
		return regsDsgnName;
	}

	public static class Tokenizer implements PlaceTokenizer<RegDsgnPlace>
	{
		@Override
		public String getToken(RegDsgnPlace place)
		{
			return place.getRegDsgnName();
		}

		@Override
		public RegDsgnPlace getPlace(String token)
		{
			return new RegDsgnPlace(token);
		}
	}
}
