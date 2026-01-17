package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.CeasefirePower;
import thePenance.util.CardStats;

public class FlailSmash extends BaseCard {

    public static final String ID = makeID("FlailSmash");

    private static final int COST = 2;
    private static final int DAMAGE = 7;
    private static final int UPG_DAMAGE = 3; // 7 -> 10

    public FlailSmash() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
        setExhaust(true); // 设置消耗
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        // 2. 给予止戈 (1回合)
        addToBot(new ApplyPowerAction(m, p, new CeasefirePower(m, 1), 1));
    }
}