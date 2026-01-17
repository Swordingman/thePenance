package thePenance.relics;

import thePenance.PenanceMod;

public class Innocent extends BaseRelic {
    public static final String ID = PenanceMod.makeID("Innocent");

    public Innocent() {
        super(ID, "Innocent", RelicTier.SPECIAL, LandingSound.CLINK);
    }
}