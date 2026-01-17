package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class ThornyPathPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("ThornyPathPower");
    // 移除 PowerStrings, NAME, DESCRIPTIONS，BasePower 已处理

    public ThornyPathPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased
                owner,             // 拥有者
                null,              // source
                amount,            // 层数 (这里代表百分比，例如 50 代表 50%)
                true,              // initDescription
                false              // loadImage -> 设为 false，禁止自动找图
        );

        // 暂时借用原版 "Evolve" (进化) 图标
        this.loadRegion("evolve");
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            // 1. 获取当前裁决层数
            int currentJudgement = 0;
            if (this.owner.hasPower(JudgementPower.POWER_ID)) {
                currentJudgement = this.owner.getPower(JudgementPower.POWER_ID).amount;
            }

            // 2. 如果有裁决，计算获得的荆棘环身数量
            if (currentJudgement > 0) {
                // 计算逻辑：裁决 * (amount / 100)
                // 例如 amount 是 50，裁决是 10，则获得 5 层荆棘环身
                int gainAmount = (int)(currentJudgement * (this.amount / 100.0f));

                if (gainAmount > 0) {
                    this.flash(); // 闪烁图标提示触发

                    // 3. 给予荆棘环身 (ThornAuraPower)
                    addToBot(new ApplyPowerAction(this.owner, this.owner,
                            new ThornAuraPower(this.owner, gainAmount), gainAmount));
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        // 使用父类 BasePower 提供的 DESCRIPTIONS
        // JSON 描述示例："回合结束时，获得等同于你 #y裁决 #b%d% 的 #y荆棘环身。"
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new ThornyPathPower(owner, amount);
    }
}