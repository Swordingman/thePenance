package thePenance.powers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.powers.ConfusionPower;
// 引入接口
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import thePenance.PenanceMod;

// 1. 实现 InvisiblePower 接口
public class TemporaryConfusionPower extends AbstractPower implements InvisiblePower {
    public static final String POWER_ID = PenanceMod.makeID("TemporaryConfusionPower");

    public TemporaryConfusionPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        // 2. 因为是隐身的，不需要设置 name, description，也不需要 loadRegion
        this.name = "";
        this.description = "";
        // 不需要 loadRegion("confusion");
        // 也不需要 updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            // 移除原本的混乱
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, ConfusionPower.POWER_ID));
            // 移除自己
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    // 3. InvisiblePower 接口不需要实现任何额外方法，它只是一个标记
}