package com.giftandgo.rest.api.validator.blacklist;

import com.giftandgo.rest.api.validator.ValidationRecord;

import java.util.Optional;
import java.util.Set;

public class InMemoryCountryBlackList implements BlackList {
    private static final Set<String> countryCodeBlackList = Set.of(
        "ES",   // Spain
        "HK",   // Hong Kong (China)
        "CN",   // China
        "US"    // USA
    );

    @Override
    public Optional<String> excludeFromProcessing(ValidationRecord validationRecord) {
        if (validationRecord.countryCode() == null) {
            return Optional.of("Country is unknown, might be on the BlackList.");
        }
        if (countryCodeBlackList.contains(validationRecord.countryCode().toUpperCase())) {
            return Optional.of("Country in Country BlackList.");
        }
        return Optional.empty();
    }
}
