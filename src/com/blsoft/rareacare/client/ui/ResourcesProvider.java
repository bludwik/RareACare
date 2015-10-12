package com.blsoft.rareacare.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.cellview.client.CellList;

public class ResourcesProvider {

	interface DragListsResources extends CellList.Resources {
		@Override
		@Source({ "CellListDragItems.css", CellList.Style.DEFAULT_CSS })
		CellList.Style cellListStyle();
	}
	
	private static DragListsResources dragListsResources = null; 
	static DragListsResources getCellListDragItemRes() {
		if (dragListsResources == null)
			dragListsResources = GWT.create(DragListsResources.class);
		return dragListsResources;		
	}
	
}
