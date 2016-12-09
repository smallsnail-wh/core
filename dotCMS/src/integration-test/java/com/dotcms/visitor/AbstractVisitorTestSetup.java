package com.dotcms.visitor;

import java.util.List;
import java.util.Locale;

import com.dotcms.visitor.domain.ImmutableVisitor;
import com.dotcms.visitor.domain.Visitor;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.google.common.collect.ImmutableList;

import eu.bitwalker.useragentutils.UserAgent;

public class AbstractVisitorTestSetup {
  
  

  final static String ID = "id";
  final static String IP = "127.0.0.1";
  final static String KEYTAG  = "KEYTAG";
  final static String PERSONA_TAGS  = "tag1, tag2";
  
  
  final static String REFERER = "http://referer.com";
  final static UserAgent AGENT = new UserAgent("I don't know");
  final static Language language = new Language();
  final static Locale locale= Locale.US;
  
  
  final static List<String> tagList = ImmutableList.of("tagOne", "tagTwo", "tagTwo", "tagThree", "tagThree", "tagThree");
  
  
  final static String tagListString = "tagFour,tagFour,tagFour,tagFour,tagFive ,tagFive ,tagFive ,tagFive ,tagFive ,,,";
  
  
  
  
  
  final static Visitor visitor = ImmutableVisitor.builder()
      .id(ID)
      .ipAddress(IP)
      .language(language)
      .referer(REFERER)
      .userAgent(AGENT)
      .locale(locale)
      .build();
  


}
