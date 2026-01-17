package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;

public class LawIsCanonPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("LawIsCanonPower");
    // 移除 PowerStrings, NAME, DESCRIPTIONS，BasePower 已处理

    public LawIsCanonPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased
                owner,             // 拥有者
                null,              // source
                amount,            // 层数
                true,              // initDescription
                false              // loadImage -> 设为 false，禁止自动找图
        );

        // 暂时借用原版 "Omega" (欧米伽) 的图标
        // 你也可以换成 "mantra" (真言/敬拜) 或者 "accuracy" (精准)
        this.loadRegion("omega");
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        // 必须是玩家回合结束
        if (!isPlayer) return;

        // 1. 检查是否有屏障 (BarrierPower)
        AbstractPower barrier = owner.getPower(BarrierPower.POWER_ID);
        if (barrier != null && barrier.amount > 0) {

            // 2. 检查是否有力量 (StrengthPower)
            AbstractPower strength = owner.getPower(StrengthPower.POWER_ID);
            if (strength != null && strength.amount > 0) {

                this.flash();

                // 3. 计算获得量： 力量值 * 此能力层数
                int gainAmount = strength.amount * this.amount;

                addToBot(new ApplyPowerAction(owner, owner,
                        new JudgementPower(owner, gainAmount), gainAmount));
            }
        }
    }

    @Override
    public void updateDescription() {
        // 使用 BasePower 自动获取的 DESCRIPTIONS
        // 确保你的本地化文件 (PowerStrings.json) 里该能力的 DESCRIPTIONS[0] 包含了 "{0}" 或类似占位符（如果描述不需要动态数字则无视）
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        // 【重要修正】之前这里写成了 new CodeOfRevengePower，已修正为 LawIsCanonPower
        return new LawIsCanonPower(owner, amount);
    }
}