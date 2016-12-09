package com.dotcms.visitor.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.UUIDUtil;

import eu.bitwalker.useragentutils.UserAgent;


public abstract class AbstractVisitor implements Visitor {

  private static final long serialVersionUID = 1L;


  protected final Map<String, Object> map;

  protected final Visitor parent;
 

  protected AbstractVisitor(Visitor visitor) {
    this.parent =  visitor;
    this.map = (visitor instanceof AbstractVisitor) ? ((AbstractVisitor)visitor).map : new HashMap<>();
    
  }

  @Override
  public UserAgent userAgent() {
    return parent.userAgent();
  }

  public String ipAddress() {
    return parent.ipAddress();
  }

  public Language language() {
    return parent.language();
  }

  public Locale locale() {
    return parent.locale();
  }

  @Nullable
  public String referer() {
    return parent.referer();
  }

  @Value.Default
  public String id() {
    return (parent.id() != null) ? parent.id() : UUIDUtil.uuidTimeBased();
  }
}
