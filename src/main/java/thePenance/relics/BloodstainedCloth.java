package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;
import thePenance.powers.ThornAuraPower; // 你的荆棘

public class BloodstainedCloth extends BaseRelic {
    public static final String ID = PenanceMod.makeID("BloodstainedCloth");

    public BloodstainedCloth() {
        super(ID, "BloodstainedCloth", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new StrengthPower(AbstractDungeon.player, -2), -2));
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new ThornAuraPower(AbstractDungeon.player, 8), 8));
    }
}