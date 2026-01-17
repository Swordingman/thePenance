package thePenance.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.util.CardStats;

public class DeadlyEnemy extends BaseCard {
    public static final String ID = makeID("DeadlyEnemy");

    public DeadlyEnemy() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.NONE,
                -2
        ));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }
}