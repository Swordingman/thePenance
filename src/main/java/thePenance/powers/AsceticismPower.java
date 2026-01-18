package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;
// 引用您的荆棘Power
import thePenance.powers.ThornAuraPower;

public class AsceticismPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("AsceticismPower");

    public AsceticismPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    public void onBarrierDamaged() {
        this.flash();
        addToBot(new ApplyPowerAction(owner, owner, new ThornAuraPower(owner, this.amount), this.amount));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new AsceticismPower(owner, amount);
    }
}