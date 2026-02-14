package thePenance.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.BlockToBarrierAction;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class LawModification extends BaseCard {

    public static final String ID = makeID("LawModification");

    private static final int COST = 1;
    private static final int UPGRADED_COST = 0;

    public LawModification() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        this.selfRetain = true;
        this.exhaust = true;

        setCostUpgrade(UPGRADED_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new BlockToBarrierAction());
    }
}