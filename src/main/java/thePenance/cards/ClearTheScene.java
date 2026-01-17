package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ClearTheScene extends BaseCard {
    public static final String ID = makeID("ClearTheScene");
    private static final int COST = 1;
    private static final int DAMAGE = 11;
    private static final int UPG_DAMAGE = 4; // 11->15
    private static final int HP_COST = 3;

    public ClearTheScene() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ALL_ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(p, p, HP_COST));
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }
}