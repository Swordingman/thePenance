package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class ArtOfTheHidingFox extends BaseCard {

    public static final String ID = makeID("ArtOfTheHidingFox");
    private static final int COST = 1;
    private static final int BUFF_AMT = 3;
    private static final int BARRIER = 25;

    public ArtOfTheHidingFox() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.SELF,
                COST
        ));
        setMagic(BUFF_AMT);
        setExhaust(true);
        // 我们用 customVar 来存屏障，或者直接写死，这里用 customVar 方便显示
        setCustomVar("Barrier", BARRIER);

        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得 3 力量
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));
        // 2. 获得 3 裁决
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, magicNumber), magicNumber));
        // 2. 获得 3 荆棘环身
        addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, magicNumber), magicNumber));
        // 3. 获得 25 屏障
        int barrier = customVar("Barrier");
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrier), barrier));

        // 4. 升级后保留手牌
        if (upgraded) {
            // EquilibriumPower (均衡) 的效果就是回合结束不弃牌
            addToBot(new ApplyPowerAction(p, p, new EquilibriumPower(p, 1), 1));
        }

        // 5. 强制结束回合
        addToBot(new PressEndTurnButtonAction());
    }

    @Override
    public void triggerWhenDrawn() {
        addToBot(new NewQueueCardAction(this, true, false, true));
    }
}