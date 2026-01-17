package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import thePenance.PenanceMod;

public class SilenceWrathPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("SilenceWrathPower");
    // 移除 PowerStrings, NAME, DESCRIPTIONS，BasePower 已处理

    public SilenceWrathPower(AbstractCreature owner, int amount) {
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

        // 借用原版 "Anger" (愤怒) 的图标
        this.loadRegion("anger");
    }

    // --- 核心方法：当屏障破碎时调用 ---
    // 保持原逻辑不变
    public void onBarrierBroken() {
        this.flash(); // 闪烁图标

        // 1. 获得力量
        addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, this.amount), this.amount));

        // 2. 给予所有敌人虚弱
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, owner, new WeakPower(mo, this.amount, false), this.amount));
            }
        }
    }

    @Override
    public void updateDescription() {
        // 使用 BasePower 提供的 DESCRIPTIONS 数组
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

    @Override
    public AbstractPower makeCopy() {
        return new SilenceWrathPower(owner, amount);
    }
}