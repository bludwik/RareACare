package com.blsoft.rareacare.client.ui.interfaces;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 * 
 * @author drfibonacci
 */
public interface IGoodbyeView extends IsWidget
{
	void setName(String helloName);
}