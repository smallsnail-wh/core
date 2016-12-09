package com.dotcms.visitor.domain;

public class BaseVisitor extends AbstractVisitor {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public BaseVisitor(Visitor ivisitor) {
    super(ivisitor);
    if(!(ivisitor instanceof AbstractVisitor)) return;
    
    
    Visitor firstAbstract = this;
    while(parent instanceof AbstractVisitor){
      AbstractVisitor abs = (AbstractVisitor) parent;
      if(abs.parent instanceof AbstractVisitor){
        parent = abs.parent;
      }
      else{
        break;
      }
    }
    System.err.println("parent:" + parent);
    
  }
  

  
  
}
