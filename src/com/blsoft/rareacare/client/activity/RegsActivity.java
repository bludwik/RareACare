package com.blsoft.rareacare.client.activity;

import java.util.List;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.ui.interfaces.IRegDesignerProps;
import com.blsoft.rareacare.client.ui.interfaces.IRegsView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.Range;

public class RegsActivity extends CustomActivity implements IRegsActivity {

	public interface IRegConsumer {
		void consumeRegList(int startRange, List<RegistryProxy> list);

		void consumeRegCount(int count);

	}

	private Range oldRange;
	private String oldPatern;

	public RegsActivity(RegsPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
	}

	/** Invoked by the ActivityManager to start a new Activity */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		Utils.CheckLogIn();
		IRegsView iRegsView = iClientFactory.getRegsView();
		iRegsView.setPresenter(this);
		FadeAnimation.changeWidget(iRegsView.asWidget());
		// containerWidget.setWidget(iRegsView.asWidget());
	}

	@Override
	public void getRegCount(final IRegConsumer consumer, String patern) {
		RegistryDataRequest req = iClientFactory.getRequestFactory().registryDataRequest();
		req.getCount(patern).fire(new CommonReceiver<Integer>() {
			@Override
			public void onSuccess(Integer response) {
				consumer.consumeRegCount(response);
			}
		});
	}

	/** Odczyt rejestrów o podanym fragmencie nazwy w podanym zakresie rekordów.
	 * Gdy zakres, lub fragment nazu == null, to system stosuje ostatnio
	 * zastoowane, lub domyślne. Jeśli podano Consumera, to po sukciesie jest
	 * wykonywana odpowiednia metoda z consumera. */
	@Override
	public void getRegs(Range newRange, final IRegConsumer consumer, String patern) {
		// if (newRange == null || !newRange.equals(oldRange) ||
		// !patern.equals(oldPatern)) {
		RegistryDataRequest req = iClientFactory.getRequestFactory().registryDataRequest();
		final Range rg = (newRange == null) ? (oldRange == null) ? new Range(0, 25) : oldRange : newRange;
		final String ptr = (patern == null) ? (oldPatern == null) ? "" : oldPatern : patern;
		req.getRegs(rg.getStart(), rg.getLength(), ptr).with("owner").fire(new CommonReceiver<List<RegistryProxy>>() {
			public void onSuccess(List<RegistryProxy> u) {
				oldRange = rg;
				oldPatern = ptr;
				if (consumer != null)
					consumer.consumeRegList(rg.getStart(), u);
			}
		});
		// }
	}

	@Override
	public void editRegProps(final RegistryProxy reg) {
		if (Utils.CheckLogInForced()) {

			if (reg == null) {
				// Gdy null, to tworzę nowy rejetr (musze być superuserem)
				if (Utils.getUser().getIsSuperuser())
					openProps(null);
				else
					Utils.showError("Nie posiadasz uprawnień do tworzenia nowych rejestrów");
			}
			else {
				RegistryProxy r = Utils.getRegDefFromCache(reg.getId());
				if (r == null) {

					RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
					req.findRegistry(reg.getId()).with("currRegUser", "currRegUser.user", "owner").fire(
							new CommonReceiver<RegistryProxy>() {
								public void onSuccess(RegistryProxy u) {
									Utils.setRegDefToCache(reg.getId(), u);
									openProps(u);
								}
							});
				} else {
					openProps(r);
				}
			}
		}

	}

	private void openProps(RegistryProxy r) {

		if (r == null) {
			if (Utils.getUser().getIsSuperuser()) {
				final IRegDesignerProps rdp = iClientFactory.getRegDesignerProps();
				rdp.edit(null, this);
			} else
				Utils.showError("Nie masz uprawnień do defniowania nowego rejestru");
		} else if (r.getCurrRegUser() != null && r.getCurrRegUser().getCanDefine()) {
			final IRegDesignerProps rdp = iClientFactory.getRegDesignerProps();
			rdp.edit(r, this);
		} else
			Utils.showError("Nie masz uprawnień do defniowania właściwości rejestru");
	}

	@Override
	public void setRegistryPropsEdited(RegistryProxy response) {
		// Czyszczę cache, bo jest nieaktualna
		Utils.setRegDefToCache(response.getId(), null);

		// Odświeżanie listy
		getRegs(null, iClientFactory.getRegsView(), null);
	}

}
