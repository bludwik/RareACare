package com.blsoft.rareacare.client.ui.interfaces;

import java.util.List;

import com.blsoft.rareacare.client.activity.IRegDsgnActivity;
import com.blsoft.rareacare.client.requestfactory.RegUserProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;

public interface IRegUsersEditor {

	void showRegUsersEditor(RegistryProxy u, IRegDsgnActivity presenter);

	void consumeRegUsers(List<RegUserProxy> ls);

}