package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;

public class PunishmentForTransgressionPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("PunishmentForTransgressionPower");

    // 用来标记本回合是否打出过攻击牌
    private boolean attackPlayed = false;

    public PunishmentForTransgressionPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    // 1. 回合开始时，重置标记
    @Override
    public void atStartOfTurn() {
        this.attackPlayed = false;
    }

    // 2. 每次出牌时，检查是否为攻击牌
    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            this.attackPlayed = true;
        }
    }

    // 3. 回合结束时判断
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            // 如果没有打出过攻击牌
            if (!this.attackPlayed) {
                this.flash();

                // 给一层正当防卫（这里我保留了 this.amount，意味着如果你有2层这个能力，就给2层正当防卫。
                // 如果你严格想要只给“1层”，请把下面的 this.amount 改成 1）
                addToBot(new ApplyPowerAction(owner, owner,
                        new JustifiedDefensePower(owner, this.amount), this.amount));
            }
        }
    }

    @Override
    public void updateDescription() {
        // 记得去 strings.json 修改描述，写明“回合结束时，若未打出攻击牌...”
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}