package com.blsoft.rareacare.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

public class UsersPlace extends SecuredPlace {
	private String userId;
	
	public UsersPlace(String token)
	{
		super();
		this.userId = token;
	}

	public String getUserId()
	{
		return userId;
	}

	public static class Tokenizer implements PlaceTokenizer<UsersPlace>
	{
		@Override
		public String getToken(UsersPlace place)
		{
			return place.getUserId();
		}

		@Override
		public UsersPlace getPlace(String token)
		{
			return new UsersPlace(token);
		}
	}

}
