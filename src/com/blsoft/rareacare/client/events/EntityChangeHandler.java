package com.blsoft.rareacare.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface EntityChangeHandler extends EventHandler {
	void onEntityChange(EntityChangeEvent event);
}
