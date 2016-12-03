package com.dotcms.visitor.business;

import java.util.ArrayList;
import java.util.List;

import com.dotcms.repackage.com.google.common.collect.ImmutableList;
import com.dotcms.visitor.domain.Visitor;

public class VisitorEvents {

  public VisitorEvents(){
    this(ImmutableList.of());
  }
  public VisitorEvents(List<VisitorListener> listeners){
    this.listeners = ImmutableList.copyOf(listeners);
  }
  
  
  private List<VisitorListener> listeners ;

  public void addListener(VisitorListener toAdd) {
    listeners = ImmutableList.copyOf(listeners).of(toAdd);
  }

  public void removeListener(VisitorListener toAdd) {
    List<VisitorListener> l = new ArrayList<>();
    for (VisitorListener vl : listeners) {
      if (!vl.equals(toAdd)) {
        l.add(vl);
      }
    }
    listeners = ImmutableList.copyOf(l);

  }

  void updated(Visitor old, Visitor updated) {
    if(old.equals(updated)) return;
    // Notify everybody that may be interested.
    for (VisitorListener vl : listeners)
      vl.visitorUpdated(old, updated);
  }

  void created(Visitor visitor) {
    // Notify everybody that may be interested.
    for (VisitorListener vl : listeners)
      vl.visitorCreated(visitor);
  }

  void destroyed(Visitor visitor) {
    // Notify everybody that may be interested.
    for (VisitorListener vl : listeners)
      vl.visitorDestroyed(visitor);
  }


}
