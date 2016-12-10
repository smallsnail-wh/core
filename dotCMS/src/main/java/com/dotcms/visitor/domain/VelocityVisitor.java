package com.dotcms.visitor.domain;

import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;

import eu.bitwalker.useragentutils.UserAgent;

public class VelocityVisitor extends AbstractVisitor {

    public VelocityVisitor(Visitor visitor) {
      super(visitor);
    }

    private static final long serialVersionUID = 1L;


    private PersonifiedVisitor persona(){
      return new PersonifiedVisitor(this);
    }
    
    private TaggedVisitor taggedVisitor(){
      return new TaggedVisitor(this);
    }
    
    public InetAddress getIpAddress() {
        return inetAddress();
    }

    public Language getSelectedLanguage() {
        return language();
    }

    public void setSelectedLanguage(Language selectedLanguage) {
      Visitor updateVisitor =  ImmutableVisitor.builder().from(this).language(selectedLanguage).build();
      new CopyVisitor(this, updateVisitor);
      
    }

    public Locale getLocale() {
        return locale();
    }


    public IPersona getPersona() {
        return new PersonifiedVisitor(this).persona();
    }

    public void setPersona(IPersona persona) {

      new PersonifiedVisitor(this, persona);
    }

    
   
    public List<AccruedTag> getAccruedTags() {

      return taggedVisitor().accruedTags();
    }
    
    public List<AccruedTag> getTags() {

      return taggedVisitor().accruedTags();
    }
    
    public void addAccruedTags(Set<String> tags){
        taggedVisitor().addAccruedTags(tags);
    }
    public void addTag(String tag){
       taggedVisitor().addTag(tag);
    }
    
    public void addTag(String tag, int count){
      taggedVisitor().addTag(tag, count);
    }
    public void removeTag(String tag){
      taggedVisitor().remove(tag);

    }

    public void clearTags(){
      taggedVisitor().clearTags();
    }

    public UserAgent getUserAgent() {
        return userAgent();
    }


    public String getDmid() {
        return new DMIDVisitor(this).dmid();
    }

    public boolean isNewVisitor() {
      return new DMIDVisitor(this).newVisitor(); 
    }
    public boolean getNewVisitor() {
      return new DMIDVisitor(this).newVisitor(); 
    }

    public String getReferrer() {
        return referer();
    }
    
    public String getDevice() {
      return userAgent().getOperatingSystem().getDeviceType().toString();

    }
    

    public void setReferrer(String referrer) {
      Visitor updateVisitor =  ImmutableVisitor.builder().from(this).referer(referrer).build();
      new CopyVisitor(this, updateVisitor);
    }


    @Override
    public String toString() {
        return "Visitor{" +
                "id=" + this.hashCode() +
                ", ipAddress=" + ipAddress() +
                ", selectedLanguage=" + language() +
                ", locale=" + locale() +
                ", persona=" + persona() +
                ", accruedTags=" + getAccruedTags() +
                ", userAgent=" + userAgent() +
                ", device=" + getDevice() +
                ", dmid=" + getDmid() +
                ", newVisitor=" + isNewVisitor() +
                ", referrer=" + referer() +
                ", map=" + map() +
                '}';
    }


}