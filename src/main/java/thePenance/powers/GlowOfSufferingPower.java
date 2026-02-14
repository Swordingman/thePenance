package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;
import thePenance.patches.HpLostTracker;

public class GlowOfSufferingPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("GlowOfSufferingPower");

    // 这里存的是 100, 150, 200 这种整数
    private int multiplierPct;

    // 只需要传一个倍率进来就行，屏障和荆棘倍率是一样的
    public GlowOfSufferingPower(AbstractCreature owner, int multiplierPct) {
        super(POWER_ID, PowerType.BUFF, false, owner, multiplierPct); // amount 存倍率也行，或者分开存
        this.multiplierPct = multiplierPct;
        this.amount = multiplierPct; // 为了方便 stackPower，我们也更新 amount
        updateDescription();
    }

    @Override
    public int onLoseHp(int damageAmount) {
        if (damageAmount > 0 && HpLostTracker.isLoseHpActionRunning) {
            this.flash();

            // --- 简单的数学计算 ---
            // 失去 10 血 * (150 / 100.0) = 15
            int bonus = (int)(damageAmount * (this.multiplierPct / 100.0f));

            if (bonus > 0) {
                // 给屏障
                addToBot(new ApplyPowerAction(owner, owner, new BarrierPower(owner, bonus), bonus));
                // 给荆棘
                addToBot(new ApplyPowerAction(owner, owner, new ThornAuraPower(owner, bonus), bonus));
            }
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        // Power 上的描述还是动态显示一下比较好，防止玩家忘了倍率是多少
        // 把 150 转成 "1.5"
        float displayVal = this.multiplierPct / 100.0f;
        this.description = DESCRIPTIONS[0] + displayVal + DESCRIPTIONS[1] + displayVal + DESCRIPTIONS[2];
    }

    // 堆叠逻辑：取最大值
    public void stackPower(int newMultiplierPct) {
        if (newMultiplierPct > this.multiplierPct) {
            this.multiplierPct = newMultiplierPct;
            this.amount = newMultiplierPct;
            updateDescription();
        }
    }
}