package com.blsoft.rareacare.client.activity;

import java.util.List;

import com.blsoft.rareacare.client.requestfactory.RegUserProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.google.gwt.core.client.JsArray;

public interface IRegDsgnActivity extends IPresenter { 
	void editRegUsers();
	void saveRegUser(RegUserProxy currentUser);
	void editRegUser(RegUserProxy user);
	void deleteRegUser(RegUserProxy user);
	RegistryProxy getRegistry();
	RareACareDef getDoc();
	void saveReg(boolean closeDesigner);
	List<String> getFieldList(Doc doc);
	List<String> getFieldListOfItems(JsArray<DocItem> items);

}
