package com.blsoft.rareacare.client.ui.controls;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.PushButton;

public class BLButton extends PushButton {

	public enum ImgPos {
		Left, Right, Up, Down
	};

	private ImageResource imageResource = null;
	private ImgPos pos = ImgPos.Left;

	public BLButton() {
		super();
	}

	public BLButton(String text, ImageResource imageResource, ImgPos pos, ClickHandler clickHandler) {
		super();
		addClickHandler(clickHandler);
		this.imageResource = imageResource;
		this.pos = pos;
		this.setText(text);
		init();
	}

	public BLButton(String text, ImageResource imageResource, ImgPos pos) {
		super();
		this.imageResource = imageResource;
		this.pos = pos;
		this.setText(text);
		init();
	}

	public BLButton(String text) {
		super(text);
	}

	public BLButton(ImageResource imageResource) {
		super();
		this.imageResource = imageResource;
		init();
	}
	
    @Override
    public final void setText(String text) {
    	super.setText(text);
    	init();
    }
	
    @Override
    public final void setTitle(String text) {
    	super.setTitle(text);
    	init();
    }
	

	private String getTextAsHtml() {
		return getText().replace("\n", "<br>");
	}

	private void init() {
		String html = null;
		if (imageResource != null)
			switch (pos) {
			case Left:
				html = "<div><table cellpadding='0' cellspacing='0'><tr><td><img src='" + imageResource.getSafeUri().asString()
						+ "' ></td><td><label style='margin-left: 5px;'>" + getTextAsHtml() + "</label></td></tr></table></div>";

				// "<div><img src='" + imageResource.getSafeUri().asString() +
				// "' style='vertical-align:middle' /><label vertical-align:middle'>"
				// + getText()
				// + "</label></div>";
				break;
			case Right:
				html = "<div><table cellpadding='0' cellspacing='0'><tr><td>" + getTextAsHtml() + "</td><td><img src='"
						+ imageResource.getSafeUri().asString() + "' ></td></tr></table></div>";

				break;
			case Up:
				
//				html = "<table cellspacing='2' cellpadding='2'><tr><td><p align='center'><img src='" + imageResource.getSafeUri().asString() + "'></p></td></tr><tr><td>"
//					+ "<p align='center'>" + getText() + "</p></td></tr></table>";
				
				html = "<div style='text-align:center'><img src='" + imageResource.getSafeUri().asString()
						+ "'/><br><label style='text-align:center'>" + getTextAsHtml() + "</label></br></div>";
				break;
			case Down:
				html = "<div style='text-align:center'><label style='text-align:center'>" + getTextAsHtml() + "</label><br><img src='"
						+ imageResource.getSafeUri().asString() + "'/></br></div>";
				break;
			default:
				break;
			}
		
		setHTML(html);
	}

	/** @return the imageResource */
	public ImageResource getImageResource() {
		return imageResource;
	}

	/** @param imageResource
	 *            the imageResource to set */
	public void setImageResource(ImageResource imageResource) {
		this.imageResource = imageResource;
		init();
	}

	/** @return the pos */
	public ImgPos getPos() {
		return pos;
	}

	/** @param pos
	 *            the pos to set */
	public void setPos(ImgPos pos) {
		this.pos = pos;
		init();
	}

}
