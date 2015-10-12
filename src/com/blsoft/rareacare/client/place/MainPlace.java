package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

public class MainPlace extends SecuredPlace {

	public static class Tokenizer implements PlaceTokenizer<MainPlace> {

		@Override
		public String getToken(MainPlace place) {
			return null;
		}

		@Override
		public MainPlace getPlace(String token) {
			return new MainPlace();
		}

	}

}
