package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class PenaltyRegression extends BaseCard {
    public static final String ID = makeID("PenaltyRegression");
    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPG_DAMAGE = 3;

    public PenaltyRegression() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        // 2. 减少自身 Debuff 层数
        // 注意：我们不能在遍历 List 时修改 List (ReducePowerAction 可能会导致 remove)，
        // 但 addToBot 是将动作加入队列后续执行，不会立即修改 p.powers，所以这里直接遍历是安全的。
        for (AbstractPower power : p.powers) {
            // 判断是否为 Debuff 且层数 > 0 (有些特殊 Debuff 只有ID没有层数，通常层数为 -1)
            if (power.type == AbstractPower.PowerType.DEBUFF && power.amount > 0) {
                addToBot(new ReducePowerAction(p, p, power, 1));
            }
        }
    }
}