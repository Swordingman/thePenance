package thePenance.relics;

import thePenance.PenanceMod;

public class ShopVoucher extends BaseRelic {
    public static final String ID = PenanceMod.makeID("ShopVoucher");

    public ShopVoucher() {
        super(ID, "ShopVoucher", RelicTier.COMMON, LandingSound.FLAT);
    }
}