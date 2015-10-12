package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * Główne miejsce apliakacji po zalogowaniu - zawiera listę rejestrów i menu 
 * @author bartek
 *
 */
public class RegsPlace extends SecuredPlace {
	
	public static class Tokenizer implements PlaceTokenizer<RegsPlace>
	{
		@Override
		public String getToken(RegsPlace place)
		{
			return null;
		}

		@Override
		public RegsPlace getPlace(String token)
		{
			return new RegsPlace();
		}
	}
}
