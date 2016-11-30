package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.util.Date;

import org.immutables.value.Value;

@Value.Immutable
public abstract class VisitorRequest implements Serializable {


  static final long serialVersionUID = 1L;

  abstract Visit visit();

  abstract String protocol();

  abstract String serverName();

  abstract int serverPort();

  abstract String uri();

  abstract String queryString();

  abstract String userId();

  abstract long languageId();

  @Value.Lazy
  Date timestamp() {
    return new Date();
  }

  abstract String hostId();

  abstract String contentId();


}
