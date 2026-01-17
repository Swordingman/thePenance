package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.CrownOfThornsPower;
import thePenance.util.CardStats;

public class CrownOfThorns extends BaseCard {

    public static final String ID = makeID("CrownOfThorns");

    private static final int COST = 1;
    private static final int THORNS = 3;
    private static final int UPG_THORNS = 2;

    public CrownOfThorns() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(THORNS, UPG_THORNS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new CrownOfThornsPower(p, magicNumber), magicNumber));
    }
}