package com.dotcms.visitor.domain;



import java.io.IOException;

import org.pmw.tinylog.Logger;

import com.dotcms.repackage.com.maxmind.geoip2.DatabaseReader;
import com.dotcms.repackage.com.maxmind.geoip2.exception.GeoIp2Exception;
import com.dotcms.repackage.com.maxmind.geoip2.model.AnonymousIpResponse;
import com.dotcms.repackage.com.maxmind.geoip2.model.CityResponse;
import com.dotcms.repackage.com.maxmind.geoip2.model.ConnectionTypeResponse;
import com.dotcms.repackage.com.maxmind.geoip2.model.CountryResponse;
import com.dotcms.repackage.com.maxmind.geoip2.model.DomainResponse;
import com.dotcms.repackage.com.maxmind.geoip2.model.IspResponse;
import com.dotcms.repackage.com.maxmind.geoip2.record.City;
import com.dotcms.repackage.com.maxmind.geoip2.record.Continent;
import com.dotcms.repackage.com.maxmind.geoip2.record.Country;
import com.dotcms.repackage.com.maxmind.geoip2.record.Location;
import com.dotcms.repackage.com.maxmind.geoip2.record.Postal;
import com.dotcms.repackage.com.maxmind.geoip2.record.Subdivision;
import com.dotcms.util.GeoIp2CityDbUtil;




public class GeolocatedVisitor extends AbstractVisitor {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private  DatabaseReader db =null;
  public enum GeoData {
    CITY("city"), 
    COUNTRY("country"), 
    ISP("isp"), 
    ANONYMOUS("anonymous"), 
    DOMAIN("domain"), 
    CONNECTION("connectionType");

    private final String key;

    @Override
    public String toString() {
      return this.key;
    }

    GeoData(String x) {
      this.key = x;
    }
  }


  public GeolocatedVisitor(Visitor visitor) {
    super(visitor);

  }

  
  private DatabaseReader db(){
    db = (db==null) ? GeoIp2CityDbUtil.getInstance().getDatabaseReader() : db;
    return db;
  }
  
  
  public City city(){
    return cityResponse().getCity();
  }
  
  public Continent continent(){
    return cityResponse().getContinent();
  }
  
  public Postal postal(){
    return cityResponse().getPostal();
  }
  
  public Location location(){
    return cityResponse().getLocation();
  }
  
  private CityResponse cityResponse(){

    try {
      CityResponse cr =(CityResponse) map.get(GeoData.CITY.toString());
      if(cr==null){
        cr = db().city(this.inetAddress());
        map.put(GeoData.CITY.toString(), cr);
      }
      return cr;
    } catch (Exception e) {
      Logger.warn(this.getClass().toString(), e.getMessage());
      return null;
    }
  }
  
  public Country country(){

      return cityResponse().getCountry();
    
  }
  public Subdivision subdivision(){
    return cityResponse().getMostSpecificSubdivision();
  }
  
  
  /*
  public IspResponse isp(){
    try {
      return (IspResponse) map.putIfAbsent(GeoData.ISP.toString(), db().isp(this.inetAddress()));
    } catch (Exception e) {
      Logger.warn(this.getClass().toString(), e.getMessage());
      return null;
    }
  }
  public AnonymousIpResponse anonymous(){

    try {
      return (AnonymousIpResponse) map.putIfAbsent(GeoData.ANONYMOUS.toString(), db().anonymousIp(this.inetAddress()));
    } catch (Exception e) {
      Logger.warn(this.getClass().toString(), e.getMessage());
      return null;
    }
  }
  public DomainResponse domain(){

    try {
      return (DomainResponse) map.putIfAbsent(GeoData.DOMAIN.toString(), db().domain(this.inetAddress()));
    } catch (Exception e) {
      Logger.warn(this.getClass().toString(), e.getMessage());
      return null;
    }
  }
  
  public ConnectionTypeResponse connectionType(){

    try {
      return (ConnectionTypeResponse) map.putIfAbsent(GeoData.CONNECTION.toString(), db().connectionType(this.inetAddress()));
    } catch (Exception e) {
      Logger.warn(this.getClass().toString(), e.getMessage());
      return null;
    }
  }
  */
}
