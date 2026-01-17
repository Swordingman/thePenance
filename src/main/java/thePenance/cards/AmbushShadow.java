package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import thePenance.PenanceMod;
import thePenance.util.CardStats;

public class AmbushShadow extends BaseCard {

    public static final String ID = PenanceMod.makeID("AmbushShadow");
    private static final int COST = 1;
    private static final int DISCARD_AMT = 3;
    private static final int UPG_DISCARD_AMT = -1; // 3->2 (负面效果减少)

    public AmbushShadow() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.SELF,
                COST
        ));
        setMagic(DISCARD_AMT, UPG_DISCARD_AMT);
        setExhaust(true);
        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 随机丢弃手牌
        // DiscardAction 第二个参数是 source，第三个是 amount，第四个是 isRandom
        addToBot(new DiscardAction(p, p, magicNumber, true));

        // 2. 获得1层无实体
        addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1), 1));
    }

    @Override
    public void triggerWhenDrawn() {
        addToBot(new NewQueueCardAction(this, true, false, true));
    }
}