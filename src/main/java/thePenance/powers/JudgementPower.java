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

    public JudgementPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased
                owner,             // 拥有者
                null,              // source
                amount,            // 层数
                true,              // initDescription
                true              // loadImage
        );
    }

    // 核心逻辑保持不变
    public void onBarrierDamaged(AbstractCreature attacker) {
        if (attacker != null && attacker != this.owner && this.amount > 0) {
            addToTop(new thePenance.actions.TriggerJudgementAction(attacker));
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