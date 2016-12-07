package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.dotmarketing.business.DotStateException;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.util.UUIDUtil;
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
  
  @Value.Default
  public void setPersona(IPersona persona) {
    throw new DotStateException("Unimplemented method");
  }
  
  public abstract List<String> accruedTagsRaw();

  public abstract UserAgent userAgent();

  public abstract String dmid();

  public abstract boolean newVisitor();

  @Nullable
  public abstract String referer();

  @Value.Default
  public Date lastRequestDate() {
    return new Date();
  }


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

  Set<String> pagesViewed = ImmutableSet.of();


  @Value.Derived
  public int getNumberPagesViewed() {
    return pagesViewed.size();
  }
}
