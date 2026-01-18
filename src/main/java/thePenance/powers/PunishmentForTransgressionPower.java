package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;

public class PunishmentForTransgressionPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("PunishmentForTransgressionPower");

    public PunishmentForTransgressionPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.flash();
            // 获得 amount 层正当防卫
            addToBot(new ApplyPowerAction(owner, owner,
                    new JustifiedDefensePower(owner, this.amount), this.amount));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}