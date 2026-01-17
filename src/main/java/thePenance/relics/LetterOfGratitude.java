package thePenance.relics;

import thePenance.PenanceMod;

public class LetterOfGratitude extends BaseRelic {
    public static final String ID = PenanceMod.makeID("LetterOfGratitude");

    public LetterOfGratitude() {
        super(ID, "LetterOfGratitude", RelicTier.SPECIAL, LandingSound.FLAT);
    }
}