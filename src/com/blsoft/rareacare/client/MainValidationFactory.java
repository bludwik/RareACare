package com.blsoft.rareacare.client;

import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.shared.ClientGroup;
import com.blsoft.rareacare.shared.ServerGroup;
import com.google.gwt.validation.client.AbstractGwtValidatorFactory;
import com.google.gwt.validation.client.impl.AbstractGwtValidator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.validation.client.GwtValidation;

import javax.validation.Validator;
import javax.validation.groups.Default;

public final class MainValidationFactory extends AbstractGwtValidatorFactory {

	/**
	   * Validator marker for the Validation Sample project. Only the classes listed
	   * in the {@link GwtValidation} annotation can be validated.
	   */
	  @GwtValidation(value = User.class,
	      groups = {Default.class, ClientGroup.class, ServerGroup.class})
	  public interface GwtValidator extends Validator {
	  }

	  @Override
	  public AbstractGwtValidator createValidator() {
	    return GWT.create(GwtValidator.class);
	  }

}
