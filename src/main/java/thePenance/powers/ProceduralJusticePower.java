package thePenance.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;

public class ProceduralJusticePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("ProceduralJusticePower");

    public ProceduralJusticePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}