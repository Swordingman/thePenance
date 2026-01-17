package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class InescapableNetPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("InescapableNetPower");

    public InescapableNetPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.loadRegion("entangled"); // 借用缠绕图标
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        // 获得屏障
        addToBot(new ApplyPowerAction(owner, owner, new BarrierPower(owner, this.amount), this.amount));
        // 抽1张牌
        addToBot(new DrawCardAction(owner, 1));

        // 移除自身
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}