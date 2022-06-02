package com.gsu.knowledgebase.util;

import java.util.Locale;

/**
 * Created by cnytync on 02/03/2018.
 */
public class LanguageUtils {
    /**
     * Locale TR value for language specific tasks
     * @return Locale tr-TR object
     */
    public static Locale locale_tr_TR() {
        return new Locale.Builder().setLanguage("tr").setRegion("TR").build();
    }

    /**
     * Locale TR value for language specific tasks
     * @return Locale tr-TR object
     */
    public static Locale locale_en_UK() {
        return new Locale.Builder().setLanguage("en").setRegion("UK").build();
    }

    /**
     * Locale TR value for language specific tasks
     * @return Locale tr-TR object
     */
    public static Locale locale_it_IT() {
        return new Locale.Builder().setLanguage("it").setRegion("IT").build();
    }

}
