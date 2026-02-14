package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.powers.JustifiedDefensePower;
import thePenance.util.CardStats;

public class JustifiedRetaliation extends BaseCard {
    public static final String ID = makeID("JustifiedRetaliation");
    private static final int COST = 1;
    private static final int BARRIER = 7;
    private static final int UPG_BARRIER = 3; // 7->10

    public JustifiedRetaliation() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        int finalBarrier = BARRIER;
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL)
            finalBarrier = 5;

        setMagic(finalBarrier, UPG_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, magicNumber), magicNumber));
        addToBot(new ApplyPowerAction(p, p, new JustifiedDefensePower(p, 1), 1));
    }
}