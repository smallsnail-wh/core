package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.dotcms.repackage.com.google.common.collect.HashMultiset;
import com.dotcms.repackage.com.google.common.collect.Multiset;
import com.dotcms.repackage.com.google.common.collect.Multisets;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.bitwalker.useragentutils.UserAgent;

public class VisitorWrapper extends Visitor {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  Visitor _visitor;

  public VisitorWrapper(Visitor visitor) {
    if(visitor instanceof VisitorWrapper){
      throw new DotStateException("can't wrap a VisitorWrapper");
    }
    _setVisitor(visitor);
  }

  private Map<String, Serializable> _map = ImmutableMap.of();

  public void put(String key, Serializable value) {
    _map = ImmutableMap.<String, Serializable>builder().putAll(_map).put(key, value).build();
  }

  public Serializable get(String key) {
    return _map.get(key);
  }

  public void remove(String key) {
    Map<String, Serializable> map = new HashMap<>();
    map.putAll(_map);
    map.remove(key);


    _map = ImmutableMap.copyOf(map);
  }

  public void _setVisitor(Visitor visitor) {
    this._visitor = visitor;

  }
  
  @Override
  public void setPersona(IPersona persona) {
    this._visitor = APILocator.getVisitorAPI().setPersona(_visitor, persona);

  }
  
  @Override
  public String ipAddress() {
    return _visitor.ipAddress();
  }

  @Override
  public Language language() {
    return _visitor.language();
  }

  @Override
  public Locale locale() {
    return _visitor.locale();
  }

  @Override
  public IPersona persona() {
    return _visitor.persona();
  }

  @Override
  public UserAgent userAgent() {
    return _visitor.userAgent();
  }

  @Override
  public String dmid() {
    return _visitor.dmid();
  }

  @Override
  public boolean newVisitor() {
    return _visitor.newVisitor();
  }

  @Override
  public String referer() {
    return _visitor.referer();
  }

  public List<AccruedTag> getAccruedTags() {
    Multiset<String> myMultiset = HashMultiset.create();
    myMultiset.addAll(_visitor.accruedTagsRaw());
    List<AccruedTag> tags = new ArrayList<>();
    for (String key : Multisets.copyHighestCountFirst(myMultiset).elementSet()) {
      AccruedTag tag = new AccruedTag(key, myMultiset.count(key));
      tags.add(tag);
    }
    return tags;
  }

  public List<AccruedTag> getTags() {
    return getAccruedTags();
  }

  public void addAccruedTags(Set<String> addingTags) {

    List<String> newTags = new ArrayList<>();
    newTags.addAll(addingTags);
    newTags.addAll(_visitor.accruedTagsRaw());

    _visitor = ImmutableVisitor.builder().from(_visitor).accruedTagsRaw(newTags).build();
  }

  public void addTag(String tag) {
    if (tag == null)
      return;
    List<String> newTags = new ArrayList<>();
    newTags.add(tag);
    newTags.addAll(_visitor.accruedTagsRaw());

    _visitor = ImmutableVisitor.builder().from(_visitor).accruedTagsRaw(newTags).build();
  }

  public void addTag(String tag, int count) {
    if (tag == null)
      return;
    List<String> newTags = new ArrayList<>();
    newTags.addAll(_visitor.accruedTagsRaw());
    removeTag(tag);
    for (int j = 0; j < count; j++) {
      newTags.add(tag);
    }

    _visitor = ImmutableVisitor.builder().from(_visitor).accruedTagsRaw(newTags).build();
  }

  public void removeTag(String tag) {
    List<String> newTags = new ArrayList<>();
    newTags.addAll(_visitor.accruedTagsRaw());
    Iterator<String> i = newTags.iterator();
    while (i.hasNext()) {
      String oldTag = i.next();
      if (tag.equalsIgnoreCase(oldTag))
        i.remove();
    }

    _visitor = ImmutableVisitor.builder().from(_visitor).accruedTagsRaw(newTags).build();
  }

  public void clearTags() {
    _visitor = ImmutableVisitor.builder().from(_visitor).accruedTagsRaw(ImmutableList.of()).build();
  }


  public List<AccruedTag> accruedTags() {
    return getAccruedTags();
  }

  @Override
  public List<String> accruedTagsRaw() {
    return _visitor.accruedTagsRaw();
  }

  @Override
  public boolean equals(Object obj) {
    return _visitor.equals(obj);
  }

  @Override
  public String toString() {
    return _visitor.toString();
  }

  @Override
  public int hashCode() {

    return _visitor.hashCode();
  }

  
}
