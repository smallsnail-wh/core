package com.dotcms.visitor.domain;


import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.TagUtil;


public class PersonifiedVisitor extends AbstractVisitor {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  final static String PERSONA = "PERSONA";

  public PersonifiedVisitor(Visitor visitor) {
    this(visitor, (visitor instanceof AbstractVisitor) ? (IPersona) ((AbstractVisitor)visitor).map().get(PERSONA) : null);
  }



  public PersonifiedVisitor(Visitor visitor, IPersona persona) {
    super(visitor);
    map().put(PERSONA,  persona);
    if(persona!=null){
      String x;
      try {
        x = TagUtil.tagListToString(APILocator.getTagAPI().getTagsByInode(persona.getInode()));
        new TaggedVisitor(this, x);
      } catch (DotDataException e) {
        Logger.debug(this.getClass(), e.getMessage());
      }
    }
  }
  
  public IPersona persona() {
    return (IPersona) map().get(PERSONA);
  }
}
