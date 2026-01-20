package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class GuardianOfTheLawPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("GuardianOfTheLawPower");
    // 不需要手动定义 NAME 和 DESCRIPTIONS，BasePower 会自动读取 localization

    public GuardianOfTheLawPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // 是否回合制
                owner,             // 拥有者
                null,              // 来源
                amount,            // 层数
                true,              // 是否初始化描述
                true              // loadImage -> 设为 false，表示我们要手动指定原版图标
        );
    }

    // --- 当屏障受到伤害时调用 ---
    public void onBarrierDamaged() {
        this.flash();
        // 获得裁决
        addToBot(new ApplyPowerAction(owner, owner, new JudgementPower(owner, this.amount), this.amount));
    }

    @Override
    public void updateDescription() {
        // 使用父类 BasePower 提供的 DESCRIPTIONS
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new GuardianOfTheLawPower(owner, amount);
    }
}