package fi.fileuploader.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class LanguageResolver extends AcceptHeaderLocaleResolver {

    private static final List<Locale> SUPPORTED_LOCALES = Collections.singletonList(
        Locale.ENGLISH
    );

    @Override
    public Locale resolveLocale(final HttpServletRequest request) {
        final String headerLang = request.getHeader("Accept-Language");
        return headerLang == null || headerLang.isEmpty()
               ? SUPPORTED_LOCALES.get(0)
               : Locale.lookup(Locale.LanguageRange.parse(headerLang), SUPPORTED_LOCALES);
    }

    public List<Locale> getLocales() {
        return SUPPORTED_LOCALES;
    }
}
