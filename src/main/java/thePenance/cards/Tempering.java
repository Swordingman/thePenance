package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class Tempering extends BaseCard {

    public static final String ID = makeID("Tempering");

    private static final int COST = 1;
    private static final int HP_LOSS = 3;
    private static final int BARRIER = 12;
    private static final int UPG_BARRIER = 3; // 12 -> 15

    public Tempering() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        int finalBarrier = BARRIER;
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL)
            finalBarrier = 10;

        setMagic(finalBarrier, UPG_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 失去生命
        addToBot(new LoseHPAction(p, p, HP_LOSS));
        // 获得屏障
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, magicNumber), magicNumber));
    }
}