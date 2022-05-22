package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.specialCards.SpecialCardName;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that manages the use of the special cards and their effect
 */
public class SpecialCardManager {
private Set<SpecialCardName> specialCardNames;

public SpecialCardManager(Set<SpecialCardName> specialCardNames){
    this.specialCardNames = new HashSet<>(specialCardNames);
}


}
