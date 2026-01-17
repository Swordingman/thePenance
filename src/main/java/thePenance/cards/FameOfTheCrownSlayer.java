package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;
import thePenance.util.CardStats;

public class FameOfTheCrownSlayer extends BaseCard {

    public static final String ID = makeID("FameOfTheCrownSlayer");
    private static final int COST = 1;
    private static final int TEMP_STR = 3;
    private static final int DRAW = 2;
    private static final int UPG_DRAW = 1; // 2->3

    public FameOfTheCrownSlayer() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL_ENEMY,
                COST
        ));
        setMagic(DRAW, UPG_DRAW);
        setExhaust(true);

        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 遍历所有敌人，给攻击意图的敌人加临时力量
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                // 判断意图是否为攻击
                if (mo.getIntentBaseDmg() >= 0) {
                    addToBot(new ApplyPowerAction(mo, p, new StrengthPower(mo, TEMP_STR), TEMP_STR));
                    // 临时力量 = 获得力量 + 获得“回合结束失去力量”
                    addToBot(new ApplyPowerAction(mo, p, new LoseStrengthPower(mo, TEMP_STR), TEMP_STR));
                }
            }
        }

        // 2. 抽牌
        addToBot(new DrawCardAction(p, magicNumber));
    }

    @Override
    public void triggerWhenDrawn() {
        // 直接调用 BaseCard 里的通用方法
        triggerWolfAutoplay();
    }
}