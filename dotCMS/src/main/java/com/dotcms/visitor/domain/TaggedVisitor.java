package com.dotcms.visitor.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.dotcms.repackage.com.google.common.collect.HashMultiset;
import com.dotcms.repackage.com.google.common.collect.Multiset;
import com.dotcms.repackage.com.google.common.collect.Multisets;

public class TaggedVisitor extends AbstractVisitor {


  private static final long serialVersionUID = 1L;

  private static final String ACCRUED_TAGS = "accruedTags";



  public TaggedVisitor(Visitor visitor) {
    super(visitor);
    List<String> accruedTags = map().containsKey(ACCRUED_TAGS) ? (List) map().get(ACCRUED_TAGS) : new ArrayList<>();
    map().put(ACCRUED_TAGS, accruedTags);
  }


  public TaggedVisitor(Visitor visitor, Collection<String> tags) {
    this(visitor);
    addAccruedTags(tags);
  }

  public TaggedVisitor(Visitor visitor, String tags) {
    this(visitor, Arrays.asList(((tags == null) ? "" : tags).split(",")));
  }


  public List<AccruedTag> getAccruedTags() {
    Multiset<String> myMultiset = HashMultiset.create();
    myMultiset.addAll(accrued());
    List<AccruedTag> tags = new ArrayList<>();
    for (String key : Multisets.copyHighestCountFirst(myMultiset).elementSet()) {
      AccruedTag tag = new AccruedTag(key, myMultiset.count(key));
      tags.add(tag);
    }
    return tags;
  }


  @SuppressWarnings("unchecked")
  public List<String> accrued() {
    return (List<String>) map().get(ACCRUED_TAGS);
  }



  public List<AccruedTag> getTags() {
    return getAccruedTags();
  }

  public TaggedVisitor addAccruedTags(Collection<String> addingTags) {

    for (String x : addingTags) {
      if (x != null)
        accrued().add(x.trim());
    }

    return this;
  }

  public TaggedVisitor addTag(String tag) {
    if (tag != null && tag.trim().length() > 0)
      accrued().add(tag.trim());
    return this;
  }

  public TaggedVisitor addTag(String tag, int count) {

    for (int j = 0; j < count; j++) {
      accrued().add(tag);
    }
    return this;
  }

  public TaggedVisitor removeTag(String tag) {

    Iterator<String> i = accrued().iterator();
    while (i.hasNext()) {
      String oldTag = i.next();
      if (tag.equalsIgnoreCase(oldTag))
        i.remove();
    }
    return this;
  }

  public TaggedVisitor clearTags() {
    accrued().clear();
    return this;
  }


  public List<AccruedTag> accruedTags() {
    return getAccruedTags();
  }



  public void remove(String tag) {
    Iterator<String> it = accrued().iterator();
    while (it.hasNext()) {
      String thisTag = it.next();
      if (thisTag.equals(tag)) {
        it.remove();
      }
    }

  }

}
