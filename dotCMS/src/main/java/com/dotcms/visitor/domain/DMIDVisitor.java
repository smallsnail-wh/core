package com.dotcms.visitor.domain;

import com.dotmarketing.util.UUIDUtil;


public class DMIDVisitor extends AbstractVisitor {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final String DMID = "dmid";
  private static final String NEW_VISITOR= "newVisitor";
  
  public DMIDVisitor(Visitor visitor) {
    this(visitor, null);
  }

  public DMIDVisitor(Visitor visitor, String dmid) {
    super(visitor);
    
    if(dmid!=null){
      map.put(DMID,dmid);
      map.put(NEW_VISITOR,false);
    }else if(dmid() ==null){
      map.put(DMID,UUIDUtil.uuidTimeBased());
      map.put(NEW_VISITOR,true);
    }
  }
  
  public String dmid() {
    return (String) map.get("dmid");
  }

  public boolean newVisitor() {
    return (Boolean) map.get("newVisitor");
  }
  
  
}
