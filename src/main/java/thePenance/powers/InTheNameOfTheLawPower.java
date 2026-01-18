package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class InTheNameOfTheLawPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("InTheNameOfTheLawPower");

    public InTheNameOfTheLawPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased
                owner,             // 拥有者
                null,              // source
                amount,            // 层数
                true,              // initDescription
                true              // loadImage -> 设为 false，因为要借用 thorns 图标
        );
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new InTheNameOfTheLawPower(owner, amount);
    }
}