package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class ThornAuraPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("ThornAuraPower");
    // 移除 PowerStrings, NAME, DESCRIPTIONS，BasePower 已自动处理

    public ThornAuraPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased
                owner,             // 拥有者
                null,              // source
                amount,            // 层数
                true,              // initDescription
                true              // loadImage -> 设为 false，禁止自动找图
        );
    }

    @Override
    public void updateDescription() {
        // 使用父类 BasePower 提供的 DESCRIPTIONS
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.flash();

            // 逻辑保持不变：对所有敌人造成等同于层数的荆棘伤害
            this.addToBot(new DamageAllEnemiesAction(
                    null,
                    DamageInfo.createDamageMatrix(this.amount, true),
                    DamageInfo.DamageType.THORNS,
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new ThornAuraPower(owner, amount);
    }
}