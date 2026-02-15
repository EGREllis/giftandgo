package com.giftandgo.rest.api.validator.blacklist;

import com.giftandgo.rest.api.validator.ValidationRecord;

import java.util.Optional;
import java.util.Set;

public class InMemoryIspBlackList implements BlackList {
    private static final Set<String> ispBlackList = Set.of(
            "AWS",
            "GCP",
            "Azure"
    );

    @Override
    public Optional<String> excludeFromProcessing(ValidationRecord validationRecord) {
        if (validationRecord.isp() == null) {
            return Optional.of("Unknown ISP, blocked by BlackList.");
        }
        for (String isp : ispBlackList) {
            if (validationRecord.isp().toUpperCase().contains(isp.toUpperCase())) {
                return Optional.of("ISP in ISP BlackList.");
            }
        }
        return Optional.empty();
    }
}
