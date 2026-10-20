package com.example.smartpantry.matching;

import com.example.smartpantry.model.Recipe;

import java.util.Collections;
import java.util.List;

/**
 * Outcome of {@link PantryMatcher#match(List, List)}.
 *
 * strictMatches - recipes the pantry can fully satisfy right now
 * almostMatches - recipes missing exactly one ingredient
 */
public class MatchResult {

    private final List<Recipe> strictMatches;
    private final List<Recipe> almostMatches;

    public MatchResult(List<Recipe> strictMatches, List<Recipe> almostMatches) {
        this.strictMatches = strictMatches == null
                ? Collections.emptyList() : strictMatches;
        this.almostMatches = almostMatches == null
                ? Collections.emptyList() : almostMatches;
    }

    public List<Recipe> getStrictMatches() { return strictMatches; }
    public List<Recipe> getAlmostMatches() { return almostMatches; }

    public boolean isEmpty() {
        return strictMatches.isEmpty() && almostMatches.isEmpty();
    }
}