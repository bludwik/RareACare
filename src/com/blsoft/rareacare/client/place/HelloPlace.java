package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

//public class HelloPlace extends ActivityPlace<HelloActivityImpl>
public class HelloPlace extends Place
{
	public static class Tokenizer implements PlaceTokenizer<HelloPlace>
	{

		@Override
		public String getToken(HelloPlace place)
		{
			return null;
		}

		@Override
		public HelloPlace getPlace(String token)
		{
			return new HelloPlace();
		}

	}
	
//	@Override
//	protected Place getPlace(String token)
//	{
//		return new HelloPlace(token);
//	}
//
//	@Override
//	protected Activity getActivity()
//	{
//		return new HelloActivityImpl("David");
//	}
}
