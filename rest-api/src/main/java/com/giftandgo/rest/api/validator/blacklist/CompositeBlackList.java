package com.giftandgo.rest.api.validator.blacklist;

import com.giftandgo.rest.api.validator.ValidationRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CompositeBlackList implements BlackList {
    private final List<BlackList> blackLists;

    public CompositeBlackList(List<BlackList> blackLists) {
        this.blackLists = blackLists;
    }

    public CompositeBlackList() {
        // This could be done better.
        this(List.of(new InMemoryCountryBlackList(), new InMemoryIspBlackList()));
    }

    @Override
    public Optional<String> excludeFromProcessing(ValidationRecord validationRecord) {
        for (BlackList blackList : blackLists) {
            Optional<String> response = blackList.excludeFromProcessing(validationRecord);
            if (response.isPresent()) {
                return response;
            }
        }
        return Optional.empty();
    }
}
