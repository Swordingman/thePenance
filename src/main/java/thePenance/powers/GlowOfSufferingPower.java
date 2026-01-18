package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;
import thePenance.patches.HpLostTracker;

public class GlowOfSufferingPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("GlowOfSufferingPower");

    // amount: 荆棘层数
    // multiplier: 屏障转换倍率 (1倍 或 2倍)
    private int barrierMultiplier;

    public GlowOfSufferingPower(AbstractCreature owner, int thornsAmt, int barrierMultiplier) {
        super(POWER_ID, PowerType.BUFF, false, owner, thornsAmt);
        this.barrierMultiplier = barrierMultiplier;
        this.updateDescription();
    }

    @Override
    public int onLoseHp(int damageAmount) {
        if (damageAmount > 0) {
            if (HpLostTracker.isLoseHpActionRunning) {
                this.flash();

                // 获得屏障
                int barrierGain = damageAmount * this.barrierMultiplier;
                addToBot(new ApplyPowerAction(owner, owner, new BarrierPower(owner, barrierGain), barrierGain));

                // 获得荆棘
                addToBot(new ApplyPowerAction(owner, owner, new ThornAuraPower(owner, this.amount), this.amount));
            }
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.barrierMultiplier + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

    // 处理堆叠：如果新来的倍率更高，就更新倍率；荆棘叠加
    public void stackPower(int stackAmount, int newMultiplier) {
        super.stackPower(stackAmount);
        if (newMultiplier > this.barrierMultiplier) {
            this.barrierMultiplier = newMultiplier;
        }
        updateDescription();
    }
}