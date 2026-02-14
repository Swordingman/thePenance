package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class EmergencyRepair extends BaseCard {

    public static final String ID = makeID("EmergencyRepair");

    private static final int COST = 1;
    private static final int BARRIER = 4;
    private static final int UPG_BARRIER = 3;

    public EmergencyRepair() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        int finalBarrier = BARRIER;
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL)
            finalBarrier = 3;

        setMagic(finalBarrier, UPG_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, magicNumber), magicNumber));
        addToBot(new DrawCardAction(p, 1));
    }
}