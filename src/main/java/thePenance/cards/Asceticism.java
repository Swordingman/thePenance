package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.AsceticismPower;
import thePenance.util.CardStats;

public class Asceticism extends BaseCard {

    public static final String ID = makeID("Asceticism");

    private static final int COST = 1;
    private static final int THORNS_GAIN = 2;
    private static final int UPG_THORNS_GAIN = 1; // 2->3

    public Asceticism() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));

        setMagic(THORNS_GAIN, UPG_THORNS_GAIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new AsceticismPower(p, magicNumber), magicNumber));
    }
}