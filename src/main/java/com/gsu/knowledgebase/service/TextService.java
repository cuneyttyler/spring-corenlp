package com.gsu.knowledgebase.service;

import com.gsu.knowledgebase.util.LanguageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Service("textService")
public class TextService {

	protected static final String bundleName = "texts";
	
	protected ResourceBundle bundleDefault;
	protected ResourceBundle bundle_tr_TR;
	protected ResourceBundle bundle_en_UK;
	
	public static class SYSTEM_LANGUAGES {
		
		public static String LANGUAGE_EN = "en";
		public static String LANGUAGE_TR = "tr";
		public static String LANGUAGE_IT = "it";
		public static String LANGUAGE_DEFAULT = LANGUAGE_EN;
		public static String LOCALE_LANGUAGE_DEFAULT = LanguageUtils.locale_en_UK().toString();
		public static Locale LOCALE_DEFAULT = LanguageUtils.locale_en_UK();
		
		public static List<String> languageStringsList;
		public static List<String> languageLocaleStringsList;
		public static List<String> languageLocaleStringsListWithoutTr;
		public static List<Locale> languageLocalesList;
		
		
		static {
			/*
			 * PREVENT ORDERS AND AMOUNTS!
			 */
			languageStringsList = new LinkedList<String>();
			languageStringsList.add(LANGUAGE_EN);
			languageStringsList.add(LANGUAGE_TR);
			languageStringsList.add(LANGUAGE_IT);
			
			languageLocaleStringsList = new LinkedList<String>();
			languageLocaleStringsList.add(LanguageUtils.locale_en_UK().toString());
			languageLocaleStringsList.add(LanguageUtils.locale_tr_TR().toString());
			languageLocaleStringsList.add(LanguageUtils.locale_it_IT().toString());
			
			languageLocaleStringsListWithoutTr = new LinkedList<String>();
			languageLocaleStringsListWithoutTr.add(LanguageUtils.locale_en_UK().toString());
			languageLocaleStringsListWithoutTr.add(LanguageUtils.locale_it_IT().toString());
			
			languageLocalesList = new LinkedList<Locale>();
			languageLocalesList.add(LanguageUtils.locale_en_UK());
			languageLocalesList.add(LanguageUtils.locale_tr_TR());
			languageLocalesList.add(LanguageUtils.locale_it_IT());
		}
		
	}
	
	public static class Keys {
		//Just the keys that are used at Java side.
		public static String INDEX_TITLE = "index.title";


        public static class Month {
			public static String JANUARY = "month.january";
			public static String FEBRUARY = "month.february";
			public static String MARCH = "month.march";
			public static String APRIL = "month.april";
			public static String MAY = "month.may";
			public static String JUNE = "month.june";
			public static String JULY = "month.july";
			public static String AUGUST = "month.august";
			public static String SEPTEMBER = "month.september";
			public static String OCTOBER = "month.october";
			public static String NOVEMBER = "month.november";
			public static String DECEMBER = "month.december";
		}
	}
	
	public static class JSPPlaceHolders {
		public static String TITLE = "title";
		public static String ERROR_TEXT = "errorText";
		public static String ERROR_TITLE = "errorTitle";
	}
	
	public static class DB {
		public static final String CODE_VALUE_VALUE = "value";
		public static final String TAG_NAME = "name";
		public static final String ROLE_DISPLAY_NAME = "display_name";
		public static final String USER_TYPE_VALUE = "value";
		public static final String PRESS_SECTOR = "sectorname";
		public static final String PRESS_TITLE = "titlename";
	}
	
	@PostConstruct
	public void init() throws Exception {
//		bundleDefault = ResourceBundle.getBundle(bundleName);
//		bundle_en_UK = ResourceBundle.getBundle(bundleName, LanguageUtils.locale_en_UK());
//		bundle_tr_TR = ResourceBundle.getBundle(bundleName, LanguageUtils.locale_tr_TR());
	}
	
	/**
	 * Get text according to the locale
	 * @param key Key of the string inside properties files
	 * @param locale Locale to get text from
	 * @return Text belongs to the key and locale
	 */
	public String getText(String key, Locale locale) {
		if (locale.equals(LanguageUtils.locale_tr_TR())) {
			return bundle_tr_TR.getString(key);
		} else if (locale.equals(LanguageUtils.locale_en_UK())) {
			return bundle_en_UK.getString(key);
		} else {
			return bundleDefault.getString(key);
		}
	}
	
}
