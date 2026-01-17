package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.ThornyPathPower;
import thePenance.util.CardStats;

public class ThornyPath extends BaseCard {

    public static final String ID = makeID("ThornyPath");

    private static final int COST = 1;
    private static final int UPGRADED_COST = 0;
    private static final int MAGIC = 50;

    public ThornyPath() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(MAGIC);
        setCostUpgrade(UPGRADED_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new ThornyPathPower(p, magicNumber), magicNumber));
    }
}