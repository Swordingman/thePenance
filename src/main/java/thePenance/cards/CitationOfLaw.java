package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class CitationOfLaw extends BaseCard {
    public static final String ID = makeID("CitationOfLaw");
    private static final int COST = 1;
    private static final int BASE_BARRIER = 5;
    private static final int PERCENT = 50;
    private static final int UPG_PERCENT = 20;

    public CitationOfLaw() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON, // 罕见
                CardTarget.SELF,
                COST
        ));

        int finalBarrier = BASE_BARRIER;
        int finalPercent = PERCENT;
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            finalBarrier = 3;
            finalPercent = 30;
        }

        setCustomVar("BaseBarrier", finalBarrier);
        setMagic(finalPercent, UPG_PERCENT);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得5点屏障
        int baseB = customVar("BaseBarrier");
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, baseB), baseB));

        // 2. 获得当前屏障百分比的屏障 (排队执行，确保上面的5点已经加上了)
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (p.hasPower(BarrierPower.POWER_ID)) {
                    int current = p.getPower(BarrierPower.POWER_ID).amount;
                    // 计算百分比
                    int gain = (int) (current * (magicNumber / 100.0f));

                    if (gain > 0) {
                        addToTop(new ApplyPowerAction(p, p, new BarrierPower(p, gain), gain));
                    }
                }
                this.isDone = true;
            }
        });
    }
}