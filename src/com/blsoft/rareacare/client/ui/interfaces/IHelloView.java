package com.blsoft.rareacare.client.ui.interfaces;

import com.blsoft.rareacare.client.activity.IPresenter;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 */
public interface IHelloView extends IsWidget
{

	void setPresenter(IPresenter listener);

}