package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

import thePenance.powers.ThornAuraPower;

public class UnwaveringPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("UnwaveringPower");

    // 定义减伤比例：30%
    private static final float DAMAGE_REDUCTION_RATE = 0.30F;

    public UnwaveringPower(AbstractCreature owner, int thornsAmount) {
        super(
                POWER_ID,
                PowerType.BUFF,
                false,
                owner,
                thornsAmount // 这里的 amount 用来存储回合结束加多少荆棘
        );
        this.loadRegion("barricade"); // 暂时借用"街垒"的图标，很有坚定不移的感觉
    }

    // 效果1：所有攻击牌获得消耗
    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        // 如果打出的是攻击牌，且它原本不消耗（避免重复触发逻辑）
        if (card.type == AbstractCard.CardType.ATTACK && !card.purgeOnUse) {
            this.flash();
            // 修改动作参数，强制让这张卡进入消耗堆
            action.exhaustCard = true;
        }
    }

    // 效果2：受到的伤害减少 30%
    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        // 只减少攻击伤害，不减少 HP_LOSS (生命流失)
        if (type != DamageInfo.DamageType.HP_LOSS) {
            return damage * (1.0F - DAMAGE_REDUCTION_RATE);
        }
        return damage;
    }

    // 效果3：回合结束增加荆棘
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.flash();
            // 获得 amount 层荆棘
            addToBot(new ApplyPowerAction(owner, owner,
                    new ThornAuraPower(owner, this.amount), this.amount));
        }
    }

    @Override
    public void updateDescription() {
        // 描述：攻击牌消耗。受到的伤害减少 30%。回合结束获得 #b{0} 点荆棘。
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new UnwaveringPower(owner, amount);
    }
}