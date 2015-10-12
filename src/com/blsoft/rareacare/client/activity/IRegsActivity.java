/**
 * 
 */
package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.activity.RegsActivity.IRegConsumer; 
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.ui.interfaces.IRegsDesignerPropsConsumer;
import com.google.gwt.view.client.Range;

/**
 * @author bartek
 *
 */
public interface IRegsActivity extends IPresenter, IRegsDesignerPropsConsumer {

	void getRegCount(IRegConsumer consumer, String patern);
	void getRegs(Range newRange, IRegConsumer consumer, String patern);
	void editRegProps(RegistryProxy reg);

}
