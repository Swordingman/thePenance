package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class JudgementPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("JudgementPower");
    // 移除了 PowerStrings, NAME, DESCRIPTIONS，BasePower 已自动处理

    public JudgementPower(AbstractCreature owner, int amount) {
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

    // 核心逻辑保持不变
    public void onBarrierDamaged(AbstractCreature attacker) {
        if (attacker != null && attacker != this.owner && this.amount > 0) {
            this.flash();
            // 对攻击者造成等同于裁决值的伤害
            addToTop(new DamageAction(attacker,
                    new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

            if (this.owner.hasPower(CodeOfRevengePower.POWER_ID)) {
                // 获取复仇法典能力，并调用触发方法
                // 注意：这里需要强转，确保 CodeOfRevengePower 类也已经更新并存在
                CodeOfRevengePower power = (CodeOfRevengePower) this.owner.getPower(CodeOfRevengePower.POWER_ID);
                power.onJudgementTriggered();
            }
        }
    }

    @Override
    public void updateDescription() {
        // 使用父类 BasePower 提供的 DESCRIPTIONS
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new JudgementPower(owner, amount);
    }
}