package com.blsoft.rareacare.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.RegDsgnPlace;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.RegUserProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Elem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.ElemItem;
import com.blsoft.rareacare.client.ui.interfaces.IRegDesigner;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class RegDsgnActivity extends CustomActivity implements IRegDsgnActivity {

	private RegDsgnPlace place;
	private RegistryProxy reg = null;
	private List<RegUserProxy> users;
	private RegistryProxy.RareACareDef def;

	public RegDsgnActivity(RegDsgnPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
		this.place = place;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, EventBus eventBus) {
		if (Utils.CheckLogInForced()) {

			reg = Utils.getRegDefFromCache(Integer.parseInt(place.getRegDsgnName()));
			if (reg == null) {

				RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
				req.findRegistry(Integer.parseInt(place.getRegDsgnName())).with("currRegUser", "currRegUser.user", "owner").fire(
						new CommonReceiver<RegistryProxy>() {
							public void onSuccess(RegistryProxy u) {
								Utils.setRegDefToCache(Integer.parseInt(place.getRegDsgnName()), u);
								openDesigner(u);
							}
						});
			} else
				openDesigner(reg);

		}
	}

	private void openDesigner(RegistryProxy rg) {
		if (!rg.getCurrRegUser().getCanDefine())
			Utils.showError("Nie posiadasz uprawnień do edytowania wskazanego rejestru");
		else {
			reg = rg;
			def = RegistryProxy.RareACareDef.createFromString(reg.getDef());
			IRegDesigner iRegDesigner = iClientFactory.getRegDesigner();
			iRegDesigner.setPresenter(this);

			FadeAnimation.changeWidget(iRegDesigner.asWidget());
		}
	}

	/** Edycja użytkowników rejestru */
	@Override
	public void editRegUsers() {

		Utils.getCF().getRegUsersEditor().showRegUsersEditor(reg, this);

		RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
		req.getRegUsers(reg.getId()).with("user").fire(new CommonReceiver<List<RegUserProxy>>() {

			@Override
			public void onSuccess(List<RegUserProxy> response) {
				users = response;
				Utils.getCF().getRegUsersEditor().consumeRegUsers(response);
			}
		});

	}

	/** Edycja wskazanego pracownika (uprawnień)
	 * 
	 * @param user
	 *            - edytowane uprawnienia pracownika */
	@Override
	public void editRegUser(RegUserProxy user) {
		Utils.getCF().getRegUserPropEditor().editRegUser(user, this);
	}

	/** Zapis edytowanego użytkownika */
	@Override
	public void saveRegUser(RegUserProxy user) {
		Utils.replaceOrAddEntity(users, user);
		Utils.getCF().getRegUsersEditor().consumeRegUsers(users);
	}

	@Override
	public RegistryProxy getRegistry() {
		return reg;
	}

	@Override
	public void deleteRegUser(final RegUserProxy user) {
		RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
		req.remove(user).fire(new CommonReceiver<Void>() {

			@Override
			public void onSuccess(Void response) {
				Utils.removeEntity(users, user);
				Utils.getCF().getRegUsersEditor().consumeRegUsers(users);
			}
		});
	}

	@Override
	public RegistryProxy.RareACareDef getDoc() {
		return def;
	}

	/** Zapisuje aktualny rejestr do bazy */
	@Override
	public void saveReg(final boolean closeDesigner) {
		RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
		reg = req.edit(reg);
		reg.setDef(def.toJson());
		req.persist(reg).fire(new CommonReceiver<Void>() {

			@Override
			public void onSuccess(Void response) {
				// Jeśli udało się zapisać, to usuwam z cache, bo konieczne jest
				// by cache miał też dane zalogowanegu usera
				Utils.setRegDefToCache(reg.getId(), null);
				if (closeDesigner)
					goTo(new RegsPlace());
			}

		});
		// req.per remove(user).fire(new CommonReceiver<Void>() {

		return;
	}

	@Override
	public List<String> getFieldList(Doc doc) {
		List<String> ls = new ArrayList<String>();
		buildFieldList(ls, doc.getItems(), "");
		return ls;
	}

	@Override
	public List<String> getFieldListOfItems(JsArray<DocItem> items) {
		List<String> ls = new ArrayList<String>();
		buildFieldList(ls, items, "");
		return ls;
	}

	private void buildFieldList(List<String> fldList, JsArray<DocItem> items, String id) {

		for (int i = 0; i < items.length(); i++) {
			DocItem it = items.get(i);
			JsArray<Elem> elems = def.getElems();

			switch (it.getKind()) {
			case Header:
				buildFieldList(fldList, it.getItems(), Utils.joinItems(id, it.getId()));
				break;

			case Elem:
				for (int j = 0; j < elems.length(); j++) {
					String itemId;
					Elem elem = elems.get(j);
					if (elem.getId().equals(it.getId())) {

						// Renderuję wskazany element
						for (int k = 0; k < elem.getItems().length(); k++) {
							ElemItem eIt = elem.getItems().get(k);
							itemId = id + '|' + Utils.joinItems(it.getId(), eIt.getId());
							fldList.add(itemId);
						}
						break;
					}
				}
			default:
				break;
			}
		}
	}

}
