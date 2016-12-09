package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.dotmarketing.business.DotStateException;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.UUIDUtil;

import eu.bitwalker.useragentutils.UserAgent;


@Value.Immutable
public interface Visitor extends Serializable {

  static final long serialVersionUID = 1L;

  public String ipAddress();

  @Value.Derived
  default InetAddress inetAddress(){
    try {
      return Inet4Address.getByName(ipAddress());
    } catch (UnknownHostException e) {
      throw new DotStateException(e);
    }
  }
  
  public Language language();

  public Locale locale();


  @Value.Default
  default String id() {
    return UUIDUtil.uuidTimeBased();
  }

  public UserAgent userAgent();

  @Nullable
  public String referer();


}
