package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class BurdenedJourney extends BaseCard {

    public static final String ID = makeID("BurdenedJourney");

    private static final int COST = 1;
    private static final int AMT = 3;
    private static final int UPG_AMT = 2;

    public BurdenedJourney() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        int finalAmt = AMT;
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL)
            finalAmt = 2;

        // 设置魔法数字（裁决）
        setMagic(finalAmt, UPG_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, magicNumber), magicNumber));
        addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, magicNumber), magicNumber));
    }
}