package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.dotcms.repackage.com.google.common.collect.HashMultiset;
import com.dotcms.repackage.com.google.common.collect.Multiset;
import com.dotcms.repackage.com.google.common.collect.Multisets;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.util.UUIDUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;


@Value.Immutable
public abstract class Visitor implements Serializable {

  static final long serialVersionUID = 1L;

  public abstract String ipAddress();;

  public abstract Language language();

  public abstract Locale locale();

  @Nullable
  public abstract IPersona persona();

  @Value.Default
  public String id() {
    return UUIDUtil.uuidTimeBased();
  }

  Multiset<String> _accruedTags = HashMultiset.create();

  public abstract UserAgent userAgent();

  public abstract String dmid();

  public abstract boolean newVisitor();

  @Nullable
  public abstract String referer();

  @Value.Default
  public Date lastRequestDate() {
    return new Date();
  }

  private Map<String, Serializable> map = ImmutableMap.of();


  Set<String> pagesViewed = ImmutableSet.of();

  String getIpAddress() {
    return ipAddress();
  }
  @Value.Default
  public String userId(){
    return "anonymous";
  }
  public Locale getLocale() {
    return locale();
  }



  public IPersona getPersona() {
    return persona();
  }


  public abstract List<AccruedTag> accruedTags();



  public List<AccruedTag> getAccruedTags() {
    List<AccruedTag> tags = new ArrayList<>();
    for (String key : Multisets.copyHighestCountFirst(_accruedTags).elementSet()) {
      AccruedTag tag = new AccruedTag(key, _accruedTags.count(key));
      tags.add(tag);
    }
    return tags;
  }

  public List<AccruedTag> getTags() {
    return getAccruedTags();
  }

  public void addAccruedTags(Set<String> tags) {
    for (String tag : tags) {
      addTag(tag);
    }
    // _accruedTags.addAll(tags);
  }

  public void addTag(String tag) {
    if (tag == null)
      return;
    _accruedTags.add(tag);
  }

  public void addTag(String tag, int count) {
    if (tag == null)
      return;
    _accruedTags.add(tag, count);
  }

  public void removeTag(String tag) {
    _accruedTags.remove(tag);
  }

  public void clearTags() {
    _accruedTags = HashMultiset.create();
  }

  public UserAgent getUserAgent() {
    return this.userAgent();
  }

  public String getDmid() {
    return dmid();
  }

  public boolean isNewVisitor() {
    return newVisitor();
  }

  public boolean getNewVisitor() {
    return newVisitor();
  }


  public String getReferrer() {
    return referer();
  }

  public String getDevice() {
    if (userAgent() != null) {
      return userAgent().getOperatingSystem().getDeviceType().toString();
    }
    return DeviceType.UNKNOWN.toString();
  }


  public Date getLastRequestDate() {
    return lastRequestDate();
  }


  public void put(String key, Serializable value) {
    map = ImmutableMap.<String, Serializable>builder().putAll(map).put(key, value).build();

  }

  public Serializable get(String key) {
    return map.get(key);
  }



  @Value.Derived
  public int getNumberPagesViewed() {
    return pagesViewed.size();
  }
}
