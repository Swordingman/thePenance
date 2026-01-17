package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.InTheNameOfTheLawPower;
import thePenance.util.CardStats;

public class InTheNameOfTheLaw extends BaseCard {

    public static final String ID = makeID("InTheNameOfTheLaw");

    private static final int COST = 1;
    private static final int UPGRADED_COST = 0;

    public InTheNameOfTheLaw() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setCostUpgrade(UPGRADED_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new InTheNameOfTheLawPower(p, 1), 1));
    }
}