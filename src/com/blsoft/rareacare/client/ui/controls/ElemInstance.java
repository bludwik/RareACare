package com.blsoft.rareacare.client.ui.controls;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ElemInstance extends Composite {
	private FlowPanel mainPanel;
	private HTML elemLabel;
	private FlexTable flexTable;
	private Label lblLabelcomment;
	private HTML htmlComment;
	private Label labelSelector;
	private VerticalPanel verticalPanel;
	private CheckBox chckbxPyt;
	private CheckBox chckbxPyt_1;
	private HTML htmlCommentForSelectro;
	private Label lblLabeledit;

	public ElemInstance() {
		
		mainPanel = new FlowPanel();
		initWidget(mainPanel);
		mainPanel.setWidth("500px");
		
		elemLabel = new HTML("Label elementu", true);
		mainPanel.add(elemLabel);
		
		flexTable = new FlexTable();
		mainPanel.add(flexTable);
		flexTable.setWidth("100%");
		
		lblLabelcomment = new Label("labelComment");
		flexTable.setWidget(0, 0, lblLabelcomment);
		flexTable.getCellFormatter().setWidth(0, 0, "30%");
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_JUSTIFY);
		
		htmlComment = new HTML("Comment", true);
		flexTable.setWidget(0, 1, htmlComment);
		flexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		
		labelSelector = new Label("Selector");
		flexTable.setWidget(1, 0, labelSelector);
		
		verticalPanel = new VerticalPanel();
		flexTable.setWidget(1, 1, verticalPanel);
		verticalPanel.setWidth("100%");
		flexTable.getCellFormatter().setWidth(1, 1, "40%");
		
		chckbxPyt = new CheckBox("pyt1");
		verticalPanel.add(chckbxPyt);
		
		chckbxPyt_1 = new CheckBox("pyt2");
		verticalPanel.add(chckbxPyt_1);
		
		htmlCommentForSelectro = new HTML("Comment for selectro", true);
		flexTable.setWidget(1, 2, htmlCommentForSelectro);
		flexTable.getCellFormatter().setWidth(1, 2, "30%");
		flexTable.getFlexCellFormatter().setColSpan(0, 1, 2);
		flexTable.getCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		
		lblLabeledit = new Label("LabelEdit");
		flexTable.setWidget(2, 0, lblLabeledit);
	}

}
