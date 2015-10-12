package com.blsoft.rareacare.client.ui;

import com.blsoft.rareacare.client.requestfactory.AddressProxy;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class AddressEditor extends Composite implements Editor<AddressProxy>{
	private DecoratorPanel decoratorPanel;
	private FlexTable flexTable;
	private Label lblNewLabel;
	private Label lblUlica;
	private Label lblKodIMiasto;
	private Label lblKod;
	private Label lblWojewdztwo;
	private Label lblKraj;

	TextBox streetEditor;
	TextBox str_noEditor;
	TextBox str_locEditor;
	TextBox cityEditor;
	TextBox zipcodeEditor;
	TextBox regionEditor;
	TextBox countryEditor;

	public AddressEditor() {

		decoratorPanel = new DecoratorPanel();
		initWidget(decoratorPanel);
		decoratorPanel.setSize("100%", "100%");

		flexTable = new FlexTable();
		flexTable.setCellPadding(5);
		flexTable.setCellSpacing(5);
		decoratorPanel.setWidget(flexTable);
		flexTable.setSize("", "");

		lblNewLabel = new Label("Adres");
		lblNewLabel.setStyleName("groupHeader");
		flexTable.setWidget(0, 0, lblNewLabel);

		lblUlica = new Label("Ulica");
		lblUlica.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		flexTable.setWidget(1, 0, lblUlica);

		streetEditor = new TextBox();
		flexTable.setWidget(1, 1, streetEditor);
		flexTable.getCellFormatter().setWidth(1, 1, "");
		streetEditor.setWidth("30EM");

		str_noEditor = new TextBox();
		flexTable.setWidget(1, 2, str_noEditor);
		flexTable.getCellFormatter().setWidth(1, 2, "");
		str_noEditor.setWidth("5EM");
		flexTable.getCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);

		str_locEditor = new TextBox();
		flexTable.setWidget(1, 3, str_locEditor);
		flexTable.getCellFormatter().setWidth(1, 3, "");
		str_locEditor.setWidth("5EM");

		lblKodIMiasto = new Label("Miasto");
		lblKodIMiasto.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		flexTable.setWidget(2, 0, lblKodIMiasto);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter().setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter().setHorizontalAlignment(1, 3, HasHorizontalAlignment.ALIGN_LEFT);

		cityEditor = new TextBox();
		flexTable.setWidget(2, 1, cityEditor);
		cityEditor.setWidth("30EM");

		lblKod = new Label("Kod");
		lblKod.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		flexTable.setWidget(2, 2, lblKod);

		zipcodeEditor = new TextBox();
		flexTable.setWidget(2, 3, zipcodeEditor);
		zipcodeEditor.setWidth("5EM");

		lblWojewdztwo = new Label("Województwo");
		lblWojewdztwo.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		flexTable.setWidget(3, 0, lblWojewdztwo);

		regionEditor = new TextBox();
		flexTable.setWidget(3, 1, regionEditor);
		regionEditor.setWidth("30EM");

		lblKraj = new Label("Kraj");
		lblKraj.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		flexTable.setWidget(3, 2, lblKraj);

		countryEditor = new TextBox();
		flexTable.setWidget(3, 3, countryEditor);
		countryEditor.setWidth("5EM");
		flexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
	}

}
