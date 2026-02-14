package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.ResoluteAction;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class Resolute extends BaseCard {

    public static final String ID = makeID("Resolute");

    private static final int COST = -1; // X 费
    private static final int BARRIER_MULTIPLIER = 4;
    private static final int UPGRADE_BARRIER_MULTIPLIER = 1;

    public Resolute() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.BASIC,
                CardTarget.SELF,
                COST
        ));

        int finalBarrier = BARRIER_MULTIPLIER;

        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL)
            finalBarrier = 2;

        setMagic(finalBarrier, UPGRADE_BARRIER_MULTIPLIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = 3;
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, amount), amount));
        // 这里的 magicNumber 会自动包含升级后的数值
        addToBot(new ResoluteAction(p, magicNumber, upgraded, freeToPlayOnce, energyOnUse));
    }
}