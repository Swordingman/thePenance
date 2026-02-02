package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ForcedSummons extends BaseCard {

    public static final String ID = makeID("ForcedSummons");

    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPG_DAMAGE = 3;

    public ForcedSummons() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

        int drawAmt = 1;
        // 检查意图是否为攻击 (Damage > -1)
        if (m != null && m.getIntentBaseDmg() >= 0) {
            drawAmt = 2;
        }

        addToBot(new DrawCardAction(p, drawAmt));
    }
}