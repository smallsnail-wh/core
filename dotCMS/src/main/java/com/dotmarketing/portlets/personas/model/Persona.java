package com.dotmarketing.portlets.personas.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.personas.business.PersonaAPI;
import com.dotmarketing.util.TagUtil;

public class Persona extends Contentlet implements IPersona{
	private static final long serialVersionUID = -4775734788059690797L;

	

	public Persona(Contentlet oldCon) {
		Map<String, Object> newMap = new HashMap<>();
		oldCon.getMap().forEach(newMap::put);
		super.map = newMap;
	}

	@Override
	public String getName() {
		return getStringProperty(PersonaAPI.NAME_FIELD);
	}

	@Override
	public void setName(String name) {
		setStringProperty(PersonaAPI.NAME_FIELD, name);
	}

	@Override
	public String getKeyTag() {
		return getStringProperty(PersonaAPI.KEY_TAG_FIELD);
	}

	@Override
	public void setKeyTag(String keyTag) {
		setStringProperty(PersonaAPI.KEY_TAG_FIELD, keyTag);
	}

	@Override
	public String getDescription() {
		return getStringProperty(PersonaAPI.DESCRIPTION_FIELD);
	}

	@Override
	public void setDescription(String description) {
		setStringProperty(PersonaAPI.DESCRIPTION_FIELD, description);
	}



}
