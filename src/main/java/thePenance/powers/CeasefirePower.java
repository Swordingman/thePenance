package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;

public class CeasefirePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("CeasefirePower");

    public CeasefirePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.DEBUFF, true, owner, amount);
        this.loadRegion("entangled");
    }

    // --- 玩家侧效果：无法打出攻击牌 ---
    @Override
    public boolean canPlayCard(AbstractCard card) {
        if (this.owner.isPlayer && card.type == AbstractCard.CardType.ATTACK) {
            return false;
        }
        return true;
    }

    @Override
    public void atEndOfRound() {
        // 逻辑：如果是 0 层或 1 层，这一回合结束后它就该消失了
        if (this.amount <= 1) {
            // 直接移除
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            // 否则层数减 1
            // 推荐使用 ReducePowerAction，它会有红色的数字飘出动画，看起来更原生
            addToBot(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    @Override
    public void updateDescription() {
        if (owner.isPlayer) {
            this.description = DESCRIPTIONS[0]; // 玩家描述
        } else {
            this.description = DESCRIPTIONS[1]; // 敌人描述
        }
    }
}