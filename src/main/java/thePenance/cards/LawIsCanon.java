package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.LawIsCanonPower;
import thePenance.util.CardStats;

public class LawIsCanon extends BaseCard {

    public static final String ID = makeID("LawIsCanon");

    private static final int COST = 2;
    private static final int UPGRADED_COST = 1;

    public LawIsCanon() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));

        setCostUpgrade(UPGRADED_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得 1 层 LawIsCanonPower
        addToBot(new ApplyPowerAction(p, p, new LawIsCanonPower(p, 1), 1));
    }
}