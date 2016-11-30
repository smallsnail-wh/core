package com.dotcms.visitor.domain;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.immutables.value.Value;

import com.dotcms.repackage.com.google.common.collect.HashMultiset;
import com.dotcms.repackage.com.google.common.collect.Multiset;
import com.dotcms.repackage.com.google.common.collect.Multisets;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;


@Value.Immutable
public abstract class Visitor implements Serializable {

     static final long serialVersionUID = 1L;

     public abstract InetAddress ipAddress();;

     public abstract Language language();

     public abstract Locale locale();

     public abstract IPersona persona();

     Multiset<String> _accruedTags = HashMultiset.create();
    		
     public abstract UserAgent userAgent();

     public abstract String dmid();

     public abstract boolean newVisitor();

     public abstract String referrer();

     public abstract Date lastRequestDate();

     private Map<String, Serializable> map =  ImmutableMap.of();

     Set<String> pagesViewed =  ImmutableSet.of();
     /*
     public void setPersona(IPersona persona) {

       //Validate if we must accrue the Tags for this "new" Persona
       if ( persona != null &&
               (this.persona == null || !this.persona.getIdentifier().equals(persona.getIdentifier())) ) {

           try {
               //The Persona changed for this Visitor, we must accrue the tags associated to this new Persona
               List<Tag> personaTags = APILocator.getTagAPI().getTagsByInode(persona.getInode());

               String foundTags = TagUtil.tagListToString(personaTags);
               //Accrue these found tags to this visitor object
               TagUtil.accrueTagsToVisitor(this, foundTags);
           } catch (DotDataException e) {
               Logger.error(this, "Unable to retrieve Tags associated to Persona [" + persona.getInode() + "].", e);
           }

       }

       this.persona = persona;
   }
*/
     InetAddress getIpAddress() {
        return ipAddress();
    }



    public Locale getLocale() {
        return locale();
    }



    public IPersona getPersona() {
        return persona();
    }

    
    public abstract List<AccruedTag> accruedTags();
    
    
   
    public List<AccruedTag> getAccruedTags() {
    	List<AccruedTag> tags = new ArrayList<>();
		for (String key : Multisets.copyHighestCountFirst(_accruedTags).elementSet()) {
			AccruedTag tag = new AccruedTag(key,_accruedTags.count(key) );
		    tags.add(tag);
		}
		return tags;
    }
    
    public List<AccruedTag> getTags() {
		return getAccruedTags();
    }
    
    public void addAccruedTags(Set<String> tags){
    	for(String tag : tags){
    		addTag(tag);
    	}
    	//_accruedTags.addAll(tags);
    }
    public void addTag(String tag){
    	if(tag==null) return;
    	_accruedTags.add(tag);
    }
    
    public void addTag(String tag, int count){
    	if(tag==null) return;
    	_accruedTags.add(tag, count);
    }
    public void removeTag(String tag){
    	_accruedTags.remove(tag);
    }

    public void clearTags(){
    	_accruedTags = HashMultiset.create();
    }
    

    public UserAgent getUserAgent() {
        return this.userAgent();
    }


    public String getDmid() {
        return dmid();
    }

    public boolean isNewVisitor() {
        return newVisitor();
    }

    public boolean getNewVisitor() {
        return newVisitor();
    }


    public String getReferrer() {
        return referrer();
    }

    public String getDevice() {
    	if(userAgent() !=null){
    		return userAgent().getOperatingSystem().getDeviceType().toString();
    	}
        return DeviceType.UNKNOWN.toString();
    }
    

    public Date getLastRequestDate() {
        return lastRequestDate();
    }


    public void put(String key, Serializable value) {
        map= ImmutableMap.<String, Serializable>builder().putAll(map).put(key, value).build();

    }

    public Serializable get(String key) {
        return map.get(key);
    }



    @Value.Derived
    public int getNumberPagesViewed(){
        return pagesViewed.size();
    }
}
