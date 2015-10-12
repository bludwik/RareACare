package com.blsoft.rareacare.client.ui.interfaces;

import com.blsoft.rareacare.client.activity.IRegsActivity;
import com.blsoft.rareacare.client.activity.RegsActivity.IRegConsumer;
import com.google.gwt.user.client.ui.IsWidget;

public interface IRegsView extends IsWidget, IRegConsumer {

	public void setPresenter(IRegsActivity presenter);

}
