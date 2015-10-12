package com.blsoft.rareacare.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface DocElemTemplates extends ClientBundle {

	public static final DocElemTemplates INSTANCE = GWT.create(DocElemTemplates.class);

	@Source("comment.htm")
	TextResource Comment();

	@Source("separator.htm")
	TextResource Separator();

	@Source("header.htm")
	TextResource Header();

	@Source("elem.htm")
	TextResource Elem();


}
