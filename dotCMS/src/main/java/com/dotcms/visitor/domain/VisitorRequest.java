package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import eu.bitwalker.useragentutils.UserAgent;

@Value.Immutable
public abstract class VisitorRequest implements Serializable {


  static final long serialVersionUID = 1L;

  public abstract String protocol();
  public abstract String ipAddress();
  public abstract String serverName();

  public abstract int serverPort();

  public abstract String uri();

  @Nullable
  public abstract String queryString();

  @Nullable
  public abstract String userId();

  public abstract long languageId();

  @Value.Lazy
  public Date timestamp() {
    return new Date();
  }

  public abstract String hostId();

  public abstract String userAgentHeader();

  
  public abstract UserAgent userAgent();
  
  @Nullable
  public abstract String contentId();
  
  @Nullable
  public abstract String pageId();

  
  @Nullable
  public abstract String referer();

  public abstract String dmid();

}
