package com.blsoft.rareacare.client.activity;

public interface ILoginActivity extends IPresenter {
	public void login(String userTxt, String passwordTxt);

	public void logout();

}
