package com.dotcms.visitor;

import static com.dotcms.visitor.AbstractVisitorTestSetup.*;
import static com.dotcms.visitor.AbstractVisitorTestSetup.IP;
import static com.dotcms.visitor.AbstractVisitorTestSetup.KEYTAG;
import static com.dotcms.visitor.AbstractVisitorTestSetup.REFERER;
import static com.dotcms.visitor.AbstractVisitorTestSetup.language;
import static com.dotcms.visitor.AbstractVisitorTestSetup.visitor;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.dotcms.repackage.com.maxmind.geoip2.model.CityResponse;
import com.dotcms.util.ConfigTestHelper;
import com.dotcms.util.GeoIp2CityDbUtil;
import com.dotcms.visitor.domain.DMIDVisitor;
import com.dotcms.visitor.domain.GeolocatedVisitor;
import com.dotcms.visitor.domain.ImmutableVisitor;
import com.dotcms.visitor.domain.PersonifiedVisitor;
import com.dotcms.visitor.domain.TaggedVisitor;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.personas.model.Persona;
import com.dotmarketing.util.Config;
public class AbstractVisitorTest {
  

  @Test
  public void DMIDTest() {
    DMIDVisitor oldVisitor = new DMIDVisitor(visitor);
    String old = oldVisitor.dmid();
    
    assertNotNull(old);
    assertTrue(IP.equals(oldVisitor.ipAddress()));
    assertTrue(language.equals(oldVisitor.language()));
    assertTrue(AGENT.equals(oldVisitor.userAgent()));
    assertTrue(REFERER.equals(oldVisitor.referer()));
    
    DMIDVisitor newVisitor = new DMIDVisitor(oldVisitor);
    
    
    assertTrue(old.equals(newVisitor.dmid()));
    assertTrue(IP.equals(newVisitor.ipAddress()));
    assertTrue(language.equals(newVisitor.language()));
    assertTrue(AGENT.equals(newVisitor.userAgent()));
    assertTrue(REFERER.equals(newVisitor.referer()));
    

    
  }
  
  @Test
  public void PersonifiedVisitorTest() {
    DMIDVisitor visitor1 = new DMIDVisitor(visitor);
    PersonifiedVisitor visitor2 = new PersonifiedVisitor(visitor1);
    assertNotNull(visitor2);

    assertTrue(IP.equals(visitor2.ipAddress()));
    assertTrue(language.equals(visitor2.language()));
    assertTrue(AGENT.equals(visitor2.userAgent()));
    assertTrue(REFERER.equals(visitor2.referer()));
    assertNull(visitor2.persona());
    
    Persona test = new Persona(new Contentlet());
    test.setKeyTag(KEYTAG);
    
    PersonifiedVisitor visitor3 = new PersonifiedVisitor(visitor2, test);
    assertNotNull(visitor3);

    
    assertNotNull(visitor3.persona());
    assertTrue(KEYTAG.equals(visitor3.persona().getKeyTag()));
    
    assertTrue(visitor3.id().equals(visitor1.id()));
    assertTrue(IP.equals(visitor3.ipAddress()));
    assertTrue(language.equals(visitor3.language()));
    assertTrue(AGENT.equals(visitor3.userAgent()));
    assertTrue(REFERER.equals(visitor3.referer()));
    
    DMIDVisitor visitor4 = new DMIDVisitor(visitor3);
    assertTrue(visitor1.dmid().equals(new DMIDVisitor(visitor4).dmid()));
    PersonifiedVisitor visitor5 = new PersonifiedVisitor(visitor4, test);
    assertTrue(visitor3.persona().getKeyTag().equals(visitor5.persona().getKeyTag()));

  }
  
  @Test
  public void TaggedVisitorTest() {
    DMIDVisitor visitor1 = new DMIDVisitor(visitor);
    TaggedVisitor tv1 = new TaggedVisitor(visitor1);
    
    assertTrue(tv1.accruedTags().size() == 0);
    
    TaggedVisitor tv2 = new TaggedVisitor(tv1, tagList);
    
    assertTrue(tv2.accruedTags().size() == 3);
    assertTrue(tv2.accruedTags().get(0).getTag().equals("tagThree"));
    assertTrue(tv2.accruedTags().get(0).getCount() ==3);
    assertTrue(tv2.accruedTags().get(1).getTag().equals("tagTwo"));
    assertTrue(tv2.accruedTags().get(1).getCount() ==2);
    
    
    TaggedVisitor tv3 = new TaggedVisitor(tv2, tagListString);
    
    assertTrue(tv3.accruedTags().size() == 5);
    assertTrue(tv3.accruedTags().get(0).getTag().equals("tagFive"));
    assertTrue(tv3.accruedTags().get(0).getCount() ==5);
  }
  
  @Test
  public void GeolocatedVisitorTest() throws Exception {
    DMIDVisitor visitor1 = new DMIDVisitor(ImmutableVisitor.copyOf(visitor).withIpAddress("173.76.180.135"));
    GeolocatedVisitor geo = new GeolocatedVisitor(visitor1);

  ConfigTestHelper._setupFakeTestingContext();
  Config.setProperty("GEOIP2_CITY_DATABASE_PATH_OVERRIDE", "/Users/will/git/tomcat8/webapps/ROOT/WEB-INF/geoip2/GeoLite2-City.mmdb");

    String x = Config.getStringProperty("GEOIP2_CITY_DATABASE_PATH_OVERRIDE");
    
    assertTrue(GeoIp2CityDbUtil.getInstance() != null);
    
    
    //assertNotNull(geo.connectionType());
    //assertNotNull(geo.isp());
    assertNotNull(geo.city());
    assertNotNull(geo.country());
    assertNotNull(geo.postal());
    assertNotNull(geo.subdivision());
    assertNotNull(geo.location());
    assertNotNull(geo.continent());
    
    /**
    System.out.println(geo.city());
    System.out.println(geo.country().getIsoCode());
    System.out.println(geo.postal());
    System.out.println(geo.subdivision());
    System.out.println(geo.location());
    System.out.println(geo.continent().getCode());
    **/
  }
  
  
  
  
  
}
