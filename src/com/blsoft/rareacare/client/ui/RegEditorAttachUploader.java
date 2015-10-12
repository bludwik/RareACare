package com.blsoft.rareacare.client.ui;


import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import com.blsoft.rareacare.client.res.Images;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.blsoft.rareacare.client.ui.controls.BLButton.ImgPos;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;

public class RegEditorAttachUploader extends DialogBox {

	// A panel where the thumbnails of uploaded images will be shown
	private FlowPanel panelFiles = new FlowPanel();
	private RegEditor  parent;

	public RegEditorAttachUploader(String path) {

		setGlassEnabled(true);
		setAutoHideOnHistoryEventsEnabled(true);

		FlowPanel rootPanel = new FlowPanel();
		setWidget(rootPanel);

		rootPanel.add(panelFiles);

		// Create a new uploader panel and attach it to the document
		final MultiUploader defaultUploader = new MultiUploader();
		defaultUploader.setFileInputPrefix(path + '_');
		rootPanel.add(defaultUploader);
		
		BLButton btnOK = new BLButton("Zamknij", Images.INSTANCE.ok24(), ImgPos.Left);
//		btnOK.setWidth("7EM");
		btnOK.getElement().getStyle().setProperty("margin", "20px");
//		btnOK.getElement().getStyle().setProperty("padding", "5px");

		rootPanel.add(btnOK);
		btnOK.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				defaultUploader.cancel();
				hide();

			
			}
		});

		// Add a finish handler which will load the image once the upload
		// finishes
		defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);

	}

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				UploadedInfo info = uploader.getServerInfo();
				System.out.println("File name " + info.name);
				parent.loadDocuments();
				
			

//				new PreloadedImage(uploader.fileUrl(), showImage);
//
//				// The server sends useful information to the client by default
//				UploadedInfo info = uploader.getServerInfo();
//				System.out.println("File name " + info.name);
//				System.out.println("File content-type " + info.ctype);
//				System.out.println("File size " + info.size);
//
//				// You can send any customized message and parse it
//				System.out.println("Server message " + info.message);
			}
		}
	};

//	// Attach an image to the pictures viewer
//	private OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() {
//		public void onLoad(PreloadedImage image) {
//			image.setWidth("75px");
//			panelFiles.add(image);
//		}
//	};

	static public RegEditorAttachUploader show(String path, RegEditor  parent) {
		
		
		RegEditorAttachUploader instance = new RegEditorAttachUploader(path);

//		DocumentCell.regDef = ed.getRegDef();
//		instance.cellList.setRowData(ed.getDocsToAdd());

		instance.parent = parent;
		instance.setAnimationEnabled(true);
		instance.setAutoHideOnHistoryEventsEnabled(true);
		instance.setGlassEnabled(true);
		instance.center();
		
		return instance;
	}

}
