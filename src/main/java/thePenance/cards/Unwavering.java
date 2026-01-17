package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.UnwaveringPower;
import thePenance.util.CardStats;

public class Unwavering extends BaseCard {

    public static final String ID = makeID("Unwavering");

    private static final int COST = 3;

    private static final int THORNS_GAIN = 3;
    private static final int UPGRADE_PLUS_THORNS = 1;

    public Unwavering() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));

        setMagic(THORNS_GAIN, UPGRADE_PLUS_THORNS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new UnwaveringPower(p, magicNumber), magicNumber));
    }
}