package com.blsoft.rareacare.client.ui.interfaces;

import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.google.gwt.user.client.ui.IsWidget;

public interface IRegDesignerProps extends IsWidget {

	public void edit(RegistryProxy reg, IRegsDesignerPropsConsumer presenter);

}
