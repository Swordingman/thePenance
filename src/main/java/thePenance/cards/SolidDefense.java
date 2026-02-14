package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class SolidDefense extends BaseCard {

    public static final String ID = makeID("SolidDefense");

    private static final int COST = 1;
    private static final int BLOCK = 4;
    private static final int UPGRADE_PLUS_BLOCK = 2;
    private static final int BARRIER = 4;
    private static final int UPGRADE_PLUS_BARRIER = 2;

    public SolidDefense() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        int finalBarrier = BARRIER;
        int finalUPGBarrier = UPGRADE_PLUS_BARRIER;

        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL){
            finalBarrier = 2;
            finalUPGBarrier = 1;
        }

        setBlock(BLOCK, UPGRADE_PLUS_BLOCK);
        setMagic(finalBarrier, finalUPGBarrier);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, magicNumber), magicNumber));
    }
}