package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;
// 引用你的荆棘


public class CrownOfThornsPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("CrownOfThornsPower");

    private static final int HP_LOSS = 2;

    public CrownOfThornsPower(AbstractCreature owner, int thornsAmount) {
        super(POWER_ID, PowerType.BUFF, false, owner, thornsAmount);
        this.loadRegion("buffer");
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        // 1. 失去生命
        addToBot(new LoseHPAction(owner, owner, HP_LOSS));
        // 2. 获得荆棘
        addToBot(new ApplyPowerAction(owner, owner, new ThornAuraPower(owner, this.amount), this.amount));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + HP_LOSS + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}