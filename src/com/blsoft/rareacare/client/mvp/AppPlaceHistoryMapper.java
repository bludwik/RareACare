package com.blsoft.rareacare.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.blsoft.rareacare.client.place.GoodbyePlace;
import com.blsoft.rareacare.client.place.HelloPlace;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.place.RegDsgnPlace;
import com.blsoft.rareacare.client.place.RegEditPlace;
import com.blsoft.rareacare.client.place.RegPtsListPlace;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.place.UsersPlace;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers({ HelloPlace.Tokenizer.class, GoodbyePlace.Tokenizer.class, RegsPlace.Tokenizer.class, UsersPlace.Tokenizer.class,
		MainPlace.Tokenizer.class, RegDsgnPlace.Tokenizer.class, RegEditPlace.Tokenizer.class, RegPtsListPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
