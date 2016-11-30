package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.util.Date;

import org.immutables.value.Value;



@Value.Immutable
public abstract class Visit implements Serializable {
  private static final long serialVersionUID = 1L;

  abstract String host();

  abstract String user();

  abstract String dmid();

  abstract String remoteAddress();

  abstract String remoteHostname();

  abstract String initialReferrer();

  abstract String userAgent();

  abstract Date timestamp() ;

  abstract boolean bot();

  abstract String os();

  abstract String browserName();

  abstract String browserVersion();

  abstract boolean mobileDevice();

}
