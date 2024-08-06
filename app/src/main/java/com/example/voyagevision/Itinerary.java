package com.example.voyagevision;

import java.util.List;

public class Itinerary {
    private final String id;
    private final List<String> attractionIds;

    public Itinerary(String id, List<String> attractionIds) {
        this.id = id;
        this.attractionIds = attractionIds;
    }

    public String getId() {
        return id;
    }
    public List<String> getAttractionIds() {
        return attractionIds;
    }
}

