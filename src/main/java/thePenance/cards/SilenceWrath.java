package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.SilenceWrathPower;
import thePenance.util.CardStats;

public class SilenceWrath extends BaseCard {

    public static final String ID = makeID("SilenceWrath");

    private static final int COST = 1;
    private static final int AMOUNT = 2;
    private static final int UPGRADE_AMOUNT = 1;

    public SilenceWrath() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(AMOUNT, UPGRADE_AMOUNT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new SilenceWrathPower(p, magicNumber), magicNumber));
    }
}