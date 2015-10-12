package com.blsoft.rareacare.client.ui.interfaces;

import com.blsoft.rareacare.client.activity.IRegDsgnActivity; 
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.google.gwt.user.client.ui.IsWidget;

public interface IRegDesigner extends IsWidget {

	void setPresenter(IRegDsgnActivity presenter);

	void addOrSetDoc(Doc e);

}