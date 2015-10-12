package com.blsoft.rareacare.client.ui.interfaces;

import com.blsoft.rareacare.client.activity.ILoginActivity;
import com.google.gwt.user.client.ui.IsWidget;

/** View dla aktywności logowania
 * 
 * @author bartek
 *
 */

public interface ILoginView extends IsWidget {
	
	public void setPresenter(ILoginActivity presenter);
	public void initView();

}