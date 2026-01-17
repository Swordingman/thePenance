package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import thePenance.PenanceMod;
import thePenance.character.Penance; // 请替换为你的角色枚举
import thePenance.powers.TemporaryConfusionPower;
import thePenance.util.CardStats;

public class WandAntiGravity extends BaseCard {

    public static final String ID = PenanceMod.makeID("WandAntiGravity");
    private static final int COST = 0;
    private static final int DRAW = 3;
    private static final int UPG_DRAW = 1;

    public WandAntiGravity() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.SELF,
                COST
        ));
        setMagic(DRAW, UPG_DRAW);
        setExhaust(true);
        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 施加混乱 (原版逻辑)
        addToBot(new ApplyPowerAction(p, p, new ConfusionPower(p)));

        // 2. 施加“回合结束移除混乱”的标记 (新逻辑)
        addToBot(new ApplyPowerAction(p, p, new TemporaryConfusionPower(p)));

        // 3. 抽牌
        addToBot(new DrawCardAction(magicNumber));
    }

    @Override
    public void triggerWhenDrawn() {
        // 直接调用 BaseCard 里的通用方法
        triggerWolfAutoplay();
    }
}