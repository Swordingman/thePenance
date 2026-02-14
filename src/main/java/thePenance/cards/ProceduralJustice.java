package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
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

        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            // Hell: 获得格挡时，获得2点屏障
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).EXTENDED_DESCRIPTION[0];
        } else {
            // Normal: 获得格挡时，获得等量屏障
            // 建议在普通难度的描述最后加一句提示，或者让它变成 "Unique" (不可叠加)
            // 但如果不想改机制，保持原样即可。
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).DESCRIPTION;
        }
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new ProceduralJusticePower(p, 1), 1));
    }
}