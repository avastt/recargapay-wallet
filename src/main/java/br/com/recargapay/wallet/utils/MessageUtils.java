package br.com.recargapay.wallet.utils;

import br.com.recargapay.wallet.exception.ErrorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Component
@RequiredArgsConstructor
public class MessageUtils {
    private final MessageSource messageSource;

    public String getMessage(final ErrorKey errorKey, final Object... param){
        return messageSource.getMessage(errorKey.getKey(), param, getLocale());
    }
}
