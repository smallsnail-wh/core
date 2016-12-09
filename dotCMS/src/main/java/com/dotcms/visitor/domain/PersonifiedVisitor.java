package com.dotcms.visitor.domain;



import com.dotmarketing.portlets.personas.model.IPersona;


public class PersonifiedVisitor extends AbstractVisitor {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  final String PERSONA = "PERSONA";

  public PersonifiedVisitor(Visitor visitor) {
    this(visitor,null);

  }

  public PersonifiedVisitor(Visitor visitor, IPersona persona) {
    super(visitor);
    map.put(PERSONA,  persona);
    if(persona!=null){
      persona.getTags();
      new TaggedVisitor(this, persona.getTags());
    }
  }


  public IPersona persona() {
    return (IPersona) map.get(PERSONA);
  }
}
