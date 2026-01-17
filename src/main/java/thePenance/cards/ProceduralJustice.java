package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.ProceduralJusticePower;
import thePenance.util.CardStats;

public class ProceduralJustice extends BaseCard {
    public static final String ID = makeID("ProceduralJustice");
    private static final int COST = 2;
    private static final int UPGRADE_COST = 1;

    public ProceduralJustice() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON, // 罕见
                CardTarget.SELF,
                COST
        ));
        setCostUpgrade(UPGRADE_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new ProceduralJusticePower(p, 1), 1));
    }
}