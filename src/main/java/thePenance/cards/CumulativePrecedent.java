package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.CumulativePrecedentPower;
import thePenance.util.CardStats;

public class CumulativePrecedent extends BaseCard {
    public static final String ID = makeID("CumulativePrecedent");
    private static final int COST = 1;
    private static final int BARRIER_AMT = 3;
    private static final int UPG_BARRIER_AMT = 1; // 3->4

    public CumulativePrecedent() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON, // 罕见
                CardTarget.SELF,
                COST
        ));
        setMagic(BARRIER_AMT, UPG_BARRIER_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new CumulativePrecedentPower(p, magicNumber), magicNumber));
    }
}