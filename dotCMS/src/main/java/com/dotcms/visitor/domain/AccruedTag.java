package com.dotcms.visitor.domain;

import java.io.Serializable;



public class AccruedTag implements Serializable {

  private static final long serialVersionUID = 1L;
  final String tag;
  final int count;

  public AccruedTag(String tag, int count) {
    this.tag = tag;
    this.count = count;
  }

  public AccruedTag(AccruedTag tag) {
    this.tag = tag.tag;
    this.count = tag.count + 1;
  }



  public String getTag() {
    return tag;
  }

  public int getCount() {
    return count;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AccruedTag) {
      AccruedTag tag2 = (AccruedTag) obj;
      if (tag2.getTag().equals(this.tag) && this.count == tag2.count) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "{\"tag\":\"" + tag + "\", \"count\":" + count + "}";

  }

}
