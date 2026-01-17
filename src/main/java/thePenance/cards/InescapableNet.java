package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.InescapableNetPower;
import thePenance.util.CardStats;

public class InescapableNet extends BaseCard {
    public static final String ID = makeID("InescapableNet");
    private static final int COST = 1;
    private static final int BARRIER = 10;
    private static final int UPG_BARRIER = 4; // 10->14

    public InescapableNet() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON, // 罕见
                CardTarget.SELF,
                COST
        ));
        setMagic(BARRIER, UPG_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new InescapableNetPower(p, magicNumber), magicNumber));
    }
}